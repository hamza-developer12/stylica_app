package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stylica_app.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdminAnalyticsActivity extends BaseActivity {

    // Orders
    TextView txtTotalOrders, txtTotalRevenue,
            txtPendingPayments, txtVerifiedPayments, txtRejectedPayments;

    // SubOrder statuses
    TextView txtConfirmed, txtPacked, txtShipped, txtDelivered;

    // Users
    TextView txtTotalVendors, txtTotalCustomers,
            txtTotalModerators, txtTotalProducts;

    ProgressBar loader;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_analytics);
        setupAppBar("Analytics");

        firestore = FirebaseFirestore.getInstance();

        txtTotalOrders      = findViewById(R.id.txtTotalOrders);
        txtTotalRevenue     = findViewById(R.id.txtTotalRevenue);
        txtPendingPayments  = findViewById(R.id.txtPendingPayments);
        txtVerifiedPayments = findViewById(R.id.txtVerifiedPayments);
        txtRejectedPayments = findViewById(R.id.txtRejectedPayments);
        txtConfirmed        = findViewById(R.id.txtConfirmed);
        txtPacked           = findViewById(R.id.txtPacked);
        txtShipped          = findViewById(R.id.txtShipped);
        txtDelivered        = findViewById(R.id.txtDelivered);
        txtTotalVendors     = findViewById(R.id.txtTotalVendors);
        txtTotalCustomers   = findViewById(R.id.txtTotalCustomers);
        txtTotalModerators  = findViewById(R.id.txtTotalModerators);
        txtTotalProducts    = findViewById(R.id.txtTotalProducts);
        loader              = findViewById(R.id.loader);

        loadAnalytics();
    }

    private void loadAnalytics() {
        loader.setVisibility(View.VISIBLE);
        loadOrderStats();
        loadSubOrderStats();
        loadUserStats();
        loadProductStats();
    }

    private void loadOrderStats() {
        firestore.collection("orders").get()
                .addOnSuccessListener(snapshot -> {
                    int total = 0, pending = 0, verified = 0, rejected = 0;
                    double revenue = 0;

                    for (QueryDocumentSnapshot doc : snapshot) {
                        total++;
                        String ps = doc.getString("paymentStatus");
                        Double grand = doc.getDouble("grandTotal");
                        if (grand != null) revenue += grand;

                        if ("pending".equals(ps))  pending++;
                        else if ("verified".equals(ps)) { verified++; }
                        else if ("rejected".equals(ps)) rejected++;
                    }

                    txtTotalOrders.setText(String.valueOf(total));
                    txtTotalRevenue.setText("Rs " + String.format("%.0f", revenue));
                    txtPendingPayments.setText(String.valueOf(pending));
                    txtVerifiedPayments.setText(String.valueOf(verified));
                    txtRejectedPayments.setText(String.valueOf(rejected));
                    loader.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load order stats",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadSubOrderStats() {
        firestore.collection("suborders").get()
                .addOnSuccessListener(snapshot -> {
                    int confirmed = 0, packed = 0, shipped = 0, delivered = 0;
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String status = doc.getString("status");
                        if (status == null) continue;
                        switch (status) {
                            case "confirmed": confirmed++; break;
                            case "packed":    packed++;    break;
                            case "shipped":   shipped++;   break;
                            case "delivered": delivered++; break;
                        }
                    }
                    txtConfirmed.setText(String.valueOf(confirmed));
                    txtPacked.setText(String.valueOf(packed));
                    txtShipped.setText(String.valueOf(shipped));
                    txtDelivered.setText(String.valueOf(delivered));
                })
                .addOnFailureListener(e -> {});
    }

    private void loadUserStats() {
        firestore.collection("users").get()
                .addOnSuccessListener(snapshot -> {
                    int vendors = 0, customers = 0, moderators = 0;
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String role = doc.getString("role");
                        if (role == null) continue;
                        switch (role) {
                            case "vendor":    vendors++;    break;
                            case "customer":  customers++;  break;
                            case "moderator": moderators++; break;
                        }
                    }
                    txtTotalVendors.setText(String.valueOf(vendors));
                    txtTotalCustomers.setText(String.valueOf(customers));
                    txtTotalModerators.setText(String.valueOf(moderators));
                })
                .addOnFailureListener(e -> {});
    }

    private void loadProductStats() {
        firestore.collection("products")
                .whereEqualTo("status", "approved")
                .get()
                .addOnSuccessListener(snapshot ->
                        txtTotalProducts.setText(String.valueOf(snapshot.size())))
                .addOnFailureListener(e -> {});
    }
}