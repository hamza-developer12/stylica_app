package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.OrderController;
import com.example.stylica_app.models.SubOrderModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.adapters.ModeratorOrdersAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ModeratorOrdersActivity extends BaseActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout emptyView;
    ChipGroup chipGroup;

    OrderController orderController;
    SessionService sessionService;
    ModeratorOrdersAdapter adapter;

    List<SubOrderModel> allOrders = new ArrayList<>();
    String selectedStatus = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator_orders);

        setupAppBar("Order Tracking");

        recyclerView  = findViewById(R.id.recyclerView);
        progressBar   = findViewById(R.id.progressBar);
        emptyView     = findViewById(R.id.emptyView);
        chipGroup     = findViewById(R.id.chipGroup);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderController = OrderController.getInstance();
        sessionService  = new SessionService(this);

        // Select "All" chip by default
        ((Chip) findViewById(R.id.chipAll)).setChecked(true);

        setupChips();
        loadOrders();
    }

    private void setupChips() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int id = checkedIds.get(0);
            if      (id == R.id.chipAll)       selectedStatus = "All";
            else if (id == R.id.chipConfirmed) selectedStatus = "confirmed";
            else if (id == R.id.chipPacked)    selectedStatus = "packed";
            else if (id == R.id.chipShipped)   selectedStatus = "shipped";
            else if (id == R.id.chipDelivered) selectedStatus = "delivered";

            applyFilter();
        });
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        String domain = sessionService.getDomain();

        orderController.getSubOrdersForModerator(domain,
                new DatabaseService.RealtimeCallback<List<SubOrderModel>>() {
                    @Override
                    public void onDataChange(List<SubOrderModel> data) {
                        progressBar.setVisibility(View.GONE);
                        allOrders = data;
                        applyFilter();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("MODERATORORDERSACTIVItysdfa", errorMessage);
                        Toast.makeText(ModeratorOrdersActivity.this,
                                "Failed to load orders: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyFilter() {
        List<SubOrderModel> filtered = new ArrayList<>();

        for (SubOrderModel order : allOrders) {
            if (selectedStatus.equals("All") ||
                    order.getStatus().equalsIgnoreCase(selectedStatus)) {
                filtered.add(order);
            }
        }

        if (filtered.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

//        adapter = new ModeratorOrdersAdapter(this, filtered, subOrder -> {
//            Intent intent = new Intent(this, ModeratorOrderDetailActivity.class);
//            intent.putExtra("subOrderId", subOrder.getSubOrderId());
//            startActivity(intent);
//        });
        recyclerView.setAdapter(adapter);
    }
}