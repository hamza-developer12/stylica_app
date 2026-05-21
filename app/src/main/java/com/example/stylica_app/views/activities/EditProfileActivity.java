package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.controllers.UserController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    TextView txtAvatar, txtFullName, txtRole;
    EditText edtFirstName, edtLastName, edtEmail,
            edtContactNumber, edtAddress;
    Spinner spinnerGender, spinnerDomain;
    Button btnSave;
    ProgressBar loader, saveLoader;
    LinearLayout formLayout, professionalSection;

    SessionService sessionService;
    UserController userController;
    CategoryController categoryController;
    String intentUserId;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    UserModel currentUser;
    List<String> categoryNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        sessionService     = new SessionService(this);
        userController     = new UserController(auth, firestore);
        categoryController = CategoryController.getInstance();
        intentUserId = getIntent().getStringExtra("userId");
        setupAppBar("Edit Profile");

        txtAvatar          = findViewById(R.id.txtAvatar);
        txtFullName        = findViewById(R.id.txtFullName);
        txtRole            = findViewById(R.id.txtRole);
        edtFirstName       = findViewById(R.id.edtFirstName);
        edtLastName        = findViewById(R.id.edtLastName);
        edtEmail           = findViewById(R.id.edtEmail);
        edtContactNumber   = findViewById(R.id.edtContactNumber);
        edtAddress         = findViewById(R.id.edtAddress);
        spinnerGender      = findViewById(R.id.spinnerGender);
        spinnerDomain      = findViewById(R.id.spinnerDomain);
        btnSave            = findViewById(R.id.btnSave);
        loader             = findViewById(R.id.loader);
        saveLoader         = findViewById(R.id.saveLoader);
        formLayout         = findViewById(R.id.formLayout);
        professionalSection = findViewById(R.id.professionalSection);

        setupGenderSpinner();

        loadCategoriesThenUser();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void setupGenderSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Male", "Female", "Other"));
        spinnerGender.setAdapter(adapter);
    }

    private void loadCategoriesThenUser() {
        loader.setVisibility(View.VISIBLE);
        formLayout.setVisibility(View.GONE);

        categoryController.getAllCategories(
                new DatabaseService.DatabaseCallback<List<CategoryModel>>() {
                    @Override
                    public void onSuccess(List<CategoryModel> data) {
                        categoryNames.clear();
                        for (CategoryModel cat : data) {
                            if (cat.getCategoryName() != null
                                    && !cat.getCategoryName().isEmpty()) {
                                categoryNames.add(cat.getCategoryName());
                            }
                        }
                        spinnerDomain.setAdapter(new ArrayAdapter<>(
                                EditProfileActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                categoryNames));

                        loadUser();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loadUser(); // still load user even if categories fail
                    }
                });
    }

    private void loadUser() {
        String userIdToLoad = (intentUserId != null && !intentUserId.isEmpty())
                ? intentUserId
                : sessionService.getUserId();
        userController.getUserById(userIdToLoad,
                new DatabaseService.DatabaseCallback<UserModel>() {
                    @Override
                    public void onSuccess(UserModel user) {
                        loader.setVisibility(View.GONE);
                        currentUser = user;
                        fillForm(user);
                        formLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(EditProfileActivity.this,
                                "Failed to load profile",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fillForm(UserModel user) {
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName  = user.getLastName()  != null ? user.getLastName()  : "";

        txtAvatar.setText(firstName.isEmpty() ? "?"
                : String.valueOf(firstName.charAt(0)).toUpperCase());
        txtFullName.setText(firstName + " " + lastName);
        txtRole.setText(user.getRole() != null ? user.getRole() : "");

        edtFirstName.setText(firstName);
        edtLastName.setText(lastName);
        edtEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        edtContactNumber.setText(user.getContactNumber() != null
                ? user.getContactNumber() : "");
        edtAddress.setText(user.getAddress() != null
                ? user.getAddress() : "");

        if (user.getGender() != null) {
            int pos = Arrays.asList("Male", "Female", "Other")
                    .indexOf(user.getGender());
            if (pos >= 0) spinnerGender.setSelection(pos);
        }

        boolean isOwnProfile =
                intentUserId == null ||
                        intentUserId.equals(sessionService.getUserId());

        if ("moderator".equalsIgnoreCase(user.getRole())
                || "vendor".equalsIgnoreCase(user.getRole())) {

            // Hide domain section if user is viewing own profile
            if (isOwnProfile) {
                professionalSection.setVisibility(View.GONE);
            } else {

                // Admin editing another user's profile
                professionalSection.setVisibility(View.VISIBLE);

                if (user.getDomain() != null && !categoryNames.isEmpty()) {
                    int pos = categoryNames.indexOf(user.getDomain());

                    if (pos >= 0) {
                        spinnerDomain.setSelection(pos);
                    }
                }
            }

        } else {
            professionalSection.setVisibility(View.GONE);
        }
    }

    private void saveProfile() {
        String firstName = edtFirstName.getText().toString().trim();
        String lastName  = edtLastName.getText().toString().trim();
        String gender    = spinnerGender.getSelectedItem().toString();
        String contact   = edtContactNumber.getText().toString().trim();
        String address   = edtAddress.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this,
                    "Please enter first and last name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        saving(true);

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName",     firstName);
        updates.put("lastName",      lastName);
        updates.put("gender",        gender);
        updates.put("contactNumber", contact);
        updates.put("address",       address);

        if ("moderator".equalsIgnoreCase(currentUser.getRole())
                && !categoryNames.isEmpty()) {
            updates.put("domain",
                    spinnerDomain.getSelectedItem().toString());
        }

        String userIdToUpdate = (intentUserId != null && !intentUserId.isEmpty())
                ? intentUserId
                : sessionService.getUserId();
        userController.updateProfile(userIdToUpdate, updates,
                new UserController.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        saving(false);
                        txtAvatar.setText(String.valueOf(
                                firstName.charAt(0)).toUpperCase());
                        txtFullName.setText(firstName + " " + lastName);
                        Toast.makeText(EditProfileActivity.this,
                                "Profile updated",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        saving(false);
                        Toast.makeText(EditProfileActivity.this,
                                "Failed: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saving(boolean isSaving) {
        saveLoader.setVisibility(isSaving ? View.VISIBLE : View.GONE);
        btnSave.setVisibility(isSaving ? View.GONE : View.VISIBLE);
    }
}