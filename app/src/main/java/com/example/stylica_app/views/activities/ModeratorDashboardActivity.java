package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.ModeratorController;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.components.DashboardHeaderComponent;

public class ModeratorDashboardActivity extends DashboardHeaderComponent {

    View prodCard;

    SessionService sessionService;


    View ordersCard;
    View pendingProductsScreenCard;
    View profileScreenCard;

    ModeratorController moderatorController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moderator_dashboard);


        sessionService = new SessionService(this);
        moderatorController = ModeratorController.getInstance(this);



        String name = sessionService.getUserName();
        String fname = name.split(" ")[0];

        setupHeader(fname,"Moderator", ()->{
            moderatorController.logout(ModeratorDashboardActivity.this);
        });
        initializeCards();


    }

    private void initializeCards(){
        // Cards
        prodCard = findViewById(R.id.productsScreenBtn);
        ordersCard = findViewById(R.id.ordersScreenBtn);
        pendingProductsScreenCard = findViewById(R.id.pendingProductsScreenBtn);
        profileScreenCard = findViewById(R.id.profileScreenBtn);

        TextView prodText = prodCard.findViewById(R.id.cardText);
        TextView orderText = ordersCard.findViewById(R.id.cardText);
        TextView pendingProductsScreenText = pendingProductsScreenCard.findViewById(R.id.cardText);
        TextView profileScreenCardText = profileScreenCard.findViewById(R.id.cardText);

        prodText.setText("Products");
        orderText.setText("Orders Status");
        pendingProductsScreenText.setText("Pending\n Products");
        profileScreenCardText.setText("Profile");


        prodCard.setOnClickListener(v->{
            Intent i = new Intent(ModeratorDashboardActivity.this, ProductsViewActivity.class);
            startActivity(i);
        });
        ordersCard.setOnClickListener(v->{
//            Intent i = new Intent(ModeratorDashboardActivity.this, ModeratorOrdersActivity.class);
            Intent i = new Intent(ModeratorDashboardActivity.this, VendorOrdersActivity.class);
            startActivity(i);
        });

        pendingProductsScreenCard.setOnClickListener(v->{
            startActivity(new Intent(ModeratorDashboardActivity.this, PendingProductsActivity.class));
        });

        profileScreenCard.setOnClickListener(v->{
            startActivity(new Intent(ModeratorDashboardActivity.this, ProfileActivity.class));
        });
    }
}