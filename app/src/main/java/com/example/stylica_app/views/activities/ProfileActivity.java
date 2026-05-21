package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.UserController;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends BaseActivity {

    TextView txtAvatar, txtFullName, txtRole,
            txtFirstName, txtLastName, txtGender,
            txtEmail, txtDomain, txtContactNumber, txtAddress;
    Button btnEditProfile;
    ProgressBar loader;
    LinearLayout contentLayout, professionalSection;

    SessionService sessionService;
    UserController userController;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    String viewUserId = null;
    boolean isViewingOther = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sessionService  = new SessionService(this);

        userController  = new UserController(auth,firestore);

        viewUserId     = getIntent().getStringExtra("userId");
        isViewingOther = viewUserId != null
                && !viewUserId.equals(sessionService.getUserId());

        setupAppBar(isViewingOther ? "Profile" : "My Profile");

        txtAvatar = findViewById(R.id.txtAvatar);
        txtFullName = findViewById(R.id.txtFullName);
        txtRole = findViewById(R.id.txtRole);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtGender = findViewById(R.id.txtGender);
        txtEmail = findViewById(R.id.txtEmail);
        txtDomain = findViewById(R.id.txtDomain);
        txtContactNumber = findViewById(R.id.txtContactNumber);
        txtAddress = findViewById(R.id.txtAddress);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        loader = findViewById(R.id.loader);
        contentLayout = findViewById(R.id.contentLayout);
        professionalSection = findViewById(R.id.professionalSection);

        if (!isViewingOther) {
            btnEditProfile.setVisibility(View.VISIBLE);
            btnEditProfile.setOnClickListener(v ->
                    startActivity(new Intent(this, EditProfileActivity.class)));
        }

        loadUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    private void loadUser() {
        loader.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        String userIdToLoad = isViewingOther
                ? viewUserId
                : sessionService.getUserId();

        userController.getUserById(userIdToLoad,
                new DatabaseService.DatabaseCallback<UserModel>() {
                    @Override
                    public void onSuccess(UserModel user) {
                        loader.setVisibility(View.GONE);
                        fillProfile(user);
                        contentLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this,
                                "Failed to load profile",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fillProfile(UserModel user) {
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName  = user.getLastName()  != null ? user.getLastName()  : "";

        txtAvatar.setText(firstName.isEmpty() ? "?"
                : String.valueOf(firstName.charAt(0)).toUpperCase());
        txtFullName.setText(firstName + " " + lastName);
        txtRole.setText(user.getRole() != null ? user.getRole() : "");
        txtFirstName.setText(firstName.isEmpty() ? "—" : firstName);
        txtLastName.setText(lastName.isEmpty() ? "—" : lastName);
        txtGender.setText(user.getGender() != null ? user.getGender() : "—");
        txtEmail.setText(user.getEmail() != null ? user.getEmail() : "—");
        txtContactNumber.setText(user.getContactNumber() != null
                ? user.getContactNumber() : "—");
        txtAddress.setText(user.getAddress() != null
                ? user.getAddress() : "—");

        if ("moderator".equalsIgnoreCase(user.getRole())) {
            professionalSection.setVisibility(View.VISIBLE);
            txtDomain.setText(user.getDomain() != null
                    ? user.getDomain() : "—");
        } else {
            professionalSection.setVisibility(View.GONE);
        }
    }
}