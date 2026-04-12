package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ModeratorsListActivity extends BaseActivity {

    FloatingActionButton fabAddModerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_moderators_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAppBar("Moderators");

        fabAddModerator = findViewById(R.id.fabAddModerator);

        fabAddModerator.setOnClickListener(v->{
            Intent i = new Intent(ModeratorsListActivity.this, AddModeratorActivity.class);
            startActivity(i);
        });
    }
}