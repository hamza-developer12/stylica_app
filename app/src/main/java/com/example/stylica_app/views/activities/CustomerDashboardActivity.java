package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.controllers.ProductController;
import com.example.stylica_app.controllers.UserController;
import com.example.stylica_app.helpers.CartDatabaseHelper;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.adapters.CustomerHomeProductAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    // App bar views
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu, btnCart;

    Button btnShopNow;

    TextView txtCartBadge;

    TextView txtSeeAllBestSellers, txtSeeAllNewArrivals;

    // Controllers
    ProductController productController;
    CategoryController categoryController;
    UserController userController;
    SessionService sessionService;
    CartDatabaseHelper cartDb;

    RecyclerView featuredProductsView, newProductsView;

    // Containers
    LinearLayout  categoryChips;
    ProgressBar loaderBestSellers, loaderNewArrivals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_dashboard);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        productController = ProductController.getInstance(this);
        categoryController = CategoryController.getInstance();
        userController = new UserController(auth, firestore);
        sessionService = new SessionService(this);
        cartDb = CartDatabaseHelper.getInstance(this);



        // App bar
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        btnCart = findViewById(R.id.btnCart);


        txtCartBadge = findViewById(R.id.txtCartBadge);
        txtSeeAllBestSellers = findViewById(R.id.txtSeeAllBestSellers);
        txtSeeAllNewArrivals = findViewById(R.id.txtSeeAllNewArrivals);

        featuredProductsView = findViewById(R.id.featuredProductsView);
        newProductsView  = findViewById(R.id.newProductsView);

        btnShopNow = findViewById(R.id.btnShopNow);


        featuredProductsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        newProductsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        categoryChips    = findViewById(R.id.categoryChips);
        loaderBestSellers = findViewById(R.id.loaderBestSellers);
        loaderNewArrivals = findViewById(R.id.loaderNewArrivals);

        // Open drawer on menu click
        btnMenu.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));

        // Cart button
        btnCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        // Setup nav drawer header with user info
        setupNavHeader();

        // Nav drawer item clicks
        setupNavMenu();

        // Load data
        loadCategories();
        loadProducts();

        txtSeeAllBestSellers.setOnClickListener(v->{
            Intent i = new Intent(CustomerDashboardActivity.this, ProductsViewActivity.class);
            startActivity(i);
        });
        txtSeeAllNewArrivals.setOnClickListener(v-> {
            startActivity(new Intent(CustomerDashboardActivity.this, ProductsViewActivity.class));
        });

        btnShopNow.setOnClickListener(v-> {
            startActivity(new Intent(CustomerDashboardActivity.this, ProductsViewActivity.class));
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Update cart badge every time screen resumes
        updateCartBadge();
    }

    private void updateCartBadge() {
        int count = cartDb.getCartCount();
        if (count > 0) {
            txtCartBadge.setVisibility(View.VISIBLE);
            txtCartBadge.setText(String.valueOf(count));
        } else {
            txtCartBadge.setVisibility(View.GONE);
        }
    }

    private void setupNavHeader() {
        View header = navigationView.getHeaderView(0);
        TextView navAvatar = header.findViewById(R.id.navAvatar);
        TextView navName   = header.findViewById(R.id.navName);
        TextView navEmail  = header.findViewById(R.id.navEmail);

        String name  = sessionService.getUserName();
        String email = sessionService.getUserEmail();

        navName.setText(name != null ? name : "Customer");
        navEmail.setText(email != null ? email : "");
        navAvatar.setText(name != null && !name.isEmpty()
                ? String.valueOf(name.charAt(0)).toUpperCase() : "C");
    }

    private void setupNavMenu() {
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Already on home — do nothing
            } else if (id == R.id.nav_products) {
                startActivity(new Intent(this, ProductsViewActivity.class));
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
            } else if (id == R.id.nav_orders) {

                startActivity(new Intent(this, MyOrdersActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_announcements) {
                startActivity(new Intent(this, AnnouncementsActivity.class));
            } else if (id == R.id.nav_logout) {
                userController.logout(this);
            }

            return true;
        });
    }

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
                    public void onFailure(String errorMessage) {}
                });
    }

    private void loadProducts() {
        loaderBestSellers.setVisibility(View.VISIBLE);
        loaderNewArrivals.setVisibility(View.VISIBLE);
        productController.getLimitedProducts("featured", true, 4, new DatabaseService.DatabaseCallback<List<ProductModel>>() {
            @Override
            public void onSuccess(List<ProductModel> data) {
                loaderBestSellers.setVisibility(View.GONE);
                CustomerHomeProductAdapter featuredProductsAdapter = new CustomerHomeProductAdapter(CustomerDashboardActivity.this,data);
                featuredProductsView.setAdapter(featuredProductsAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                loaderBestSellers.setVisibility(View.GONE);

                Toast.makeText(CustomerDashboardActivity.this, "Error: "+errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        productController.getLimitedProducts("new", true, 4, new DatabaseService.DatabaseCallback<List<ProductModel>>() {
            @Override
            public void onSuccess(List<ProductModel> data) {
                loaderNewArrivals.setVisibility(View.GONE);
                CustomerHomeProductAdapter newArrivalProductsAdapter = new CustomerHomeProductAdapter(CustomerDashboardActivity.this,data);
                newProductsView.setAdapter(newArrivalProductsAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                loaderNewArrivals.setVisibility(View.GONE);
                Toast.makeText(CustomerDashboardActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addCategoryChip(String categoryName) {
        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setGravity(android.view.Gravity.CENTER);

        LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        chipParams.setMarginEnd(
                (int) (16 * getResources().getDisplayMetrics().density));
        chip.setLayoutParams(chipParams);

        // Circle icon
        TextView icon = new TextView(this);
        int size = (int) (56 * getResources().getDisplayMetrics().density);
        icon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        icon.setBackgroundResource(R.drawable.chip_selected_bg);
        icon.setGravity(android.view.Gravity.CENTER);
        icon.setText(categoryName.substring(0, 1).toUpperCase());
        icon.setTextColor(getColor(R.color.text_white));
        icon.setTextSize(18);
        chip.addView(icon);

        // Label
        TextView label = new TextView(this);
        label.setText(categoryName);
        label.setTextColor(getColor(R.color.text_primary));
        label.setTextSize(12);
        label.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        labelParams.topMargin =
                (int) (4 * getResources().getDisplayMetrics().density);
        label.setLayoutParams(labelParams);
        chip.addView(label);

        chip.setOnClickListener(v ->
                startActivity(new Intent(this, ProductsViewActivity.class)));

        categoryChips.addView(chip);
    }
}