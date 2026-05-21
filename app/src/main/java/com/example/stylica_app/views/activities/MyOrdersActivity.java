package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.OrderController;
import com.example.stylica_app.models.SubOrderModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.adapters.MyOrdersAdapter;

import java.util.List;

public class MyOrdersActivity extends BaseActivity {

    RecyclerView ordersView;
    ProgressBar loader;
    LinearLayout emptyView;

    OrderController orderController;
    SessionService sessionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        setupAppBar("My Orders");

        ordersView      = findViewById(R.id.ordersView);
        loader          = findViewById(R.id.loader);
        emptyView       = findViewById(R.id.emptyView);
        orderController = OrderController.getInstance();
        sessionService  = new SessionService(this);

        ordersView.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        loading(true);

        String customerId = sessionService.getUserId();

        orderController.getSubOrdersForCustomer(customerId,
                new DatabaseService.DatabaseCallback<List<SubOrderModel>>() {
                    @Override
                    public void onSuccess(List<SubOrderModel> data) {
                        loading(false);

                        if (data == null || data.isEmpty()) {
                            showEmpty(true);
                            return;
                        }

                        showEmpty(false);
                        MyOrdersAdapter adapter =
                                new MyOrdersAdapter(MyOrdersActivity.this, data);
                        ordersView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loading(false);
                        Toast.makeText(MyOrdersActivity.this,
                                "Failed to load orders",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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