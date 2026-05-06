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
import com.example.stylica_app.controllers.ModeratorController;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.components.DashboardHeaderComponent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModeratorDashboardActivity extends DashboardHeaderComponent {
    View catCard;
    View prodCard;

    SessionService sessionService;
    View moderatorsCard;
    View couriersCard;

    ModeratorController moderatorController;


    TextView headerTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_moderator_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        prodCard = findViewById(R.id.productsScreenBtn);


        TextView prodText = prodCard.findViewById(R.id.cardText);

        prodText.setText("Products");
        prodCard.setOnClickListener(v->{
            Intent i = new Intent(ModeratorDashboardActivity.this, ProductsViewActivity.class);
            startActivity(i);
        });
    }
}