package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.ProductController;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.adapters.PendingProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class PendingProductsActivity extends BaseActivity {

    RecyclerView recyclerView;
    ProgressBar loader;
    LinearLayout emptyView;

    ProductController productController;
    PendingProductAdapter adapter;
    List<ProductModel> pendingProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAppBar("Pending Products");

        recyclerView     = findViewById(R.id.pendingProductsView);
        loader           = findViewById(R.id.loader);
        emptyView        = findViewById(R.id.emptyView);
        productController = ProductController.getInstance(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPendingProducts();
    }

    private void loadPendingProducts() {
        loading(true);

        productController.getPendingProducts(
                new DatabaseService.RealtimeCallback<List<ProductModel>>() {
                    @Override
                    public void onDataChange(List<ProductModel> data) {
                        loading(false);

                        if (data == null || data.isEmpty()) {
                            showEmpty(true);
                            return;
                        }

                        showEmpty(false);
                        pendingProducts = data;

                        // Set adapter with approve/reject callbacks
                        adapter = new PendingProductAdapter(
                                PendingProductsActivity.this,
                                pendingProducts,
                                new PendingProductAdapter.OnActionListener() {

                                    @Override
                                    public void onApprove(ProductModel product, int position) {
                                        productController.approveProduct(
                                                product.getProductId(),
                                                new DatabaseService.DatabaseCallback<String>() {
                                                    @Override
                                                    public void onSuccess(String data) {
                                                        adapter.removeItem(position);
                                                        Toast.makeText(
                                                                PendingProductsActivity.this,
                                                                "Product Approved",
                                                                Toast.LENGTH_SHORT).show();
                                                        // Show empty if no more products
                                                        if (adapter.getItemCount() == 0) {
                                                            showEmpty(true);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(String errorMessage) {
                                                        Toast.makeText(
                                                                PendingProductsActivity.this,
                                                                "Failed: " + errorMessage,
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onReject(ProductModel product, int position) {
                                        productController.rejectProduct(
                                                product.getProductId(),
                                                new DatabaseService.DatabaseCallback<String>() {
                                                    @Override
                                                    public void onSuccess(String data) {
                                                        adapter.removeItem(position);
                                                        Toast.makeText(
                                                                PendingProductsActivity.this,
                                                                "Product Rejected",
                                                                Toast.LENGTH_SHORT).show();
                                                        if (adapter.getItemCount() == 0) {
                                                            showEmpty(true);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(String errorMessage) {
                                                        Toast.makeText(
                                                                PendingProductsActivity.this,
                                                                "Failed: " + errorMessage,
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });

                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loading(false);
                        Toast.makeText(PendingProductsActivity.this,
                                "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            loader.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        } else {
            loader.setVisibility(View.GONE);
        }
    }

    private void showEmpty(boolean isEmpty) {
        if (isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}