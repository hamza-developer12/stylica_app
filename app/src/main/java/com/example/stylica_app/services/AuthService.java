package com.example.stylica_app.services;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public final class AuthService {
    private final FirebaseAuth auth;


    public AuthService(FirebaseAuth auth) {
        this.auth = auth;
    }
    public void register(String email, String password, AuthCallback callback){
       auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
           FirebaseUser user = authResult.getUser();
           if(user != null) {
               callback.onSuccess(user);
           }else {
               callback.onFailure("Unexpected error User is Null");
           }
       }).addOnFailureListener(e->{
           callback.onFailure(e.getMessage());
       });
    }

    public void login(String email,String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
            FirebaseUser user = authResult.getUser();
            if(user != null) {
                callback.onSuccess(user);
            }else {
                callback.onFailure("Unexpected error User is Null");
            }
        }).addOnFailureListener(e->{
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                callback.onFailure("Invalid credentials");
            } else if (e instanceof FirebaseAuthInvalidUserException) {
                callback.onFailure("Invalid credentials");
            } else {
                callback.onFailure("Login failed: " + e.getMessage());
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }
}
