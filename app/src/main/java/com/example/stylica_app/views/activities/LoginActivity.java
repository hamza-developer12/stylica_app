package com.example.stylica_app.views.activities;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.UserController;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.SessionService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    int eyeFlag = 0;
    EditText emailField;
    EditText passwordField;
    TextView goToSignupScreenText;
    ProgressBar loader;
    com.google.android.material.button.MaterialButton signInBtn;

    // Captcha
    TextView captchaTextView;
    EditText captchaInput;
    String currentCaptcha = "";

    UserController userController;
    CardView loginCardView;

    TextView forgotPasswordTxt;
    SessionService sessionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        View rootView = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Smoothly push content up when keyboard appears/disappears
        ViewCompat.setWindowInsetsAnimationCallback(rootView,
                new WindowInsetsAnimationCompat.Callback(
                        WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_STOP) {

                    @Override
                    public WindowInsetsCompat onProgress(WindowInsetsCompat insets,
                                                                  List<WindowInsetsAnimationCompat> runningAnimations) {
                        Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
                        Insets barInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        rootView.setPadding(
                                barInsets.left,
                                barInsets.top,
                                barInsets.right,
                                Math.max(imeInsets.bottom, barInsets.bottom)
                        );
                        return insets;
                    }
                });

        sessionService = new SessionService(LoginActivity.this);
        userController = new UserController(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());

        loginCardView = findViewById(R.id.login_card);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        goToSignupScreenText = findViewById(R.id.signup_screen_btn);
        signInBtn = findViewById(R.id.login_btn);
        loader = findViewById(R.id.loader);
        forgotPasswordTxt = findViewById(R.id.forgotPasswordTxt);
        captchaTextView = findViewById(R.id.captcha_text);
        captchaInput = findViewById(R.id.captcha_input);

        generateCaptcha();
        moveToSignupScreen();

        forgotPasswordTxt.setOnClickListener(v->{
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

    }

    //Captcha

    private void generateCaptcha() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        currentCaptcha = sb.toString();
        captchaTextView.setText(currentCaptcha);
        captchaInput.setText("");
    }

    public void refreshCaptcha(View v) {
        generateCaptcha();
    }

    private boolean isCaptchaValid() {
        return captchaInput.getText().toString().trim().equals(currentCaptcha);
    }

    //Auth

    public void login(View v) {
        String email    = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please provide all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isCaptchaValid()) {
            Toast.makeText(this, "Captcha does not match. Try again.", Toast.LENGTH_SHORT).show();
            generateCaptcha();
            return;
        }

        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        toggleLoading(true);

        userController.login(email, password, new UserController.UserCallback<UserModel>() {
            @Override
            public void onSuccess(UserModel data) {
                toggleLoading(false);
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Log.d("USER_DATA", data.toString());

                String name = data.getFirstName() + " " + data.getLastName();
                sessionService.saveUser(
                        data.getUserId(), name, data.getRole(),
                        data.getEmail(), true,
                        data.getVerificationStatus(), data.getDomain()
                );
                moveToScreen(data.getRole(), data.getVerificationStatus());
            }

            @Override
            public void onFailure(String errorMessage) {
                toggleLoading(false);
                generateCaptcha();
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Navigation

    public void moveToSignupScreen() {
        goToSignupScreenText.setOnClickListener(view ->
                startActivity(new Intent(view.getContext(), SignupActivity.class)));
    }

    public void moveToScreen(String role, String verificationStatus) {
        Intent i;
        if ("approved".equals(verificationStatus)) {
            switch (role) {
                case "admin":     i = new Intent(this, AdminDashboardActivity.class);     break;
                case "moderator": i = new Intent(this, ModeratorDashboardActivity.class); break;
                case "vendor":    i = new Intent(this, VendorDashboardActivity.class);    break;
                case "customer":  i = new Intent(this, CustomerDashboardActivity.class);  break;
                default:          i = new Intent(this, PendingVerificationActivity.class);
            }
        } else {
            i = new Intent(this, PendingVerificationActivity.class);
        }
        startActivity(i);
        finish();
    }

    // UI Helpers

    public void showOrHidePasswd(View v) {
        if (eyeFlag == 0) {
            passwordField.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ((ImageView) v.findViewById(R.id.show_hide_passwd_icon))
                    .setImageResource(R.drawable.eye_off);
            eyeFlag = 1;
        } else {
            passwordField.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ((ImageView) v.findViewById(R.id.show_hide_passwd_icon))
                    .setImageResource(R.drawable.eye_on);
            eyeFlag = 0;
        }
        passwordField.setSelection(passwordField.getText().length());
    }

    private void toggleLoading(boolean isLoading) {
        loader.setVisibility(isLoading ? VISIBLE : View.GONE);
        signInBtn.setVisibility(isLoading ? View.INVISIBLE : VISIBLE);
    }
}