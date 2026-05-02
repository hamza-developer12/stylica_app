package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Space;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.services.SessionService;

public class SplashActivity extends SessionActivity {

    SessionService sessionService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sessionService = new SessionService(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               moveToScreen(SplashActivity.this, sessionService);
            }
        },2000);
    }


}