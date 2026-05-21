package com.example.stylica_app.views.activities;

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
import com.example.stylica_app.views.adapters.VendorRejectedProductsAdapter;

import java.util.ArrayList;
import java.util.List;

public class VendorRejectedProductsActivity extends BaseActivity {

    RecyclerView recyclerView;
    ProgressBar loader;
    LinearLayout emptyView;

    ProductController productController;
    SessionService sessionService;
    VendorRejectedProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_rejected_products);
        setupAppBar("Rejected Products");

        recyclerView      = findViewById(R.id.recyclerView);
        loader            = findViewById(R.id.loader);
        emptyView         = findViewById(R.id.emptyView);
        productController = ProductController.getInstance(this);
        sessionService    = new SessionService(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadRejectedProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRejectedProducts();
    }

    private void loadRejectedProducts() {
        loading(true);

        String userId = sessionService.getUserId();

        productController.getRejectedProductsForVendor(userId,
                new DatabaseService.DatabaseCallback<List<ProductModel>>() {
                    @Override
                    public void onSuccess(List<ProductModel> data) {
                        loading(false);

                        if (data == null || data.isEmpty()) {
                            showEmpty(true);
                            return;
                        }

                        showEmpty(false);
                        adapter = new VendorRejectedProductsAdapter(
                                VendorRejectedProductsActivity.this,
                                data,
                                product -> resubmitProduct(product));
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loading(false);
                        Toast.makeText(VendorRejectedProductsActivity.this,
                                "Failed to load rejected products",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resubmitProduct(ProductModel product) {
        productController.resubmitProduct(product.getProductId(),
                new DatabaseService.DatabaseCallback() {
                    @Override
                    public void onSuccess(Object data) {
                        Toast.makeText(VendorRejectedProductsActivity.this,
                                "Product resubmitted for review ✓",
                                Toast.LENGTH_SHORT).show();
                        loadRejectedProducts(); // refresh list
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(VendorRejectedProductsActivity.this,
                                "Failed: " + errorMessage,
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