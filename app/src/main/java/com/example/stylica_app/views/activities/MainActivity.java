package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.UserController;
import com.example.stylica_app.services.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    int eyeFlag = 0;
    ImageView eyeIcon;
    EditText emailField;
    EditText passwordField;

    TextView goToSignupScreenText;
    ProgressBar loader;
    Button signInBtn;

    UserController userController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Initialize....
        userController = new UserController(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());

        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        goToSignupScreenText = findViewById(R.id.signup_screen_btn);
        signInBtn = findViewById(R.id.login_btn);
        loader = findViewById(R.id.loader);
        moveToSignupScreen();
    }


    public void showOrHidePasswd(View v) {
        ImageView eyeIcon = findViewById(R.id.show_hide_passwd_icon);
        if (eyeFlag == 0) {
            // Show password
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeIcon.setImageResource(R.drawable.eye_off);
            eyeFlag = 1;
        } else {
            // Hide password
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeIcon.setImageResource(R.drawable.eye_on);
            eyeFlag = 0;
        }

        passwordField.setSelection(passwordField.getText().length());
    }

    public void moveToSignupScreen() {
        goToSignupScreenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), SignupActivity.class);
                startActivity(i);
            }
        });
    }

    public void login(View v){
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please provide all Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        toggleLoading(true);
        userController.login(email, password, new UserController.UserCallback() {
            @Override
            public void onSuccess(String message) {
                toggleLoading(false);
                Toast.makeText(MainActivity.this,message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d("ERROR_FROM_FB", errorMessage);
                toggleLoading(false);
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void toggleLoading(boolean isLoading) {
        if (isLoading) {
            loader.setVisibility(View.VISIBLE);
            signInBtn.setVisibility(View.INVISIBLE);
        } else {
            loader.setVisibility(View.INVISIBLE);
            signInBtn.setVisibility(View.VISIBLE);
        }
    }
}