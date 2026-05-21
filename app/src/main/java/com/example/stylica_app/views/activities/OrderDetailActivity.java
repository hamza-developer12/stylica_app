package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.models.OrderModel;
import com.example.stylica_app.models.SubOrderModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderDetailActivity extends BaseActivity {

    TextView txtStatus, txtProductName, txtQuantity,
            txtTotal, txtCourier, txtDeliveryDays,
            txtDomain, txtPaymentStatus;
    ImageView imgScreenshot, imgProduct;
    ProgressBar loader;
    ScrollView contentLayout;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setupAppBar("Order Detail");

        txtStatus        = findViewById(R.id.txtStatus);
        txtProductName   = findViewById(R.id.txtProductName);
        txtQuantity      = findViewById(R.id.txtQuantity);
        txtTotal         = findViewById(R.id.txtTotal);
        txtCourier       = findViewById(R.id.txtCourier);
        txtDeliveryDays  = findViewById(R.id.txtDeliveryDays);
        txtDomain        = findViewById(R.id.txtDomain);
        txtPaymentStatus = findViewById(R.id.txtPaymentStatus);
        imgScreenshot    = findViewById(R.id.imgScreenshot);
        imgProduct       = findViewById(R.id.imgProduct);
        loader           = findViewById(R.id.loader);
        contentLayout    = findViewById(R.id.contentLayout);

        firestore = FirebaseFirestore.getInstance();

        String subOrderId = getIntent().getStringExtra("subOrderId");
        String orderId    = getIntent().getStringExtra("orderId");

        if (subOrderId == null || orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSubOrder(subOrderId, orderId);
    }

    private void loadSubOrder(String subOrderId, String orderId) {
        loader.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        // Load SubOrder first
        firestore.collection("suborders").document(subOrderId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        SubOrderModel subOrder = doc.toObject(SubOrderModel.class);
                        if (subOrder != null) {
                            fillSubOrderDetails(subOrder);
                            // Then load parent Order for screenshot + courier
                            loadParentOrder(orderId);
                        }
                    } else {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(this, "SubOrder not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load order", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadParentOrder(String orderId) {
        firestore.collection("orders").document(orderId)
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
        // Fulfillment status
        String paymentStatus = subOrder.getPaymentStatus() != null
                ? subOrder.getPaymentStatus() : "pending";
        String fulfillmentStatus = subOrder.getStatus() != null
                ? subOrder.getStatus() : "pending";

        // Show paymentStatus if not verified yet, else show fulfillment status
        String displayStatus = paymentStatus.equals("verified")
                ? fulfillmentStatus : paymentStatus;

        txtStatus.setText(displayStatus.substring(0, 1).toUpperCase()
                + displayStatus.substring(1));
        setStatusColor(txtStatus, displayStatus);

        // Payment status badge
        txtPaymentStatus.setText("Payment: " + paymentStatus.toUpperCase());
        setStatusColor(txtPaymentStatus, paymentStatus);

        // Product info
        txtProductName.setText(subOrder.getProductName() != null
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
        // Courier
        txtCourier.setText(order.getCourierName() != null
                ? order.getCourierName() : "—");

        // Payment screenshot
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

    private void setStatusColor(TextView view, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                view.setBackgroundColor(Color.parseColor("#FFA000"));
                break;
            case "verified":
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
            case "rejected":
                view.setBackgroundColor(Color.parseColor("#D32F2F"));
                break;
            default:
                view.setBackgroundResource(R.drawable.chip_selected_bg);
        }
    }
}