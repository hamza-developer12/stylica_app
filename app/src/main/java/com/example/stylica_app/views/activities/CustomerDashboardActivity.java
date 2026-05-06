package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.controllers.ProductController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardActivity extends AppCompatActivity {

    // Controllers
    ProductController productController;
    CategoryController categoryController;

    // Containers
    LinearLayout bestSellersList, newArrivalsList, categoryChips;

    // Loaders
    ProgressBar loaderBestSellers, loaderNewArrivals;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Init
        productController  = ProductController.getInstance(this);
        categoryController = CategoryController.getInstance();

        bestSellersList  = findViewById(R.id.bestSellersList);
        newArrivalsList  = findViewById(R.id.newArrivalsList);
        categoryChips    = findViewById(R.id.categoryChips);
        loaderBestSellers  = findViewById(R.id.loaderBestSellers);
        loaderNewArrivals  = findViewById(R.id.loaderNewArrivals);
        toolbar = findViewById(R.id.myToolBar);


        setSupportActionBar(toolbar);
        // Load all sections
        loadCategories();
        loadProducts();

        // Shop Now button
        findViewById(R.id.btnShopNow).setOnClickListener(v -> {
            Intent i = new Intent(this, ProductsViewActivity.class);
            startActivity(i);
        });

        // Shop Sale button
        findViewById(R.id.btnShopSale).setOnClickListener(v -> {
            Intent i = new Intent(this, ProductsViewActivity.class);
            startActivity(i);
        });

        // See All Best Sellers
        findViewById(R.id.txtSeeAllBestSellers).setOnClickListener(v -> {
            Intent i = new Intent(this, ProductsViewActivity.class);
            startActivity(i);
        });

        // See All New Arrivals
        findViewById(R.id.txtSeeAllNewArrivals).setOnClickListener(v -> {
            Intent i = new Intent(this, ProductsViewActivity.class);
            startActivity(i);
        });
    }

    // ✅ Load categories and build chips
    private void loadCategories() {
        categoryController.getAllCategories(
                new DatabaseService.DatabaseCallback<List<CategoryModel>>() {
                    @Override
                    public void onSuccess(List<CategoryModel> data) {
                        categoryChips.removeAllViews();
                        for (CategoryModel cat : data) {
                            addCategoryChip(cat.getCategoryName());
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(CustomerDashboardActivity.this,
                                "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProducts() {
        loaderBestSellers.setVisibility(View.VISIBLE);
        loaderNewArrivals.setVisibility(View.VISIBLE);

        productController.getAllProducts(new DatabaseService.RealtimeCallback<List<ProductModel>>() {
            @Override
            public void onDataChange(List<ProductModel> data) {
                loaderBestSellers.setVisibility(View.GONE);
                loaderNewArrivals.setVisibility(View.GONE);

                if (data == null) return;

                // Split into best sellers and new arrivals
                List<ProductModel> bestSellers = new ArrayList<>();
                List<ProductModel> newArrivals = new ArrayList<>();

                for (ProductModel p : data) {
                    if (p.getFeatured()) bestSellers.add(p);
                    if (p.getNew())      newArrivals.add(p);
                }

                buildProductCards(bestSellersList, bestSellers, "BEST SELLER");
                buildProductCards(newArrivalsList, newArrivals, "NEW");
            }

            @Override
            public void onFailure(String errorMessage) {
                loaderBestSellers.setVisibility(View.GONE);
                loaderNewArrivals.setVisibility(View.GONE);
            }
        });
    }

    // ✅ Build product cards dynamically
    private void buildProductCards(LinearLayout container,
                                   List<ProductModel> products,
                                   String badge) {
        container.removeAllViews();

        if (products.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No products found");
            empty.setTextColor(getColor(R.color.text_secondary));
            empty.setPadding(16, 16, 16, 16);
            container.addView(empty);
            return;
        }

        for (ProductModel product : products) {
            // Card container
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.product_card_bg);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    (int) (160 * getResources().getDisplayMetrics().density),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMarginEnd((int) (12 * getResources().getDisplayMetrics().density));
            card.setLayoutParams(cardParams);
            card.setPadding(0, 0, 0,
                    (int) (12 * getResources().getDisplayMetrics().density));

            // Product image
            FrameLayout imgContainer = new FrameLayout(this);
            LinearLayout.LayoutParams imgContainerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (130 * getResources().getDisplayMetrics().density)
            );
            imgContainer.setLayoutParams(imgContainerParams);

            ImageView img = new ImageView(this);
            img.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.image_placeholder_bg)
                    .into(img);
            imgContainer.addView(img);

            // Badge label (BEST SELLER / NEW)
            TextView badgeView = new TextView(this);
            badgeView.setText(badge);
            badgeView.setTextColor(getColor(R.color.text_white));
            badgeView.setBackgroundColor(getColor(R.color.primary));
            badgeView.setTextSize(9);
            int badgePad = (int) (4 * getResources().getDisplayMetrics().density);
            badgeView.setPadding(badgePad, badgePad, badgePad, badgePad);
            FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            badgeParams.gravity = android.view.Gravity.TOP | android.view.Gravity.START;
            badgeParams.setMargins(badgePad, badgePad, 0, 0);
            badgeView.setLayoutParams(badgeParams);
            imgContainer.addView(badgeView);

            card.addView(imgContainer);

            // Product name
            TextView name = new TextView(this);
            name.setText(product.getProductName());
            name.setTextColor(getColor(R.color.text_primary));
            name.setTextSize(13);
            int pad = (int) (8 * getResources().getDisplayMetrics().density);
            name.setPadding(pad, pad, pad, 2);
            name.setMaxLines(2);
            card.addView(name);

            // Price
            TextView price = new TextView(this);
            price.setText("Rs " + product.getPrice());
            price.setTextColor(getColor(R.color.primary_light_variant));
            price.setTextSize(13);
            price.setTypeface(null, android.graphics.Typeface.BOLD);
            price.setPadding(pad, 2, pad, 0);
            card.addView(price);

            // Add to cart button
            TextView addBtn = new TextView(this);
            addBtn.setText("Add to Cart");
            addBtn.setTextColor(getColor(R.color.text_white));
            addBtn.setBackgroundColor(getColor(R.color.primary_light_variant));
            addBtn.setTextSize(11);
            addBtn.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (34 * getResources().getDisplayMetrics().density)
            );
            btnParams.setMargins(pad, pad, pad, 0);
            addBtn.setLayoutParams(btnParams);
            addBtn.setOnClickListener(v ->
                    Toast.makeText(this,
                            product.getProductName() + " added to cart!",
                            Toast.LENGTH_SHORT).show()
            );
            card.addView(addBtn);

            // Open product on card click
            card.setOnClickListener(v -> {
                Intent i = new Intent(this, SingleProductActivity.class);
                i.putExtra("productId", product.getProductId());
                startActivity(i);
            });

            container.addView(card);
        }
    }

    // ✅ Build a single category chip
    private void addCategoryChip(String categoryName) {
        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setGravity(android.view.Gravity.CENTER);

        LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        chipParams.setMarginEnd((int) (16 * getResources().getDisplayMetrics().density));
        chip.setLayoutParams(chipParams);

        // Circle icon background
        TextView icon = new TextView(this);
        int size = (int) (56 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.chip_selected_bg);
        icon.setGravity(android.view.Gravity.CENTER);
        icon.setText(categoryName.substring(0, 1).toUpperCase()); // first letter
        icon.setTextColor(getColor(R.color.text_white));
        icon.setTextSize(18);
        chip.addView(icon);

        // Category name below icon
        TextView label = new TextView(this);
        label.setText(categoryName);
        label.setTextColor(getColor(R.color.text_primary));
        label.setTextSize(12);
        label.setGravity(android.view.Gravity.CENTER);
        int topMargin = (int) (4 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        labelParams.topMargin = topMargin;
        label.setLayoutParams(labelParams);
        chip.addView(label);

        // On click — go to products filtered by category
        chip.setOnClickListener(v -> {
            Intent i = new Intent(this, ProductsViewActivity.class);
            startActivity(i);
        });

        categoryChips.addView(chip);
    }
}