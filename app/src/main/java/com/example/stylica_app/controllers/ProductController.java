package com.example.stylica_app.controllers;

import android.content.Context;
import android.util.Log;

import com.example.stylica_app.helpers.CloudinaryHelper;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductController {
    FirebaseFirestore firestore;
    private static final String COLLECTION = "products";
    private static ProductController instance;
    DatabaseService<ProductModel> dbService;
    CategoryController categoryController;
    CloudinaryHelper cloudinaryHelper;

    SessionService sessionService;

    // 🔹 Delete Callback Interface
    public interface DeleteCallback {
        void onSuccess();
        void onFailure(String error);
    }

    // 🔹 Update Callback Interface
    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private ProductController(Context context) {
        firestore = FirebaseFirestore.getInstance();
        dbService = new DatabaseService<>(firestore);
        categoryController = CategoryController.getInstance();
        cloudinaryHelper = new CloudinaryHelper(context);
        sessionService = new SessionService(context);
    }

    public static ProductController getInstance(Context context) {
        if (instance == null) {
            instance = new ProductController(context);
        }
        return instance;
    }


    public void addProduct(String imageUrl, String productName, double productPrice, int stockQuantity,
                           String category, String subcategory, String description, String status,
                           String userId, String userName,boolean isNewArrival, boolean isFeatured, DatabaseService.DatabaseCallback callback) {

        String productId = FirebaseFirestore.getInstance().collection(COLLECTION).document().getId();

        ProductModel productModel = new ProductModel(
                productId, productName, description, category, subcategory,
                stockQuantity, productPrice, imageUrl, isFeatured, isNewArrival,
                status, userId, userName, null, 0
        );

        dbService.addRecord(COLLECTION, productId, productModel, callback);
    }


    public void getAllProducts(DatabaseService.RealtimeCallback callback) {
        Map conditions = new HashMap();
        String role = sessionService.getUserRole();
        String userId = sessionService.getUserId();

        conditions.put("status", "approved");

        if(role.equals("moderator")) {
            conditions.put("userId",userId);
        }
        dbService.listenWhere(COLLECTION, conditions, ProductModel.class, callback);
    }


    public void getProductById(String productId, DatabaseService.DatabaseCallback callback) {
        firestore.collection(COLLECTION)
                .document(productId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        ProductModel product = doc.toObject(ProductModel.class);
                        callback.onSuccess(product);
                        Log.d("ProductController", "Product fetched: " + productId);
                    } else {
                        callback.onFailure("Product not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    public void deleteProduct(String productId, DeleteCallback callback) {
        if (productId == null || productId.isEmpty()) {
            callback.onFailure("Invalid product ID");
            return;
        }

        firestore.collection(COLLECTION)
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("ProductController", "Product deleted: " + productId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductController", "Delete failed: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                });
    }


    public void updateProduct(String productId, String productName, double productPrice,
                              int stockQuantity, String category, String subcategory,
                              String description, String imageUrl,
                              boolean isNewArrival, boolean isFeatured,
                              DatabaseService.DatabaseCallback callback) {
        if (productId == null || productId.isEmpty()) {
            callback.onFailure("Invalid product ID");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("productName", productName);
        updates.put("price", productPrice);
        updates.put("stockQuantity", stockQuantity);
        updates.put("category", category);
        updates.put("subcategory", subcategory);
        updates.put("description", description);
        updates.put("isNew", isNewArrival);
        updates.put("isFeatured", isFeatured);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            updates.put("imageUrl", imageUrl);
        }

        dbService.updateRecord(COLLECTION,productId,updates, callback);

    }

    // Get pending products
    public void getPendingProducts(DatabaseService.RealtimeCallback callback) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("status", "pending");
        dbService.listenWhere(COLLECTION, conditions, ProductModel.class, callback);
    }

    // Approve product
    public void approveProduct(String productId, DatabaseService.DatabaseCallback callback) {
        Map<String, Object> update = new HashMap<>();
        update.put("status", "approved");
        dbService.updateRecord(COLLECTION, productId, update, callback);
    }

    // Reject product
    public void rejectProduct(String productId, DatabaseService.DatabaseCallback callback) {
        Map<String, Object> update = new HashMap<>();
        update.put("status", "rejected");
        dbService.updateRecord(COLLECTION, productId, update, callback);
    }
}