package com.example.stylica_app.controllers;

import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.CourierModel;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourierController {

    private static CourierController instance;

    FirebaseFirestore firestore;
    DatabaseService<CourierModel> dbService;

    private static String COLLECTION = "couriers";


    private CourierController(){
        firestore = FirebaseFirestore.getInstance();
        dbService = new DatabaseService<CourierModel>(firestore);
    }

    public static CourierController getInstance() {
        if(instance == null) {
            instance = new CourierController();
        }

        return instance;
    }


    public void addCourier(String courierName, String phoneNumber, String email, DatabaseService.DatabaseCallback<String> callback) {
        String courierId = firestore.collection(COLLECTION).document().getId();

        CourierModel courier = new CourierModel(courierId,courierName,phoneNumber,email, null,null );

        dbService.recordExists(COLLECTION, "email", email, exists->{
            if(exists) {
                callback.onFailure("Courier With This Email Already Exists");
            }else {
                dbService.addRecord(COLLECTION,courierId, courier,callback);
            }

        });

    }


    public void listenForCouriers(DatabaseService.RealtimeCallback<List<CourierModel>> callback) {
        dbService.listenAll(COLLECTION, CourierModel.class, callback);
    }

    public void updateCourier(String courierId, String courierName, String phoneNumber, String email, DatabaseService.DatabaseCallback<String> callback) {

        Map data = new HashMap();
        data.put("courierName",courierName);
        data.put("email", email);
        data.put("phoneNumber", phoneNumber);
        dbService.updateRecord(COLLECTION, courierId, data, callback);
    }

    public void deleteCourier(String courierId, DatabaseService.DatabaseCallback<String> callback){
        dbService.deleteById(COLLECTION, courierId, callback);
    }
}
