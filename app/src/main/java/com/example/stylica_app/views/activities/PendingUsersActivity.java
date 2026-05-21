package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.UserController;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.ApiService;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.adapters.PendingUsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PendingUsersActivity extends BaseActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    UserController userController;
    RecyclerView pendingUsersView;
    ProgressBar loader;
    View emptyView;

    PendingUsersAdapter adapter;

    List<UserModel> users = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pending_users);


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setupAppBar("Pending Users");

        userController = new UserController(auth,firestore);

        pendingUsersView = findViewById(R.id.pendingUsersView);
        loader = findViewById(R.id.loader);
        emptyView = findViewById(R.id.emptyView);
        pendingUsersView.setLayoutManager(new LinearLayoutManager(this));

        userController.initializeApiService(this);
        getPendingUsers();

    }

    public void getPendingUsers() {
        loading(true);
        userController.getPendingUsers(new DatabaseService.RealtimeCallback<List<UserModel>>() {
            @Override
            public void onDataChange(List<UserModel> data) {
                loading(false);

                if (data == null || data.isEmpty()) {
                    showEmpty(true);
                    return;
                }

                showEmpty(false);
                users = data;
                adapter = new PendingUsersAdapter(PendingUsersActivity.this, users, new PendingUsersAdapter.OnActionListener() {
                    @Override
                    public void onApprove(UserModel user, int position) {
                        userController.changeStatus(user.getUserId(), "approved", new DatabaseService.DatabaseCallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                adapter.removeItem(position);

                                Toast.makeText(PendingUsersActivity.this, "User Approved ", Toast.LENGTH_SHORT).show();
                                if(adapter.getItemCount() == 0) {
                                    showEmpty(true);
                                }
                            }


                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(
                                        PendingUsersActivity.this,
                                        "Failed: " + errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onReject(UserModel user, int position) {
                        userController.rejectUser(user.getUserId(), new ApiService.ApiCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                adapter.removeItem(position);
                                Toast.makeText(PendingUsersActivity.this, "User Rejected Successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(JSONObject error) {
                                Toast.makeText(
                                        PendingUsersActivity.this,
                                        "Failed: " + "Something went wrong",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                pendingUsersView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                loading(false);
                Log.e("PENDINGUSERSPENDING", errorMessage);
                Toast.makeText(PendingUsersActivity.this,
                        "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            loader.setVisibility(View.VISIBLE);
            pendingUsersView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        } else {
            loader.setVisibility(View.GONE);
            pendingUsersView.setVisibility(View.VISIBLE);
        }
    }

    private void showEmpty(boolean isEmpty) {
        if (isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            pendingUsersView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            pendingUsersView.setVisibility(View.VISIBLE);
        }
    }
}