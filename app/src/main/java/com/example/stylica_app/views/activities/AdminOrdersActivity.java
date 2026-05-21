package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.OrderController;
import com.example.stylica_app.models.OrderModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.adapters.AdminOrdersAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersActivity extends BaseActivity {

    RecyclerView ordersView;
    ProgressBar loader;
    LinearLayout emptyView, chipGroup;

    OrderController orderController;
    AdminOrdersAdapter adapter;

    List<OrderModel> allOrders = new ArrayList<>();
    String selectedFilter = "All";

    String[] filters = {"All", "Pending", "Verified", "Rejected"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);
        setupAppBar("Payment Verification");

        ordersView      = findViewById(R.id.ordersView);
        loader          = findViewById(R.id.loader);
        emptyView       = findViewById(R.id.emptyView);
        chipGroup       = findViewById(R.id.chipGroup);
        orderController = OrderController.getInstance();

        ordersView.setLayoutManager(new LinearLayoutManager(this));

        buildChips();
        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void buildChips() {
        chipGroup.removeAllViews();
        for (String filter : filters) {
            TextView chip = new TextView(this);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            (int) (36 * getResources().getDisplayMetrics().density));
            params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
            chip.setLayoutParams(params);
            chip.setText(filter);
            chip.setGravity(android.view.Gravity.CENTER);
            int px = (int) (16 * getResources().getDisplayMetrics().density);
            chip.setPadding(px, 0, px, 0);
            chip.setTextSize(13);

            if (filter.equals(selectedFilter)) {
                chip.setBackgroundResource(R.drawable.chip_selected_bg);
                chip.setTextColor(getColor(R.color.text_white));
            } else {
                chip.setBackgroundResource(R.drawable.chip_unselected_bg);
                chip.setTextColor(getColor(R.color.text_secondary));
            }

            chip.setOnClickListener(v -> {
                selectedFilter = filter;
                buildChips();
                filterAndShow();
            });

            chipGroup.addView(chip);
        }
    }

    private void loadOrders() {
        loading(true);

        orderController.getAllOrders(
                new DatabaseService.DatabaseCallback<List<OrderModel>>() {
                    @Override
                    public void onSuccess(List<OrderModel> data) {
                        loading(false);
                        allOrders = data != null ? data : new ArrayList<>();
                        filterAndShow();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loading(false);
                        Toast.makeText(AdminOrdersActivity.this,
                                "Failed to load orders",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterAndShow() {
        List<OrderModel> filtered = new ArrayList<>();

        for (OrderModel order : allOrders) {
            boolean match = selectedFilter.equals("All")
                    || (order.getPaymentStatus() != null
                    && order.getPaymentStatus().equalsIgnoreCase(selectedFilter));
            if (match) filtered.add(order);
        }

        if (filtered.isEmpty()) {
            showEmpty(true);
            return;
        }

        showEmpty(false);
        adapter = new AdminOrdersAdapter(this, filtered,
                new AdminOrdersAdapter.OrderActionListener() {

                    @Override
                    public void onVerify(OrderModel order, int position) {
                        orderController.verifyPayment(order.getOrderId(),
                                new OrderController.UpdateCallback() {
                                    @Override
                                    public void onSuccess() {
                                        order.setPaymentStatus("verified");
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(AdminOrdersActivity.this,
                                                "Payment verified ✓",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(AdminOrdersActivity.this,
                                                "Failed: " + error,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onReject(OrderModel order, int position) {
                        orderController.rejectPayment(order.getOrderId(),
                                new OrderController.UpdateCallback() {
                                    @Override
                                    public void onSuccess() {
                                        order.setPaymentStatus("rejected");
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(AdminOrdersActivity.this,
                                                "Payment rejected",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(AdminOrdersActivity.this,
                                                "Failed: " + error,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onViewScreenshot(String url) {
                        android.content.Intent i = new android.content.Intent(
                                AdminOrdersActivity.this, ImageViewerActivity.class);
                        i.putExtra("imageUrl", url);
                        startActivity(i);
                    }
                });

        ordersView.setAdapter(adapter);
    }

    private void loading(boolean isLoading) {
        loader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            ordersView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmpty(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        ordersView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}