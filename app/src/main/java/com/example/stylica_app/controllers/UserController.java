package com.example.stylica_app.controllers;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.Api;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.ApiService;
import com.example.stylica_app.services.AuthService;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserController {
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    AuthService authService;
    DatabaseService<UserModel> dbService;

    SessionService sessionService;

    String baseUrl = "https://stylica-backend.vercel.app/api/users/";


    final static String COLLECTION = "users";

    ApiService apiService;

    public UserController(FirebaseAuth auth, FirebaseFirestore firestore) {
        this.auth = auth;
        this.firestore = firestore;
        this.authService = new AuthService(this.auth);
        this.dbService = new DatabaseService<>(this.firestore);

    }

    public void register(String firstName, String lastName, String email, String password, UserCallback userCallback){
        authService.register(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                String userId = firebaseUser.getUid();
                UserModel user = new UserModel(userId,firstName,lastName,"", email,"customer","","",
                        "","approved"
                );
//               Database Logic
                dbService.addRecord(COLLECTION, userId, user, new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        userCallback.onSuccess("Registration Successful");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        userCallback.onFailure(errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                userCallback.onFailure(errorMessage);
            }
        });
    }

    public void login(String email, String password, UserCallback userCallback){

        authService.login(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {

                dbService.findById(COLLECTION, user.getUid(), UserModel.class, new DatabaseService.DatabaseCallback<UserModel>() {
                    @Override
                    public void onSuccess(UserModel data) {
                        Log.d("USER_DATA_FROM_CONTROLLER",data.toString());
                        userCallback.onSuccess(data);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("USER_NOT_FOUND", errorMessage);
                    }
                });

            }

            @Override
            public void onFailure(String errorMessage) {
                userCallback.onFailure(errorMessage);
            }
        });
    }


    public void getUserById(String userId,
                            DatabaseService.DatabaseCallback<UserModel> callback) {
        dbService.findById(COLLECTION, userId, UserModel.class, callback);
    }


    public void updateProfile(String userId, Map<String, Object> updates,
                              UpdateCallback callback) {
        dbService.updateRecord(COLLECTION, userId, updates,
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.onFailure(errorMessage);
                    }
                });
    }

    public void getPendingUsers(DatabaseService.RealtimeCallback callback) {
        Map conditions = new HashMap();
        conditions.put("verificationStatus","pending" );
        dbService.listenWhere(COLLECTION, conditions, UserModel.class, callback);
    }

    public void changeStatus(String userId,String verificationStatus,DatabaseService.DatabaseCallback callback) {
        Map fields = new HashMap();
        fields.put("verificationStatus", verificationStatus);
        dbService.updateRecord(COLLECTION,userId,fields,callback);
    }

    public void initializeApiService(Context context) {
        apiService = ApiService.getInstance(context);
    }
    public void rejectUser( String userId, ApiService.ApiCallback callback) {
        apiService.delete(baseUrl+userId,callback);
    }

    public void logout(AppCompatActivity context) {
        sessionService = new SessionService(context);
        auth.signOut();
        sessionService.clearUser();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        context.finish();
    }

    public void registerPartner(String firstName, String lastName,
                                String email, String password,
                                String role, String domain,
                                UserCallback callback) {
        authService.register(email, password,
                new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(FirebaseUser firebaseUser) {
                        String userId = firebaseUser.getUid();

                        UserModel user = new UserModel(
                                userId, firstName, lastName,
                                "", email, role, "", "",
                                domain, "pending");

                        dbService.addRecord(COLLECTION, userId, user,
                                new DatabaseService.DatabaseCallback<String>() {
                                    @Override
                                    public void onSuccess(String data) {
                                        callback.onSuccess(
                                                "Registration Successful");
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        callback.onFailure(error);
                                    }
                                });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.onFailure(errorMessage);
                    }
                });
    }

    public void checkStatus(String userId, DatabaseService.DatabaseCallback callback) {
        dbService.findById(COLLECTION, userId, UserModel.class,callback);
    }
    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

   public interface UserCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMessage);
   }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }


}