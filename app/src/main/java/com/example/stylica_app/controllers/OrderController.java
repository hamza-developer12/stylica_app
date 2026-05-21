package com.example.stylica_app.controllers;

import com.example.stylica_app.models.CartModel;
import com.example.stylica_app.models.OrderModel;
import com.example.stylica_app.models.SubOrderModel;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderController {

    private static final String ORDERS_COLLECTION    = "orders";
    private static final String SUBORDERS_COLLECTION = "suborders";
    private static final String PRODUCTS_COLLECTION  = "products";

    private static OrderController instance;
    private final FirebaseFirestore firestore;

    DatabaseService dbService;



    private OrderController() {
        firestore = FirebaseFirestore.getInstance();

        dbService = new DatabaseService(firestore);
    }

    public static OrderController getInstance() {
        if (instance == null) instance = new OrderController();
        return instance;
    }

    // ─────────────────────────────────────────────────────────
    // PLACE ORDER
    // 1 Order + 1 SubOrder per unique cart item (unique product)
    // Both saved in one batch
    // ─────────────────────────────────────────────────────────
    public void placeOrder(OrderModel order, List<CartModel> cartItems,
                           String customerId, String customerName,
                           String deliveryDays,
                           PlaceOrderCallback callback) {

        String orderId = firestore.collection(ORDERS_COLLECTION).document().getId();
        order.setOrderId(orderId);

        WriteBatch batch = firestore.batch();

        // Save main Order
        DocumentReference orderRef = firestore
                .collection(ORDERS_COLLECTION).document(orderId);
        batch.set(orderRef, order);

        // 1 SubOrder per unique cart item
        for (CartModel item : cartItems) {
            String subOrderId = firestore
                    .collection(SUBORDERS_COLLECTION).document().getId();

            SubOrderModel subOrder = new SubOrderModel(
                    subOrderId,
                    orderId,
                    item.getProductName(),
                    item.getProductPrice(),
                    item.getQuantity(),
                    item.getTotalPrice(),
                    item.getProductImage(),
                    item.getCategory(),
                    "pending",
                    deliveryDays,
                    customerId,
                    customerName,
                    "pending",
                    item.getVendorId()
            );

            DocumentReference subOrderRef = firestore
                    .collection(SUBORDERS_COLLECTION).document(subOrderId);
            batch.set(subOrderRef, subOrder);
        }

        // Commit Order + all SubOrders atomically
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    reduceStock(cartItems);
                    callback.onSuccess(orderId);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    // ADMIN — Verify payment
    // Updates Order paymentStatus + all its SubOrders to "verified"

    public void verifyPayment(String orderId, UpdateCallback callback) {
        updatePaymentStatus(orderId, "verified", callback);
    }


    // ADMIN — Reject payment
    // Updates Order paymentStatus + all its SubOrders to "rejected"

    public void rejectPayment(String orderId, UpdateCallback callback) {
        updatePaymentStatus(orderId, "rejected", callback);
    }

    private void updatePaymentStatus(String orderId, String paymentStatus,
                                     UpdateCallback callback) {
        firestore.collection(SUBORDERS_COLLECTION)
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    WriteBatch batch = firestore.batch();

                    // Update main order
                    DocumentReference orderRef = firestore
                            .collection(ORDERS_COLLECTION).document(orderId);
                    batch.update(orderRef, "paymentStatus", paymentStatus);

                    // Update all suborders
                    snapshot.getDocuments().forEach(doc -> {
                        DocumentReference subRef = firestore
                                .collection(SUBORDERS_COLLECTION).document(doc.getId());
                        batch.update(subRef, "paymentStatus", paymentStatus);
                    });

                    batch.commit()
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ─────────────────────────────────────────────────────────
    // ADMIN — Get all Orders
    // ─────────────────────────────────────────────────────────
    public void getAllOrders(DatabaseService.DatabaseCallback<List<OrderModel>> callback) {
        firestore.collection(ORDERS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<OrderModel> list = snapshot.toObjects(OrderModel.class);
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    // MODERATOR — Get SubOrders for their domain (verified only)

    public void getSubOrdersForModerator(String domain,
                                         DatabaseService.RealtimeCallback<List<SubOrderModel>> callback) {
        Map conditions = new HashMap();
        conditions.put("domain", domain);
        conditions.put("paymentStatus", "verified");
        dbService.listenWhere(SUBORDERS_COLLECTION,conditions, SubOrderModel.class, callback);
    }
    public void getSubOrdersForVendor(String domain, String vendorId,
                                         DatabaseService.RealtimeCallback<List<SubOrderModel>> callback) {

        Map conditions = new HashMap();
        conditions.put("domain", domain);
        conditions.put("vendorId", vendorId);
        conditions.put("paymentStatus", "verified");
        dbService.listenWhere(SUBORDERS_COLLECTION, conditions, SubOrderModel.class, callback);

    }


    // MODERATOR — Update SubOrder status
    // confirmed → packed → shipped → delivered
    public void updateSubOrderStatus(String subOrderId, String status,
                                     UpdateCallback callback) {
        firestore.collection(SUBORDERS_COLLECTION)
                .document(subOrderId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ─────────────────────────────────────────────────────────
    // CUSTOMER — Get their SubOrders (flat list)
    // ─────────────────────────────────────────────────────────
    public void getSubOrdersForCustomer(String customerId,
                                        DatabaseService.DatabaseCallback<List<SubOrderModel>> callback) {
        firestore.collection(SUBORDERS_COLLECTION)
                .whereEqualTo("customerId", customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<SubOrderModel> list = snapshot.toObjects(SubOrderModel.class);
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ─────────────────────────────────────────────────────────
    // Reduce stock after order placed
    // ─────────────────────────────────────────────────────────
    private void reduceStock(List<CartModel> items) {
        if (items == null) return;
        for (CartModel item : items) {
            firestore.collection(PRODUCTS_COLLECTION)
                    .document(item.getProductId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            int currentStock = doc.getLong("stockQuantity").intValue();
                            int newStock = Math.max(0, currentStock - item.getQuantity());
                            firestore.collection(PRODUCTS_COLLECTION)
                                    .document(item.getProductId())
                                    .update("stockQuantity", newStock);
                        }
                    });
        }
    }

    public void updateOrder(String orderId, Map<String, Object> fields,
                            UpdateCallback callback) {
        firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .update(fields)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    public interface PlaceOrderCallback {
        void onSuccess(String orderId);
        void onFailure(String error);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }

}