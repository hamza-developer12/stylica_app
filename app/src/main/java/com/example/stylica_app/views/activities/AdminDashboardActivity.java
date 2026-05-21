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

    View paymentMethodsCard;

    View announcementScreenCard;

    View pendingUsersCard;

    View paymentVerificationScreenCard;
    View transactionsScreenCard;
    View profileScreenCard;

    View vendorsScreenCard;
    View analyticsScreenCard;


    AdminController adminController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_dashboard);

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
        // Cards
        prodCard = findViewById(R.id.productsScreenBtn);
        catCard = findViewById(R.id.categoriesScreenBtn);
        moderatorsCard = findViewById(R.id.moderatorsScreenBtn);
        couriersCard = findViewById(R.id.couriersScreenBtn);
        paymentMethodsCard = findViewById(R.id.paymentMethodsScreenBtn);
        pendingUsersCard = findViewById(R.id.pendingUsersBtn);
        paymentVerificationScreenCard = findViewById(R.id.paymentVerificationScreenBtn);
        transactionsScreenCard = findViewById(R.id.transactionsScreenBtn);
        announcementScreenCard = findViewById(R.id.announcementScreenBtn);
        profileScreenCard = findViewById(R.id.profileScreenBtn);
        analyticsScreenCard = findViewById(R.id.analyticsScreenBtn);
        vendorsScreenCard = findViewById(R.id.vendorsScreenBtn);

        TextView catText = catCard.findViewById(R.id.cardText);
        TextView prodText = prodCard.findViewById(R.id.cardText);
        TextView moderatorText = moderatorsCard.findViewById(R.id.cardText);
        TextView courierText = couriersCard.findViewById(R.id.cardText);
        TextView paymentMethodsText = paymentMethodsCard.findViewById(R.id.cardText);
        TextView announcementText = announcementScreenCard.findViewById(R.id.cardText);
        TextView pendingUsersText = pendingUsersCard.findViewById(R.id.cardText);
        TextView pendingOrdersText = paymentVerificationScreenCard.findViewById(R.id.cardText);
        TextView transactionsText = transactionsScreenCard.findViewById(R.id.cardText);
        TextView profileScreenText = profileScreenCard.findViewById(R.id.cardText);
        TextView analyticsScreenText = analyticsScreenCard.findViewById(R.id.cardText);
        TextView vendorsScreenCardText = vendorsScreenCard.findViewById(R.id.cardText);

        catText.setText("Categories");
        prodText.setText("Products");
        moderatorText.setText("Moderators");
        courierText.setText("Couriers");
        paymentMethodsText.setText("Payment Methods");
        announcementText.setText("Announcements");
        pendingUsersText.setText("Pending Users");
        pendingOrdersText.setText("Payments\n Verification");
        transactionsText.setText("Transactions");
        profileScreenText.setText("Profile\n Management");
        vendorsScreenCardText.setText("Vendors");
        analyticsScreenText.setText("Analytics");

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

        paymentMethodsCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, PaymentMethodsActivity.class);
            startActivity(i);
        });

        announcementScreenCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, AnnouncementsActivity.class);
            startActivity(i);
        });

        pendingUsersCard.setOnClickListener(v->{
            Intent i = new Intent(AdminDashboardActivity.this, PendingUsersActivity.class);
            startActivity(i);
        });
        paymentVerificationScreenCard.setOnClickListener(v-> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class));
        });
        transactionsScreenCard.setOnClickListener(v->{
            startActivity(new Intent(AdminDashboardActivity.this, TransactionsActivity.class));
        });

        profileScreenCard.setOnClickListener(v->{
            startActivity(new Intent(AdminDashboardActivity.this, ProfileActivity.class));
        });

        vendorsScreenCard.setOnClickListener(v->{
            startActivity(new Intent(AdminDashboardActivity.this, VendorsListActivity.class));
        });

        analyticsScreenCard.setOnClickListener(v->{
            startActivity(new Intent(AdminDashboardActivity.this, AdminAnalyticsActivity.class));
        });

    }
}