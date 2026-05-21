package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.controllers.OrderController;
import com.example.stylica_app.models.OrderModel;
import com.example.stylica_app.models.SubOrderModel;
import com.example.stylica_app.services.SessionService;
import com.google.firebase.firestore.FirebaseFirestore;

public class VendorOrderDetailActivity extends BaseActivity {

    SessionService sessionService;
    TextView txtStatus, txtCustomerName, txtProduct,
            txtQuantity, txtTotal, txtCourier,
            txtDeliveryDays, txtDomain;
    ImageView imgScreenshot, imgProduct;
    Button btnConfirm, btnPack, btnShip, btnDeliver;
    ProgressBar loader;
    ScrollView contentLayout;
    LinearLayout actionBtnContainer;

    OrderController orderController;
    SubOrderModel currentSubOrder;
    String subOrderId, orderId;
    String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_order_detail);
        sessionService = new SessionService(this);

        role = sessionService.getUserRole();
        setupAppBar("Order Detail");

        txtStatus = findViewById(R.id.txtStatus);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtProduct = findViewById(R.id.txtProduct);
        txtQuantity = findViewById(R.id.txtQuantity);
        txtTotal = findViewById(R.id.txtTotal);
        txtCourier = findViewById(R.id.txtCourier);
        txtDeliveryDays = findViewById(R.id.txtDeliveryDays);
        txtDomain = findViewById(R.id.txtDomain);
        imgScreenshot = findViewById(R.id.imgScreenshot);
        imgProduct = findViewById(R.id.imgProduct);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnPack = findViewById(R.id.btnPack);
        btnShip = findViewById(R.id.btnShip);
        btnDeliver = findViewById(R.id.btnDeliver);
        loader = findViewById(R.id.loader);
        contentLayout = findViewById(R.id.contentLayout);
        actionBtnContainer = findViewById(R.id.actionBtnContainer);

        orderController = OrderController.getInstance();
        subOrderId = getIntent().getStringExtra("subOrderId");
        orderId = getIntent().getStringExtra("orderId");

        if (subOrderId == null || orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSubOrder();
    }

    private void loadSubOrder() {
        loader.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        FirebaseFirestore.getInstance()
                .collection("suborders")
                .document(subOrderId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentSubOrder = doc.toObject(SubOrderModel.class);
                        if (currentSubOrder != null) {
                            fillSubOrderDetails(currentSubOrder);
                            setupActionButtons(currentSubOrder.getStatus());
                            loadParentOrder(); // fetch screenshot + courier
                        }
                    } else {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(this, "Order not found",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load order",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadParentOrder() {
        FirebaseFirestore.getInstance()
                .collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(doc -> {
                    loader.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                    if (doc.exists()) {
                        OrderModel order = doc.toObject(OrderModel.class);
                        if (order != null) fillOrderDetails(order);
                    }
                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                });
    }

    private void fillSubOrderDetails(SubOrderModel subOrder) {
        // Status
        String status = subOrder.getStatus() != null
                ? subOrder.getStatus() : "pending";
        txtStatus.setText(status.substring(0, 1).toUpperCase()
                + status.substring(1));
        setStatusColor(txtStatus, status);

        // Customer
        txtCustomerName.setText(subOrder.getCustomerName() != null
                ? subOrder.getCustomerName() : "—");

        // Product
        txtProduct.setText(subOrder.getProductName() != null
                ? subOrder.getProductName() : "—");
        txtQuantity.setText("Qty: " + subOrder.getQuantity());
        txtTotal.setText("Rs " + subOrder.getTotalPrice());
        txtDomain.setText("Category: " + (subOrder.getDomain() != null
                ? subOrder.getDomain() : "—"));
        txtDeliveryDays.setText("Delivery: " + (subOrder.getDeliveryDays() != null
                ? subOrder.getDeliveryDays() + " days" : "—"));

        // Product image
        if (subOrder.getProductImageUrl() != null
                && !subOrder.getProductImageUrl().isEmpty()) {
            imgProduct.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(subOrder.getProductImageUrl())
                    .placeholder(R.drawable.image_placeholder_bg)
                    .into(imgProduct);
        } else {
            imgProduct.setVisibility(View.GONE);
        }
    }

    private void fillOrderDetails(OrderModel order) {
        // Courier from parent order
        txtCourier.setText(order.getCourierName() != null
                ? order.getCourierName() : "—");

        // Payment screenshot from parent order
        if (order.getPaymentScreenshotUrl() != null
                && !order.getPaymentScreenshotUrl().isEmpty()) {
            imgScreenshot.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(order.getPaymentScreenshotUrl())
                    .placeholder(R.drawable.image_placeholder_bg)
                    .into(imgScreenshot);
            imgScreenshot.setOnClickListener(v -> {
                Intent i = new Intent(this, ImageViewerActivity.class);
                i.putExtra("imageUrl", order.getPaymentScreenshotUrl());
                startActivity(i);
            });
        } else {
            imgScreenshot.setVisibility(View.GONE);
        }
    }

    private void setupActionButtons(String status) {
        if (status == null) status = "pending";

        // Hide all first
        btnConfirm.setVisibility(View.GONE);
        btnPack.setVisibility(View.GONE);
        btnShip.setVisibility(View.GONE);
        btnDeliver.setVisibility(View.GONE);

        if(role.equals("vendor")) {
            switch (status.toLowerCase()) {
                case "pending":
                    btnConfirm.setVisibility(View.VISIBLE);
                    btnConfirm.setOnClickListener(v ->
                            showDialog("Confirm Order",
                                    "Confirm this order?", "confirmed"));
                    break;
                case "confirmed":
                    btnPack.setVisibility(View.VISIBLE);
                    btnPack.setOnClickListener(v ->
                            showDialog("Mark as Packed",
                                    "Mark this order as packed?", "packed"));
                    break;
                case "packed":
                    btnShip.setVisibility(View.VISIBLE);
                    btnShip.setOnClickListener(v ->
                            showDialog("Mark as Shipped",
                                    "Mark this order as shipped?", "shipped"));
                    break;
                case "shipped":
                    btnDeliver.setVisibility(View.VISIBLE);
                    btnDeliver.setOnClickListener(v ->
                            showDialog("Mark as Delivered",
                                    "Mark this order as delivered?", "delivered"));
                    break;
                // delivered — no buttons
            }
        }else {
            actionBtnContainer.setVisibility(View.GONE);
        }
    }

    private void showDialog(String title, String message, String newStatus) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) ->
                        updateStatus(newStatus))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateStatus(String newStatus) {
        orderController.updateSubOrderStatus(subOrderId, newStatus,
                new OrderController.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        currentSubOrder.setStatus(newStatus);
                        txtStatus.setText(newStatus.substring(0, 1).toUpperCase()
                                + newStatus.substring(1));
                        setStatusColor(txtStatus, newStatus);
                        setupActionButtons(newStatus);
                        Toast.makeText(VendorOrderDetailActivity.this,
                                "Status updated to " + newStatus + " ✓",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(VendorOrderDetailActivity.this,
                                "Failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setStatusColor(TextView view, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                view.setBackgroundColor(Color.parseColor("#FFA000"));
                break;
            case "confirmed":
                view.setBackgroundColor(Color.parseColor("#1976D2"));
                break;
            case "packed":
                view.setBackgroundColor(Color.parseColor("#7B1FA2"));
                break;
            case "shipped":
                view.setBackgroundColor(Color.parseColor("#0288D1"));
                break;
            case "delivered":
                view.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            default:
                view.setBackgroundResource(R.drawable.chip_selected_bg);
        }
    }
}