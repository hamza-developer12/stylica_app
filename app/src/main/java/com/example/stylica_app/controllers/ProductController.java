package com.example.stylica_app.controllers;

import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductController {
    FirebaseFirestore firestore;
    private static ProductController instance;
     DatabaseService<ProductModel> dbService;


    private ProductController() {
        firestore = FirebaseFirestore.getInstance();
        dbService = new DatabaseService<>(firestore);
    }

    public static ProductController getInstance() {
        if(instance == null) {
            instance = new ProductController();
        }

        return instance;
    }

    public void addProduct(){}
    public void updateProduct(){}
    public void deleteProduct(){}
}
