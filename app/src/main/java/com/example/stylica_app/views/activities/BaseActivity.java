package com.example.stylica_app.views.activities;

import android.widget.ImageView;
import android.widget.TextView;
import com.example.stylica_app.R;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected TextView screenTitle;
    protected ImageView backBtn;

    protected void setupAppBar(String title) {
        backBtn = findViewById(R.id.btnBack);
        screenTitle = findViewById(R.id.screenTitle);

        if(backBtn != null) {
            backBtn.setOnClickListener(v-> finish());
        }
        if(screenTitle != null) {
            screenTitle.setText(title);
        }
    }
}
