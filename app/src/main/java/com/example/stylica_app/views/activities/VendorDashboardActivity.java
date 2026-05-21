package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.UserController;
import com.example.stylica_app.services.SessionService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class VendorDashboardActivity extends AppCompatActivity {

    SessionService sessionService;

    TextView txtHeaderTitle;

    ImageButton logoutBtn;

    UserController userController;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    View productsScreenBtnCard;
    View vendorOrdersScreenCard;
    View vendorPendingProductsScreenCard;
    View vendorRejectedProductsScreenCard;
    View profileScreenCard;
    View analyticsScreenCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vendor_dashboard);


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        sessionService = new SessionService(this);
        userController = new UserController(auth, firestore);

        txtHeaderTitle = findViewById(R.id.header_title);
        logoutBtn = findViewById(R.id.logoutBtn);

        String name = sessionService.getUserName().split(" ")[0];
        txtHeaderTitle.setText("Welcome Back, " + name);


        logoutBtn.setOnClickListener(v-> {
            userController.logout(VendorDashboardActivity.this);
        });


        //Cards
        productsScreenBtnCard = findViewById(R.id.productsScreenBtn);
        vendorOrdersScreenCard = findViewById(R.id.vendorOrdersScreenBtn);
        vendorPendingProductsScreenCard = findViewById(R.id.vendorPendingProductsScreenBtn);
        vendorRejectedProductsScreenCard = findViewById(R.id.vendorRejectedProductsScreenBtn);
        profileScreenCard = findViewById(R.id.profileScreenBtn);
        analyticsScreenCard = findViewById(R.id.analyticsScreenBtn);

        // TextViews
        TextView productScreenCardText = productsScreenBtnCard.findViewById(R.id.cardText);
        TextView vendorOrdersScreenCardText = vendorOrdersScreenCard.findViewById(R.id.cardText);
        TextView vendorPendingProductsScreenCardText = vendorPendingProductsScreenCard.findViewById(R.id.cardText);
        TextView vendorRejectedProductsScreenCardText = vendorRejectedProductsScreenCard.findViewById(R.id.cardText);
        TextView profileScreenCardText = profileScreenCard.findViewById(R.id.cardText);
        TextView analyticsScreenCardText = analyticsScreenCard.findViewById(R.id.cardText);

        productScreenCardText.setText("Products\n Management");
        vendorOrdersScreenCardText.setText("Orders\n Management");
        vendorPendingProductsScreenCardText.setText("Pending\n Products");
        vendorRejectedProductsScreenCardText.setText("Rejected\n Products");
        profileScreenCardText.setText("Profile");
        analyticsScreenCardText.setText("Analytics");

        productsScreenBtnCard.setOnClickListener(v->{
            startActivity(new Intent(VendorDashboardActivity.this, ProductsViewActivity.class));
        });

        vendorOrdersScreenCard.setOnClickListener(v->{
            startActivity(new Intent(VendorDashboardActivity.this, VendorOrdersActivity.class));
        });

        vendorPendingProductsScreenCard.setOnClickListener(v->{
            startActivity(new Intent(VendorDashboardActivity.this, VendorPendingProductsActivity.class));
        });
        vendorRejectedProductsScreenCard.setOnClickListener(v->{
            startActivity(new Intent(VendorDashboardActivity.this, VendorRejectedProductsActivity.class));
        });
        profileScreenCard.setOnClickListener(v->{
            startActivity(new Intent(VendorDashboardActivity.this, ProfileActivity.class));
        });
        analyticsScreenCard.setOnClickListener(v->{
            startActivity(new Intent(VendorDashboardActivity.this, VendorAnalyticsActivity.class));
        });
    }
}