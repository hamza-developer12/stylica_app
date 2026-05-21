package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.controllers.ProductController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.adapters.ProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductsViewActivity extends BaseActivity {

    FloatingActionButton fabAddProduct;
    RecyclerView productsView;
    ProductController productController;
    CategoryController categoryController;
    ProgressBar loader;
    EditText searchProduct;
    LinearLayout chipGroup;

    SessionService sessionService;

    List<ProductModel> allProducts = new ArrayList<>();
    List<String> categoryNames = new ArrayList<>();

    String selectedCategory = "All";

    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_view);

        setupAppBar("Products");

        sessionService = new SessionService(this);

        fabAddProduct     = findViewById(R.id.fabAddProduct);
        productsView      = findViewById(R.id.productsView);
        loader            = findViewById(R.id.loader);
        searchProduct     = findViewById(R.id.searchProduct);
        chipGroup         = findViewById(R.id.chipGroup);
        productController  = ProductController.getInstance(this);
        categoryController = CategoryController.getInstance();

        //        check user role
        role = sessionService.getUserRole();

        if(role.equals("customer")){
            productsView.setLayoutManager(new GridLayoutManager(this,2));
        }else {
            productsView.setLayoutManager(new LinearLayoutManager(this));
        }

        fabAddProduct.setOnClickListener(v -> {
            Intent i = new Intent(ProductsViewActivity.this, AddProductActivity.class);
            startActivity(i);
        });

        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

                filterAndShow(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });


//        if(role.equals("admin")) {
//            fetchCategoriesAndProducts();
//        }else {
//            fetchProducts();
//        }
        fetchCategoriesAndProducts();



        if(role.equals("customer") || role.equals("moderator")) {
            fabAddProduct.setVisibility(View.GONE);
        }else {
            fabAddProduct.setVisibility(View.VISIBLE);
        }
    }

    private void fetchCategoriesAndProducts() {
        loading(true);

        categoryController.getAllCategories(
                new DatabaseService.DatabaseCallback<List<CategoryModel>>() {
                    @Override
                    public void onSuccess(List<CategoryModel> data) {
                        // Store category names locally
                        categoryNames.clear();
                        categoryNames.add("All");
                        for (CategoryModel cat : data) {
                            if (cat.getCategoryName() != null
                                    && !cat.getCategoryName().isEmpty()) {
                                categoryNames.add(cat.getCategoryName());
                            }
                        }


                        if(role.equals("admin") || role.equals("customer")) {
                            renderChips();
                        }
                        fetchProducts();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Even if categories fail, still load products
                        fetchProducts();
                    }
                });
    }

    private void fetchProducts() {
        if (role.equals("moderator") || role.equals("vendor")) {
            productController.getAllProductsWhere(
                    new DatabaseService.RealtimeCallback<List<ProductModel>>() {
                        @Override
                        public void onDataChange(List<ProductModel> data) {
                            loading(false);
                            if (data == null) return;
                            allProducts = new ArrayList<>(data);
                            filterAndShow(searchProduct.getText().toString());
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            loading(false);
                        }
                    });
        } else {
            // admin + customer
            productController.getAllProducts(
                    new DatabaseService.RealtimeCallback<List<ProductModel>>() {
                        @Override
                        public void onDataChange(List<ProductModel> data) {
                            loading(false);
                            if (data == null) return;
                            allProducts = new ArrayList<>(data);
                            filterAndShow(searchProduct.getText().toString());
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            loading(false);
                        }
                    });
        }
    }

    private void renderChips() {
        chipGroup.removeAllViews();

        for (String category : categoryNames) {
            TextView chip = new TextView(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) (36 * getResources().getDisplayMetrics().density)
            );
            params.setMarginEnd(
                    (int) (8 * getResources().getDisplayMetrics().density));
            chip.setLayoutParams(params);

            chip.setText(category);
            chip.setGravity(android.view.Gravity.CENTER);
            int px18 = (int) (18 * getResources().getDisplayMetrics().density);
            chip.setPadding(px18, 0, px18, 0);
            chip.setTextSize(13);

            // Selected or unselected style
            if (category.equals(selectedCategory)) {
                chip.setBackgroundResource(R.drawable.chip_selected_bg);
                chip.setTextColor(getColor(R.color.text_white));
            } else {
                chip.setBackgroundResource(R.drawable.chip_unselected_bg);
                chip.setTextColor(getColor(R.color.text_secondary));
            }

            chip.setOnClickListener(v -> {
                selectedCategory = category;


                renderChips();


                filterAndShow(searchProduct.getText().toString());
            });

            chipGroup.addView(chip);
        }
    }

    private void filterAndShow(String keyword) {
        if (allProducts == null) return;

        List<ProductModel> filteredList = new ArrayList<>();

        for (ProductModel product : allProducts) {
            String name       = product.getProductName() != null ? product.getProductName().toLowerCase() : "";
            String category   = product.getCategory() != null ? product.getCategory().toLowerCase() : "";
            String subcategory = product.getSubcategory() != null ? product.getSubcategory().toLowerCase() : "";
            String userName   = product.getUserName() != null ? product.getUserName().toLowerCase() : "";

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


        productsView.setAdapter(new ProductAdapter(this, filteredList, role));
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