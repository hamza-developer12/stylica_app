package com.example.stylica_app.views.components;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.UserController;

public class DashboardHeaderComponent extends AppCompatActivity {

    protected TextView headerTitle;
    protected TextView headerSubtitle;
    protected ImageButton logoutBtn;


    protected void setupHeader(String name, String dashboardName, Runnable logoutCallback) {

        headerTitle = findViewById(R.id.header_title);
        headerSubtitle = findViewById(R.id.header_subtitle);

        logoutBtn = findViewById(R.id.logoutBtn);

        if(headerTitle != null) {
            headerTitle.setText("Welcome Back, " + name);
        }
        if(headerSubtitle != null) {
            headerSubtitle.setText(dashboardName + " Dashboard");
        }
        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v-> {
                logoutCallback.run();
            });
        }

    }
}
