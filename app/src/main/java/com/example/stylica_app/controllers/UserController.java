package com.example.stylica_app.controllers;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.AuthService;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserController {
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    AuthService authService;
    DatabaseService<UserModel> dbService;

    SessionService sessionService;

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
                        "",true
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

                dbService.findById("users", user.getUid(), UserModel.class, new DatabaseService.DatabaseCallback<UserModel>() {
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

    public void logout(AppCompatActivity context) {
        sessionService = new SessionService(context);
        auth.signOut();
        sessionService.clearUser();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        context.finish();
    }

    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

   public interface UserCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMessage);
   }
}