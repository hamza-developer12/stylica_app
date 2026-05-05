package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.controllers.ProductController;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;

public class SingleProductActivity extends AppCompatActivity {

    ProductModel product;
    int quantity = 1;

    // Views
    ImageView imgProduct;
    TextView txtProductName, txtProductPrice, txtDescription,
            txtCategory, txtSubcategory, txtQuantity;
    Button btnAddToCart, btnIncrease, btnDecrease;
    ImageButton btnBack;

    TextView txtStock;

    ProductController productController;

    SessionService sessionService;
    LinearLayout addToCartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionService = new SessionService(this);
        // Get productId from previous screen
        String productId = getIntent().getStringExtra("productId");

        if (productId == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        btnBack.setOnClickListener(v -> finish());

        productController = ProductController.getInstance(this);
        fetchProduct(productId);
    }

    private void initViews() {
        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtDescription = findViewById(R.id.txtDescription);
        txtCategory = findViewById(R.id.txtCategory);
        txtSubcategory = findViewById(R.id.txtSubcategory);
        txtQuantity = findViewById(R.id.txtQuantity);
        btnBack = findViewById(R.id.btnBack);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnAddToCart = findViewById(R.id.btnSubmit);
        addToCartView = findViewById(R.id.addToCartView);
        txtStock = findViewById(R.id.txtStock);

    }

    private void fetchProduct(String productId) {
        // ✅ Hide content while loading
        findViewById(R.id.addToCartView).setVisibility(View.GONE);

        productController.getProductById(productId,
                new DatabaseService.DatabaseCallback<ProductModel>() {
                    @Override
                    public void onSuccess(ProductModel data) {
                        product = data;
                        fillDetails();
                        setupQuantity();
                        setupButtons();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(SingleProductActivity.this,
                                "Failed to load product", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void fillDetails() {
        txtProductName.setText(product.getProductName());
        txtProductPrice.setText("Rs " + product.getPrice());
        txtDescription.setText(product.getDescription());
        txtCategory.setText(product.getCategory() != null ? product.getCategory() : "N/A");
        txtSubcategory.setText(product.getSubcategory() != null ? product.getSubcategory() : "N/A");

        int stock = product.getStockQuantity();

        if (stock > 0) {
            txtStock.setText("In Stock: " + stock);
            txtStock.setTextColor(getColor(R.color.success));
        } else {
            txtStock.setText("Out of Stock");
            txtStock.setTextColor(getColor(R.color.error));
        }

        Glide.with(SingleProductActivity.this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_placeholder_bg)
                .into(imgProduct);

        String role = sessionService.getUserRole();
        Log.d("User_Role", "Role is: [" + role + "]");
        if(role.equals("customer")) {
            addToCartView.setVisibility(View.VISIBLE);
        }else {
            addToCartView.setVisibility(View.GONE);
        }
    }

    private void setupQuantity() {
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (quantity < product.getStockQuantity()) {
                quantity++;
                txtQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "No more stock available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupButtons() {
        btnAddToCart.setOnClickListener(v -> {
            // TODO: implement cart
            Toast.makeText(this,
                    product.getProductName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });
    }
}