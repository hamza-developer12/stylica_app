package com.example.stylica_app.views.activities;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stylica_app.services.SessionService;

public class SessionActivity extends AppCompatActivity {
    public void moveToScreen(Context context, SessionService sessionService) {
        Intent i = null;
        String verificationStatus = sessionService.getUserVerifiedStatus();
        boolean isLoggedIn = sessionService.getUserLoggedInStatus();
        String role = sessionService.getUserRole();

        if (isLoggedIn && verificationStatus.equals("approved") ) {
            switch (role) {
                case "admin":
                    i = new Intent(context, AdminDashboardActivity.class);
                    break;
                case "moderator":
                    i = new Intent(context, ModeratorDashboardActivity.class);
                    break;
                case "vendor":
                    i = new Intent(context, VendorDashboardActivity.class);
                    break;
                case "customer":
                    i = new Intent(context, CustomerDashboardActivity.class);
                    break;
                default:
                    break;
            }
        } else if (isLoggedIn && verificationStatus.equals("pending")) {
            i = new Intent(context, PendingVerificationActivity.class);
        } else {
            i = new Intent(context, LoginActivity.class);
        }
        startActivity(i);
        finish();
    }
}
