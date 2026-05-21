package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.OrderController;
import com.example.stylica_app.models.SubOrderModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.adapters.VendorOrdersAdapter;

import java.util.ArrayList;
import java.util.List;

public class VendorOrdersActivity extends BaseActivity {

    RecyclerView ordersView;
    ProgressBar loader;
    LinearLayout emptyView, chipGroup;

    OrderController orderController;
    SessionService sessionService;
    VendorOrdersAdapter adapter;

    List<SubOrderModel> allSubOrders = new ArrayList<>();
    String selectedStatus = "All";
    String role;

    String[] statuses = {"All", "Pending", "Confirmed", "Packed", "Shipped", "Delivered"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_orders);
        sessionService  = new SessionService(this);
        role = sessionService.getUserRole();

        if(role != null) {
            setupAppBar(role.equals("vendor") ? "Orders Management": "Orders Status");
        }

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
        for (String status : statuses) {
            TextView chip = new TextView(this);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            (int) (36 * getResources().getDisplayMetrics().density));
            params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
            chip.setLayoutParams(params);
            chip.setText(status);
            chip.setGravity(android.view.Gravity.CENTER);
            int px = (int) (16 * getResources().getDisplayMetrics().density);
            chip.setPadding(px, 0, px, 0);
            chip.setTextSize(13);

            if (status.equals(selectedStatus)) {
                chip.setBackgroundResource(R.drawable.chip_selected_bg);
                chip.setTextColor(getColor(R.color.text_white));
            } else {
                chip.setBackgroundResource(R.drawable.chip_unselected_bg);
                chip.setTextColor(getColor(R.color.text_secondary));
            }

            chip.setOnClickListener(v -> {
                selectedStatus = status;
                buildChips();
                filterAndShow();
            });

            chipGroup.addView(chip);
        }
    }

    private void loadOrders() {
        loading(true);

        String domain = sessionService.getDomain();
        String vendorId = sessionService.getUserId();
        if (domain == null || vendorId == null || domain.isEmpty() || vendorId.isEmpty()) {
            loading(false);
            showEmpty(true);
            Toast.makeText(this, "No domain assigned to your account",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(role != null && role.equals("vendor")) {
            orderController.getSubOrdersForVendor(domain,vendorId,
                    new DatabaseService.RealtimeCallback<List<SubOrderModel>>() {
                        @Override
                        public void onDataChange(List<SubOrderModel> data) {
                            loading(false);
                            allSubOrders = data != null ? data : new ArrayList<>();
                            filterAndShow();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            loading(false);
                            Log.e("VENDORORDERSACTIVIy ", errorMessage);
                            Toast.makeText(VendorOrdersActivity.this,
                                    "Failed to load orders", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            orderController.getSubOrdersForModerator(domain,
                    new DatabaseService.RealtimeCallback<List<SubOrderModel>>() {
                        @Override
                        public void onDataChange(List<SubOrderModel> data) {
                            Log.d("MODERATORORdErSDOMAIN", domain);
                            loading(false);
                            allSubOrders = data != null ? data : new ArrayList<>();
                            filterAndShow();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            loading(false);
                            Toast.makeText(VendorOrdersActivity.this,
                                    "Failed to load orders", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void filterAndShow() {
        List<SubOrderModel> filtered = new ArrayList<>();

        for (SubOrderModel subOrder : allSubOrders) {
            boolean statusMatch = selectedStatus.equals("All")
                    || (subOrder.getStatus() != null
                    && subOrder.getStatus().equalsIgnoreCase(selectedStatus));
            if (statusMatch) filtered.add(subOrder);
        }

        if (filtered.isEmpty()) {
            showEmpty(true);
            return;
        }

        showEmpty(false);

        adapter = new VendorOrdersAdapter(this, filtered,role,
                new VendorOrdersAdapter.OnStatusChangeListener() {

                    @Override
                    public void onConfirm(SubOrderModel subOrder, int position) {
                        orderController.updateSubOrderStatus(
                                subOrder.getSubOrderId(), "confirmed",
                                new OrderController.UpdateCallback() {
                                    @Override
                                    public void onSuccess() {
                                        subOrder.setStatus("confirmed");
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(VendorOrdersActivity.this,
                                                "Order confirmed", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(VendorOrdersActivity.this,
                                                "Failed: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onPack(SubOrderModel subOrder, int position) {
                        orderController.updateSubOrderStatus(
                                subOrder.getSubOrderId(), "packed",
                                new OrderController.UpdateCallback() {
                                    @Override
                                    public void onSuccess() {
                                        subOrder.setStatus("packed");
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(VendorOrdersActivity.this,
                                                "Marked as packed", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(VendorOrdersActivity.this,
                                                "Failed: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onShip(SubOrderModel subOrder, int position) {
                        orderController.updateSubOrderStatus(
                                subOrder.getSubOrderId(), "shipped",
                                new OrderController.UpdateCallback() {
                                    @Override
                                    public void onSuccess() {
                                        subOrder.setStatus("shipped");
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(VendorOrdersActivity.this,
                                                "Marked as shipped", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(VendorOrdersActivity.this,
                                                "Failed: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onDeliver(SubOrderModel subOrder, int position) {
                        orderController.updateSubOrderStatus(
                                subOrder.getSubOrderId(), "delivered",
                                new OrderController.UpdateCallback() {
                                    @Override
                                    public void onSuccess() {
                                        subOrder.setStatus("delivered");
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(VendorOrdersActivity.this,
                                                "Marked as delivered ✓", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(VendorOrdersActivity.this,
                                                "Failed: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
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