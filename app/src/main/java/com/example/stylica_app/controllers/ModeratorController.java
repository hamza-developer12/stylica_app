package com.example.stylica_app.controllers;

import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModeratorController {

    private static ModeratorController instance;

    final String COLLECTION = "users";

    DatabaseService<UserModel> dbService;

    private ModeratorController(){
        dbService = new DatabaseService<>(FirebaseFirestore.getInstance());
    }

    public static ModeratorController getInstance() {
        if(instance == null) {
            instance = new ModeratorController();
        }
        return instance;
    }


    public void addModerator(){

    }

}
