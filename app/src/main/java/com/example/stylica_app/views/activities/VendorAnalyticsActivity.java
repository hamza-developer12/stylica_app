package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stylica_app.R;
import com.example.stylica_app.services.SessionService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VendorAnalyticsActivity extends BaseActivity {

    // SubOrders
    TextView txtTotalSubOrders, txtTotalRevenue,
            txtConfirmed, txtPacked,
            txtShipped, txtDelivered;

    // Products
    TextView txtApprovedProducts, txtPendingProducts, txtRejectedProducts;

    ProgressBar loader;

    FirebaseFirestore firestore;
    SessionService sessionService;
    String userId;
    String domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_analytics);
        setupAppBar("My Analytics");

        firestore = FirebaseFirestore.getInstance();
        sessionService = new SessionService(this);
        userId = sessionService.getUserId();
        domain = sessionService.getDomain();
        txtTotalSubOrders = findViewById(R.id.txtTotalSubOrders);
        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);
        txtConfirmed = findViewById(R.id.txtConfirmed);
        txtPacked = findViewById(R.id.txtPacked);
        txtShipped = findViewById(R.id.txtShipped);
        txtDelivered = findViewById(R.id.txtDelivered);
        txtApprovedProducts = findViewById(R.id.txtApprovedProducts);
        txtPendingProducts  = findViewById(R.id.txtPendingProducts);
        txtRejectedProducts = findViewById(R.id.txtRejectedProducts);
        loader = findViewById(R.id.loader);

        loadAnalytics();
    }

    private void loadAnalytics() {
        loader.setVisibility(View.VISIBLE);
        loadSubOrderStats();
        loadProductStats();
    }

    private void loadSubOrderStats() {


        firestore.collection("products")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(productSnap -> {
                    if (productSnap.isEmpty()) {
                        loader.setVisibility(View.GONE);
                        txtTotalSubOrders.setText("0");
                        txtTotalRevenue.setText("Rs 0");
                        return;
                    }

                    // Collect all product names from this vendor
                   List<String> productNames = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : productSnap) {
                        String name = doc.getString("productName");
                        if (name != null) productNames.add(name);
                    }

                    // Query suborders matching those product names
                    firestore.collection("suborders")
                            .whereEqualTo("vendorId", userId)
                            .whereEqualTo("domain", domain)
//                            .whereIn("productName", productNames.size() > 10
//                                    ? productNames.subList(0, 10)
//                                    : productNames)
                            .get()
                            .addOnSuccessListener(subSnap -> {
                                loader.setVisibility(View.GONE);
                                int total = 0, confirmed = 0, packed = 0,
                                        shipped = 0, delivered = 0;
                                double revenue = 0;

                                for (QueryDocumentSnapshot doc : subSnap) {
                                    total++;
                                    String status = doc.getString("status");
                                    Double price  = doc.getDouble("totalPrice");
                                    if (price != null) revenue += price;
                                    if (status == null) continue;
                                    switch (status) {
                                        case "confirmed": confirmed++; break;
                                        case "packed":    packed++;    break;
                                        case "shipped":   shipped++;   break;
                                        case "delivered": delivered++; break;
                                    }
                                }

                                txtTotalSubOrders.setText(String.valueOf(total));
                                txtTotalRevenue.setText("Rs " +
                                        String.format("%.0f", revenue));
                                txtConfirmed.setText(String.valueOf(confirmed));
                                txtPacked.setText(String.valueOf(packed));
                                txtShipped.setText(String.valueOf(shipped));
                                txtDelivered.setText(String.valueOf(delivered));
                            })
                            .addOnFailureListener(e -> {
                                loader.setVisibility(View.GONE);
                                Toast.makeText(this, "Failed to load order stats",
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load stats",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProductStats() {
        firestore.collection("products")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    int approved = 0, pending = 0, rejected = 0;
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String status = doc.getString("status");
                        if (status == null) continue;
                        switch (status) {
                            case "approved": approved++; break;
                            case "pending":  pending++;  break;
                            case "rejected": rejected++; break;
                        }
                    }
                    txtApprovedProducts.setText(String.valueOf(approved));
                    txtPendingProducts.setText(String.valueOf(pending));
                    txtRejectedProducts.setText(String.valueOf(rejected));
                })
                .addOnFailureListener(e -> {});
    }
}