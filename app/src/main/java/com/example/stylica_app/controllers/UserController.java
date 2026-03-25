package com.example.stylica_app.controllers;


import android.util.Log;

import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.AuthService;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserController {
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    AuthService authService;
    DatabaseService<UserModel> dbService;


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
                        null,null
                );
//                Database Logic
                dbService.addRecord("users", userId, user, new DatabaseService.DatabaseCallback<String>() {
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
                userCallback.onSuccess("Login Successful");
            }

            @Override
            public void onFailure(String errorMessage) {
                userCallback.onFailure(errorMessage);
            }
        });
    }

    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

   public interface UserCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
   }
}