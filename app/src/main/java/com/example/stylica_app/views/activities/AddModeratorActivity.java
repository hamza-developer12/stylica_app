package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;

public class AddModeratorActivity extends BaseActivity {

    EditText edtFirstName;
    EditText edtLastName;
    EditText edtEmail;
    EditText edtProductDomain;
    EditText edtPassword;

    ProgressBar loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_moderator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAppBar("Add Moderator");

        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtProductDomain = findViewById(R.id.edtProductDomain);
        edtPassword = findViewById(R.id.edtPassword);
        loader = findViewById(R.id.loader);

        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String domain = edtProductDomain.getText().toString();
        String password = edtPassword.getText().toString();


        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || domain.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Provide all details", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }


    }
}