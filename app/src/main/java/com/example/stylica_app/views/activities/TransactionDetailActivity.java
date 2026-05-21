package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.controllers.OrderController;
import com.example.stylica_app.helpers.CloudinaryHelper;
import com.example.stylica_app.models.OrderModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionDetailActivity extends BaseActivity {

    // Views
    TextView txtPaymentStatus;
    EditText edtCourierName, edtDeliveryCharges,
            edtGrandTotal, edtPaymentMethod;
    ImageView imgScreenshot;
    Button btnVerify, btnReject, btnSave, btnChangeScreenshot;
    ProgressBar loader, saveLoader;
    ScrollView contentLayout;

    // Data
    OrderController orderController;
    CloudinaryHelper cloudinaryHelper;
    OrderModel currentOrder;
    String orderId;
    Bitmap newScreenshotBitmap = null;
    String currentScreenshotUrl = null;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        setupAppBar("Transaction Detail");

        txtPaymentStatus    = findViewById(R.id.txtPaymentStatus);
        edtCourierName      = findViewById(R.id.edtCourierName);
        edtDeliveryCharges  = findViewById(R.id.edtDeliveryCharges);
        edtGrandTotal       = findViewById(R.id.edtGrandTotal);
        edtPaymentMethod    = findViewById(R.id.edtPaymentMethod);
        imgScreenshot       = findViewById(R.id.imgScreenshot);
        btnVerify           = findViewById(R.id.btnVerify);
        btnReject           = findViewById(R.id.btnReject);
        btnSave             = findViewById(R.id.btnSave);
        btnChangeScreenshot = findViewById(R.id.btnChangeScreenshot);
        loader              = findViewById(R.id.loader);
        saveLoader          = findViewById(R.id.saveLoader);
        contentLayout       = findViewById(R.id.contentLayout);

        orderController    = OrderController.getInstance();
        cloudinaryHelper   = new CloudinaryHelper(this);

        orderId = getIntent().getStringExtra("orderId");
        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOrder();
    }

    private void loadOrder() {
        loader.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        FirebaseFirestore.getInstance()
                .collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(doc -> {
                    loader.setVisibility(View.GONE);
                    if (doc.exists()) {
                        currentOrder = doc.toObject(OrderModel.class);
                        if (currentOrder != null) {
                            fillDetails(currentOrder);
                            contentLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "Order not found",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load order",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void fillDetails(OrderModel order) {
        // Payment status badge
        String ps = order.getPaymentStatus() != null
                ? order.getPaymentStatus() : "pending";
        txtPaymentStatus.setText(ps.substring(0, 1).toUpperCase()
                + ps.substring(1));
        setStatusColor(txtPaymentStatus, ps);

        // Show/hide verify+reject based on current status
        setupPaymentButtons(ps);

        // Editable fields
        edtCourierName.setText(order.getCourierName() != null
                ? order.getCourierName() : "");
        edtDeliveryCharges.setText(String.valueOf(order.getDeliveryCharges()));
        edtGrandTotal.setText(String.valueOf(order.getGrandTotal()));
        edtPaymentMethod.setText(order.getPaymentMethodName() != null
                ? order.getPaymentMethodName() : "");

        // Screenshot
        currentScreenshotUrl = order.getPaymentScreenshotUrl();
        if (currentScreenshotUrl != null && !currentScreenshotUrl.isEmpty()) {
            Glide.with(this)
                    .load(currentScreenshotUrl)
                    .placeholder(R.drawable.image_placeholder_bg)
                    .into(imgScreenshot);
            imgScreenshot.setOnClickListener(v -> {
                Intent i = new Intent(this, ImageViewerActivity.class);
                i.putExtra("imageUrl", currentScreenshotUrl);
                startActivity(i);
            });
        }

        // Change screenshot
        btnChangeScreenshot.setOnClickListener(v -> pickImage());

        // Save button
        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void setupPaymentButtons(String status) {
        if (status.equals("pending")) {
            btnVerify.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
        } else if (status.equals("verified")) {
            btnVerify.setVisibility(View.GONE);
            btnReject.setVisibility(View.VISIBLE); // can still reject a verified
        } else {
            // rejected
            btnVerify.setVisibility(View.VISIBLE); // can re-verify
            btnReject.setVisibility(View.GONE);
        }

        btnVerify.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Verify Payment")
                        .setMessage("Mark this payment as verified?")
                        .setPositiveButton("Verify", (d, w) ->
                                updatePaymentStatus("verified"))
                        .setNegativeButton("Cancel", null)
                        .show());

        btnReject.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Reject Payment")
                        .setMessage("Mark this payment as rejected?")
                        .setPositiveButton("Reject", (d, w) ->
                                updatePaymentStatus("rejected"))
                        .setNegativeButton("Cancel", null)
                        .show());
    }

    private void updatePaymentStatus(String status) {
        if (status.equals("verified")) {
            orderController.verifyPayment(orderId,
                    new OrderController.UpdateCallback() {
                        @Override
                        public void onSuccess() {
                            currentOrder.setPaymentStatus(status);
                            txtPaymentStatus.setText(
                                    status.substring(0, 1).toUpperCase()
                                            + status.substring(1));
                            setStatusColor(txtPaymentStatus, status);
                            setupPaymentButtons(status);
                            Toast.makeText(TransactionDetailActivity.this,
                                    "Payment verified ✓", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(TransactionDetailActivity.this,
                                    "Failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            orderController.rejectPayment(orderId,
                    new OrderController.UpdateCallback() {
                        @Override
                        public void onSuccess() {
                            currentOrder.setPaymentStatus(status);
                            txtPaymentStatus.setText(
                                    status.substring(0, 1).toUpperCase()
                                            + status.substring(1));
                            setStatusColor(txtPaymentStatus, status);
                            setupPaymentButtons(status);
                            Toast.makeText(TransactionDetailActivity.this,
                                    "Payment rejected", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(TransactionDetailActivity.this,
                                    "Failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                newScreenshotBitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), uri);
                Glide.with(this)
                        .load(uri)
                        .into(imgScreenshot);
                Toast.makeText(this, "Screenshot selected — save to upload",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateAndSave() {
        String courierName     = edtCourierName.getText().toString().trim();
        String deliveryStr     = edtDeliveryCharges.getText().toString().trim();
        String grandTotalStr   = edtGrandTotal.getText().toString().trim();
        String paymentMethod   = edtPaymentMethod.getText().toString().trim();

        if (courierName.isEmpty() || deliveryStr.isEmpty()
                || grandTotalStr.isEmpty() || paymentMethod.isEmpty()) {
            Toast.makeText(this, "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double deliveryCharges, grandTotal;
        try {
            deliveryCharges = Double.parseDouble(deliveryStr);
            grandTotal      = Double.parseDouble(grandTotalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        savingMode(true);

        // If new screenshot selected — upload first then save
        if (newScreenshotBitmap != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                try {
                    String newUrl = cloudinaryHelper.uploadBitmap(newScreenshotBitmap);
                    handler.post(() -> saveToFirestore(
                            courierName, deliveryCharges,
                            grandTotal, paymentMethod, newUrl));
                } catch (IOException e) {
                    handler.post(() -> {
                        savingMode(false);
                        Toast.makeText(this,
                                "Screenshot upload failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            // No new screenshot — save with existing URL
            saveToFirestore(courierName, deliveryCharges,
                    grandTotal, paymentMethod, currentScreenshotUrl);
        }
    }

    private void saveToFirestore(String courierName, double deliveryCharges,
                                 double grandTotal, String paymentMethod,
                                 String screenshotUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("courierName",          courierName);
        updates.put("deliveryCharges",      deliveryCharges);
        updates.put("grandTotal",           grandTotal);
        updates.put("paymentMethodName",    paymentMethod);
        updates.put("paymentScreenshotUrl", screenshotUrl);

        orderController.updateOrder(orderId, updates,
                new OrderController.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        savingMode(false);
                        newScreenshotBitmap  = null;
                        currentScreenshotUrl = screenshotUrl;
                        Toast.makeText(TransactionDetailActivity.this,
                                "Transaction updated ✓",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        savingMode(false);
                        Toast.makeText(TransactionDetailActivity.this,
                                "Failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void savingMode(boolean isSaving) {
        saveLoader.setVisibility(isSaving ? View.VISIBLE : View.GONE);
        btnSave.setVisibility(isSaving ? View.GONE : View.VISIBLE);
        btnVerify.setEnabled(!isSaving);
        btnReject.setEnabled(!isSaving);
    }

    private void setStatusColor(TextView view, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                view.setBackgroundColor(android.graphics.Color.parseColor("#FFA000"));
                break;
            case "verified":
                view.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"));
                break;
            case "rejected":
                view.setBackgroundColor(android.graphics.Color.parseColor("#D32F2F"));
                break;
            default:
                view.setBackgroundResource(R.drawable.chip_selected_bg);
        }
    }
}