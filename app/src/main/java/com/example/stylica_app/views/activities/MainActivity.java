package com.example.stylica_app.views.activities;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.UserController;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
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
    WebView webView;
    CardView loginCardView;
    LinearLayout webContainer;
    boolean waitingForCaptcha = false;
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

        loginCardView = findViewById(R.id.login_card);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        webView = findViewById(R.id.webview);
        goToSignupScreenText = findViewById(R.id.signup_screen_btn);
        signInBtn = findViewById(R.id.login_btn);
        loader = findViewById(R.id.loader);
        webContainer = findViewById(R.id.web_container);

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



//    public void login(View v){
//
//        String email = emailField.getText().toString().trim();
//        String password = passwordField.getText().toString().trim();
//
//        if(email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please provide all Fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        webView.setVisibility(View.VISIBLE);
//        webView.getSettings().setJavaScriptEnabled(true);

//        toggleLoading(true);
//        webView.loadUrl("https://dulcet-sprite-b1b821.netlify.app/");
//
//        userController.login(email, password, new UserController.UserCallback() {
//            @Override
//            public void onSuccess(String message) {
//                toggleLoading(false);
//                Toast.makeText(MainActivity.this,message, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                toggleLoading(false);
//                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
public void login(View v) {
    String email = emailField.getText().toString().trim();
    String password = passwordField.getText().toString().trim();

    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(this, "Please provide all Fields", Toast.LENGTH_SHORT).show();
        return;
    }

    // Show WebView and start captcha flow
    waitingForCaptcha = true;
    webContainer.setVisibility(View.VISIBLE);

    webView.getSettings().setJavaScriptEnabled(true);

    webView.addJavascriptInterface(new Object(){
        @android.webkit.JavascriptInterface
        public void sendToken(String token){
           runOnUiThread(()-> onCaptchaTokenReceived(token));
        }
    },"Android");
    // Load the captcha page
    webView.loadUrl("https://dulcet-sprite-b1b821.netlify.app/");


}
    private void toggleLoading(boolean isLoading) {
        if (isLoading) {
            loader.setVisibility(VISIBLE);
            signInBtn.setVisibility(View.INVISIBLE);
        } else {
            loader.setVisibility(View.INVISIBLE);
            signInBtn.setVisibility(VISIBLE);
        }
    }
    private void onCaptchaTokenReceived(String token) {
        Log.d("TOKEN_FOR_CAPTCHA", token);
        if(!token.isEmpty()) {
            webContainer.setVisibility(View.GONE);
            performLoginWithCaptcha(token);

        }else {

            webContainer.setVisibility(View.GONE);
            Toast.makeText(this, "Captcha Verification Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void performLoginWithCaptcha(String captchaToken) {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        toggleLoading(true);

        // Pass the token to your UserController
        userController.login(email, password, new UserController.UserCallback() {
            @Override
            public void onSuccess(String message) {
                toggleLoading(false);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                toggleLoading(false);
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void closeWebView(View view) {
        webContainer.setVisibility(View.INVISIBLE);
    }
}