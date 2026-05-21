package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.auth.User;

public class PendingVerificationActivity extends AppCompatActivity {
    UserController userController;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    SessionService sessionService;

    Button btnRefresh;
    Button btnLogout;
    ProgressBar loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pending_verification);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sessionService = new SessionService(this);
        userController = new UserController(auth,firestore);

//        initailzed buttons and loader
        btnRefresh = findViewById(R.id.btnRefresh);
        btnLogout = findViewById(R.id.btnLogout);

        loader = findViewById(R.id.loader);

        btnRefresh.setOnClickListener(v->{
            checkApprovalStatus();
        });

        btnLogout.setOnClickListener(v->{
            userController.logout(PendingVerificationActivity.this);
        });
    }

    public void checkApprovalStatus() {
        String userId = sessionService.getUserId();
        String role = sessionService.getUserRole();
        isLoading(true);
        userController.checkStatus(userId, new DatabaseService.DatabaseCallback<UserModel>() {
            @Override
            public void onSuccess(UserModel data) {

                isLoading(false);
                if(data == null) {
                    userController.logout(PendingVerificationActivity.this);
                    return;
                }

                if(data.getVerificationStatus().equals("approved")) {
                   sessionService.updateVerificationStatus(data.getVerificationStatus());
                   Intent i = null;
                   if(role.equals("moderator")) {
                       i = new Intent(PendingVerificationActivity.this, ModeratorDashboardActivity.class);
                   }else if(role.equals("vendor")) {
                       i = new Intent(PendingVerificationActivity.this, VendorDashboardActivity.class);

                   }
                   
                   if(i != null) {
                       startActivity(i);
                       finish();
                   }
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading(false);
                Toast.makeText(PendingVerificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                if(errorMessage.equals("Record Not Found")) {
                    userController.logout(PendingVerificationActivity.this);
                }
            }
        });
    }

    private void isLoading(boolean loading) {
        if(loading) {
            btnRefresh.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
        } else {
            btnRefresh.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);
        }
    }


}