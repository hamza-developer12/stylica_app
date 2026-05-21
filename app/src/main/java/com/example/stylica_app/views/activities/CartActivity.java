package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.helpers.CartDatabaseHelper;
import com.example.stylica_app.models.CartModel;
import com.example.stylica_app.views.adapters.CartAdapter;

import java.util.List;

public class CartActivity extends BaseActivity {

    RecyclerView recyclerView;
    LinearLayout emptyView, cartContent;
    TextView txtItemCount, txtTotal;
    Button btnCheckout;

    CartDatabaseHelper cartDb;
    CartAdapter adapter;
    List<CartModel> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);




        setupAppBar("Cart");

        recyclerView = findViewById(R.id.cartRecyclerView);
        emptyView    = findViewById(R.id.emptyView);
        cartContent  = findViewById(R.id.cartContent);
        txtItemCount = findViewById(R.id.txtItemCount);
        txtTotal     = findViewById(R.id.txtTotal);
        btnCheckout  = findViewById(R.id.btnCheckout);

        cartDb = CartDatabaseHelper.getInstance(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCart();

        btnCheckout.setOnClickListener(v -> {
            // Go to checkout screen
            Intent i = new Intent(this, CheckoutActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        cartItems = cartDb.getAllCartItems();

        if (cartItems.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            cartContent.setVisibility(View.GONE);
            return;
        }

        emptyView.setVisibility(View.GONE);
        cartContent.setVisibility(View.VISIBLE);

        // Setup adapter
        adapter = new CartAdapter(this, cartItems, () -> {
            // Called whenever cart changes — update summary
            updateSummary();

            // If cart becomes empty show empty state
            if (cartDb.getCartCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                cartContent.setVisibility(View.GONE);
            }
        });

        recyclerView.setAdapter(adapter);
        updateSummary();
    }

    // Update item count and total
    private void updateSummary() {
        int count     = cartDb.getCartCount();
        double total  = cartDb.getCartTotal();
        txtItemCount.setText(String.valueOf(count));
        txtTotal.setText("Rs " + total);
    }
}