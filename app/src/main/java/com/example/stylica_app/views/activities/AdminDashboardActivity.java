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
import com.example.stylica_app.controllers.AdminController;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.components.DashboardHeaderComponent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends DashboardHeaderComponent {

    View catCard;
    View prodCard;

    SessionService sessionService;
    View moderatorsCard;
    View couriersCard;

    View pendingProductsCard;
    View paymentMethodsCard;


    AdminController adminController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        headerTitle = findViewById(R.id.header_title);
        sessionService = new SessionService(this);
        adminController = new AdminController(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());


        String name = sessionService.getUserName();
        String fname = name.split(" ")[0];

        initializeCards();

        setupHeader(fname,"Admin", ()->{
            adminController.logout(AdminDashboardActivity.this);
        });
    }

    private void initializeCards() {
        prodCard = findViewById(R.id.productsScreenBtn);
        catCard = findViewById(R.id.categoriesScreenBtn);
        moderatorsCard = findViewById(R.id.moderatorsScreenBtn);
        couriersCard = findViewById(R.id.couriersScreenBtn);
        pendingProductsCard = findViewById(R.id.pendingProductsScreenBtn);
        paymentMethodsCard = findViewById(R.id.paymentMethodsScreenBtn);

        TextView catText = catCard.findViewById(R.id.cardText);
        TextView prodText = prodCard.findViewById(R.id.cardText);
        TextView moderatorText = moderatorsCard.findViewById(R.id.cardText);
        TextView courierText = couriersCard.findViewById(R.id.cardText);
        TextView pendingProductText = pendingProductsCard.findViewById(R.id.cardText);
        TextView paymentMethodsText = paymentMethodsCard.findViewById(R.id.cardText);

        catText.setText("Categories");
        prodText.setText("Products");
        moderatorText.setText("Moderators");
        courierText.setText("Couriers");
        pendingProductText.setText("Pending Products");
        paymentMethodsText.setText("Payment Methods");

        prodCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, ProductsViewActivity.class);
            startActivity(i);
        });

        catCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, CategoriesActivity.class);
            startActivity(i);
        });
        moderatorsCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, ModeratorsListActivity.class);
            startActivity(i);
        });

        couriersCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, CouriersListActivity.class);
            startActivity(i);
        });

        pendingProductsCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, PendingProductsActivity.class);
            startActivity(i);
        });

        paymentMethodsCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, PaymentMethodsActivity.class);
            startActivity(i);
        });
    }
}