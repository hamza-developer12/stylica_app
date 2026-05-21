package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.UserController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends BaseActivity {

    EditText edtEmail;

    ProgressBar loader;

    Button btnSubmit;

    UserController userController;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setupAppBar("Forgot Password");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userController = new UserController(auth,firestore);

        edtEmail = findViewById(R.id.edtEmail);
        loader = findViewById(R.id.loader);
        btnSubmit = findViewById(R.id.btnSubmit);


        btnSubmit.setOnClickListener(v->{
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                edtEmail.setError("Please provide an email");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Please provide a valid email");
                return;
            }
            isLoading(true);
            userController.resetPassword(email, new UserController.UserCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    isLoading(false);
                    Toast.makeText(ForgotPasswordActivity.this, data, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    isLoading(false);
                    Toast.makeText(ForgotPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void isLoading(boolean loading) {
        if(loading) {
            loader.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }else {
            loader.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
    }
}