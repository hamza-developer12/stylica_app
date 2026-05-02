package com.example.stylica_app.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminController extends UserController{
    public AdminController(FirebaseAuth auth, FirebaseFirestore firestore) {
        super(auth, firestore);
    }


}
