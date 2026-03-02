package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.text.InputType;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    ImageView backBtnImage;
    int passFlag = 0;
    ImageView passwordIcon;
    ImageView confirmPasswordIcon;
    TextView loginScreenBtnTxt;

    EditText firstNameField;
    EditText lastNameField;
    EditText emailField;
    EditText passwordField;
    EditText confirmPasswordField;
    ProgressBar loader;
    Button registerBtn;
    UserController userController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userController = new UserController(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());


//      GEt View References
        backBtnImage = findViewById(R.id.go_back_btn);
        loginScreenBtnTxt = findViewById(R.id.login_screen_btn_txt);
        firstNameField = findViewById(R.id.first_name_field);
        lastNameField = findViewById(R.id.last_name_field);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        confirmPasswordField = findViewById(R.id.confirm_password_field);
        loader = findViewById(R.id.loader);
        registerBtn = findViewById(R.id.login_btn);
        passwordIcon = findViewById(R.id.password_icon);
        confirmPasswordIcon = findViewById(R.id.confirm_password_icon);
        goBack();
        goToLogin(loginScreenBtnTxt);

        passwordIcon.setOnClickListener(view -> showHidePass());
        confirmPasswordIcon.setOnClickListener(view -> showHidePass());
    }

    public void goBack(){
        backBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void goToLogin(View v){

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void register(View v) {


        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please provide all Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        toggleLoading(true);

        userController.register(firstName, lastName, email, password, new UserController.UserCallback() {

            @Override
            public void onSuccess(String message) {
                toggleLoading(false);
                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                toggleLoading(false);
                Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }

        });


    }


    public void showHidePass(){
        if(passFlag == 0) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordIcon.setImageResource(R.drawable.eye_off);
            confirmPasswordIcon.setImageResource(R.drawable.eye_off);
            passFlag = 1;
        }else{
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordIcon.setImageResource(R.drawable.eye_on);
            confirmPasswordIcon.setImageResource(R.drawable.eye_on);
            passFlag = 0;
        }
        passwordField.setSelection(passwordField.getText().length());
        confirmPasswordField.setSelection(confirmPasswordField.getText().length());
    }
    private void toggleLoading(boolean isLoading) {
        if (isLoading) {
            loader.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.INVISIBLE);
        } else {
            loader.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
        }
    }

}