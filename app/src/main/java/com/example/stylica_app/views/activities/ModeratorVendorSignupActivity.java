package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.controllers.UserController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ModeratorVendorSignupActivity extends AppCompatActivity {

    // Views
    ImageView backBtnImage;
    EditText firstNameField, lastNameField, emailField,
            passwordField, confirmPasswordField;
    Spinner spinnerDomain;
    TextView chipModerator, chipVendor;
    TextView loginScreenBtnTxt;
    ImageView passwordIcon, confirmPasswordIcon;
    ProgressBar loader;
    com.google.android.material.button.MaterialButton registerBtn;

    // Controllers
    UserController userController;
    CategoryController categoryController;

    // Data
    String selectedRole = "moderator";
    List<String> categoryNames = new ArrayList<>();
    int passFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_moderator_vendor_signup);
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        userController    = new UserController(
                FirebaseAuth.getInstance(),
                FirebaseFirestore.getInstance());
        categoryController = CategoryController.getInstance();

        // Init views
        backBtnImage         = findViewById(R.id.go_back_btn);
        firstNameField       = findViewById(R.id.first_name_field);
        lastNameField        = findViewById(R.id.last_name_field);
        emailField           = findViewById(R.id.email_field);
        passwordField        = findViewById(R.id.password_field);
        confirmPasswordField = findViewById(R.id.confirm_password_field);
        spinnerDomain        = findViewById(R.id.spinnerDomain);
        chipModerator        = findViewById(R.id.chipModerator);
        chipVendor           = findViewById(R.id.chipVendor);
        loginScreenBtnTxt    = findViewById(R.id.login_screen_btn_txt);
        passwordIcon         = findViewById(R.id.password_icon);
        confirmPasswordIcon  = findViewById(R.id.confirm_password_icon);
        loader               = findViewById(R.id.loader);
        registerBtn          = findViewById(R.id.register_btn);

        // Back button
        backBtnImage.setOnClickListener(v -> finish());
        loginScreenBtnTxt.setOnClickListener(v -> finish());

        // Password show/hide
        passwordIcon.setOnClickListener(v -> showHidePass());
        confirmPasswordIcon.setOnClickListener(v -> showHidePass());

        // Role chips
        setupRoleChips();

        // Load categories for domain spinner
        loadCategories();

        // Register button
        registerBtn.setOnClickListener(v -> register());
    }

    private void setupRoleChips() {
        chipModerator.setOnClickListener(v -> {
            selectedRole = "moderator";
            chipModerator.setBackgroundResource(R.drawable.chip_selected_bg);
            chipModerator.setTextColor(getColor(R.color.text_white));
            chipVendor.setBackgroundResource(R.drawable.chip_unselected_bg);
            chipVendor.setTextColor(getColor(R.color.text_secondary));
        });

        chipVendor.setOnClickListener(v -> {
            selectedRole = "vendor";
            chipVendor.setBackgroundResource(R.drawable.chip_selected_bg);
            chipVendor.setTextColor(getColor(R.color.text_white));
            chipModerator.setBackgroundResource(R.drawable.chip_unselected_bg);
            chipModerator.setTextColor(getColor(R.color.text_secondary));
        });
    }


    private void loadCategories() {
        categoryController.getAllCategories(
                new DatabaseService.DatabaseCallback<List<CategoryModel>>() {
                    @Override
                    public void onSuccess(List<CategoryModel> data) {
                        categoryNames.clear();
                        categoryNames.add("Select Domain");
                        for (CategoryModel cat : data) {
                            if (cat.getCategoryName() != null) {
                                categoryNames.add(cat.getCategoryName());
                            }
                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(
                                        ModeratorVendorSignupActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        categoryNames);
                        spinnerDomain.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ModeratorVendorSignupActivity.this,
                                "Failed to load categories",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void register() {
        String firstName      = firstNameField.getText().toString().trim();
        String lastName       = lastNameField.getText().toString().trim();
        String email          = emailField.getText().toString().trim();
        String password       = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();
        int domainPosition    = spinnerDomain.getSelectedItemPosition();
        String domain         = domainPosition > 0
                ? categoryNames.get(domainPosition) : "";

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty()
                || email.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (domainPosition == 0) {
            Toast.makeText(this, "Please select a domain",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        toggleLoading(true);

        userController.registerPartner(
                firstName, lastName, email, password,
                selectedRole, domain,
                new UserController.UserCallback<String>() {
                    @Override
                    public void onSuccess(String message) {
                        toggleLoading(false);
                        Toast.makeText(ModeratorVendorSignupActivity.this,
                                "Registration successful! Wait for admin approval.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        toggleLoading(false);
                        Toast.makeText(ModeratorVendorSignupActivity.this,
                                errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showHidePass() {
        if (passFlag == 0) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmPasswordField.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordIcon.setImageResource(R.drawable.eye_off);
            confirmPasswordIcon.setImageResource(R.drawable.eye_off);
            passFlag = 1;
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordField.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordIcon.setImageResource(R.drawable.eye_on);
            confirmPasswordIcon.setImageResource(R.drawable.eye_on);
            passFlag = 0;
        }
        passwordField.setSelection(passwordField.getText().length());
        confirmPasswordField.setSelection(
                confirmPasswordField.getText().length());
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