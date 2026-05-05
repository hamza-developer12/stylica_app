package com.example.stylica_app.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.stylica_app.views.adapters.ProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductsViewActivity extends BaseActivity {

    FloatingActionButton fabAddProduct;
    RecyclerView productsView;
    ProductController productController;
    ProgressBar loader;
    List<ProductModel> products = new ArrayList<>();
    EditText searchProduct;
    LinearLayout chipGroup;  // dynamic chip container

    String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAppBar("Products");

        fabAddProduct = findViewById(R.id.fabAddProduct);
        productsView = findViewById(R.id.productsView);
        productsView.setLayoutManager(new LinearLayoutManager(this));
        loader = findViewById(R.id.loader);
        productController = ProductController.getInstance(this);
        searchProduct = findViewById(R.id.searchProduct);
        chipGroup = findViewById(R.id.chipGroup);

        getProducts();

        fabAddProduct.setOnClickListener(v -> {
            Intent i = new Intent(ProductsViewActivity.this, AddProductActivity.class);
            startActivity(i);
        });

        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // ✅ Build chips dynamically from product categories
    @SuppressLint("ResourceAsColor")
    private void buildCategoryChips(List<ProductModel> productList) {
        chipGroup.removeAllViews(); // clear old chips

        // Collect unique categories
        Set<String> categoriesSet = new HashSet<>();
        for (ProductModel p : productList) {
            if (p.getCategory() != null && !p.getCategory().isEmpty()) {
                categoriesSet.add(p.getCategory());
            }
        }

        // Always add "All" first
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.addAll(categoriesSet);

        // Create a chip TextView for each category
        for (String category : categories) {
            TextView chip = new TextView(this);

            // Layout params with margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) (36 * getResources().getDisplayMetrics().density) // 36dp
            );
            params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density)); // 8dp margin
            chip.setLayoutParams(params);

            chip.setText(category);
            chip.setGravity(android.view.Gravity.CENTER);

            int px18 = (int) (18 * getResources().getDisplayMetrics().density);
            chip.setPadding(px18, 0, px18, 0);
            chip.setTextSize(13);

            // Style: selected = pink, unselected = white with border
            if (category.equals(selectedCategory)) {
                chip.setBackgroundResource(R.drawable.chip_selected_bg);
                chip.setTextColor(getColor(R.color.text_white));
            } else {
                chip.setBackgroundResource(R.drawable.chip_unselected_bg);
                chip.setTextColor(R.color.text_secondary);
            }

            chip.setOnClickListener(v -> {
                selectedCategory = category;
                buildCategoryChips(products); // rebuild chips to update selected state
                filterProducts(searchProduct.getText().toString());
            });

            chipGroup.addView(chip);
        }
    }

    private void getProducts() {
        loading(true);
        productController.getAllProducts(new DatabaseService.RealtimeCallback<List<ProductModel>>() {
            @Override
            public void onDataChange(List<ProductModel> data) {
                loading(false);
                if (data == null) return;
                products = data;
                buildCategoryChips(products); // ✅ build chips from real data
                filterProducts(searchProduct.getText().toString());
            }

            @Override
            public void onFailure(String errorMessage) {
                loading(false);
            }
        });
    }

    private void filterProducts(String keyword) {
        if (products == null) return;

        List<ProductModel> filteredList = new ArrayList<>();
        for (ProductModel product : products) {
            String name = product.getProductName() != null ? product.getProductName().toLowerCase() : "";
            String category = product.getCategory() != null ? product.getCategory().toLowerCase() : "";
            String subcategory = product.getSubcategory() != null ? product.getSubcategory().toLowerCase() : "";
            String userName = product.getUserName() != null ? product.getUserName().toLowerCase() : "";

            boolean categoryMatch = selectedCategory.equals("All")
                    || category.equalsIgnoreCase(selectedCategory)
                    || subcategory.equalsIgnoreCase(selectedCategory);

            boolean searchMatch = keyword == null || keyword.trim().isEmpty()
                    || name.contains(keyword.toLowerCase())
                    || category.contains(keyword.toLowerCase())
                    || subcategory.contains(keyword.toLowerCase())
                    || userName.contains(keyword.toLowerCase());

            if (categoryMatch && searchMatch) {
                filteredList.add(product);
            }
        }

        productsView.setAdapter(new ProductAdapter(this, filteredList));
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            loader.setVisibility(View.VISIBLE);
            productsView.setVisibility(View.GONE);
        } else {
            loader.setVisibility(View.GONE);
            productsView.setVisibility(View.VISIBLE);
        }
    }
}