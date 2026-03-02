package com.example.stylica_app.controllers;


import com.example.stylica_app.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class Demo {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    public Demo(FirebaseAuth auth, FirebaseFirestore firestore){
        this.auth = auth;
        this.firestore = firestore;
    }

    public void register(String firstName, String lastName, String email, String password, RegistrationCallback callback){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful())  {
                String userId =  auth.getUid();
                UserModel user = new UserModel(userId,firstName,lastName,"", email,"","","","",
                        null,null
                        );
                if(userId != null) {
                    firestore.collection("users").document(userId).set(user).addOnSuccessListener(runnable -> {
                        callback.onSuccess();
                    }).addOnFailureListener(e->{
                        callback.onFailure(e.getMessage());
                    });
                }else {
                    callback.onFailure("Invalid User Id");
                }
            }
        }).addOnFailureListener(e->{
            callback.onFailure(e.getMessage());
        });
    }
    public interface RegistrationCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
