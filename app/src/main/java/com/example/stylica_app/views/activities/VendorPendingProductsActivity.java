package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.ProductController;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.adapters.VendorPendingProductsAdapter;

import java.util.ArrayList;
import java.util.List;

public class VendorPendingProductsActivity extends BaseActivity {

    RecyclerView recyclerView;
    ProgressBar loader;
    LinearLayout emptyView;

    ProductController productController;
    SessionService sessionService;
    VendorPendingProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_pending_products);
        setupAppBar("Pending Products");

        recyclerView      = findViewById(R.id.recyclerView);
        loader            = findViewById(R.id.loader);
        emptyView         = findViewById(R.id.emptyView);
        productController = ProductController.getInstance(this);
        sessionService    = new SessionService(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPendingProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPendingProducts();
    }

    private void loadPendingProducts() {
        loading(true);

        String userId = sessionService.getUserId();

        productController.getPendingProductsForVendor(userId,
                new DatabaseService.DatabaseCallback<List<ProductModel>>() {
                    @Override
                    public void onSuccess(List<ProductModel> data) {
                        loading(false);

                        if (data == null || data.isEmpty()) {
                            showEmpty(true);
                            return;
                        }

                        showEmpty(false);
                        adapter = new VendorPendingProductsAdapter(
                                VendorPendingProductsActivity.this,
                                data,
                                product -> {
                                    // Edit product
                                    Intent i = new Intent(
                                            VendorPendingProductsActivity.this,
                                            EditProductActivity.class);
                                    i.putExtra("productId", product.getProductId());
                                    startActivity(i);
                                });
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loading(false);
                        Toast.makeText(VendorPendingProductsActivity.this,
                                "Failed to load pending products",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loading(boolean isLoading) {
        loader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmpty(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}