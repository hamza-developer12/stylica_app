package com.example.stylica_app.views.activities;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stylica_app.services.SessionService;

public class SessionActivity extends AppCompatActivity {
    public void moveToScreen(Context context, SessionService sessionService) {
        Intent i = null;
        boolean isVerified = sessionService.getUserVerifiedStatus();
        boolean isLoggedIn = sessionService.getUserLoggedInStatus();
        String role = sessionService.getUserRole();

        if (isLoggedIn && isVerified) {
            switch (role) {
                case "admin":
                    i = new Intent(context, AdminDashboardActivity.class);
                    break;
                case "moderator":

                    break;
                case "vendor":

                    break;
                case "customer":

                    break;
                default:

                    break;
            }
        } else if (isLoggedIn && !isVerified) {

        } else {
            i = new Intent(context, LoginActivity.class);
        }
        startActivity(i);
        finish();
    }
}
