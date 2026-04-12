package com.example.stylica_app.controllers;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.activities.AddCategoryActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public  class CategoryController {

    private static CategoryController instance;

    final String COLLECTION = "categories";

    DatabaseService<CategoryModel> dbService;

    ArrayList<CategoryModel> categories = new ArrayList<CategoryModel>();

    private CategoryController() {
        dbService = new DatabaseService<>(FirebaseFirestore.getInstance());
    }
    public static synchronized CategoryController getInstance() {
        if (instance == null) {
            instance = new CategoryController();
        }
        return instance;
    }


    public void addCategory(String categoryName, ArrayList<String> categories, DatabaseService.DatabaseCallback<String> callback) {
        String id = FirebaseFirestore.getInstance().collection(COLLECTION).document().getId();
        CategoryModel category = new CategoryModel(id, categoryName, categories, null, null);
//        dbService.addRecord(COLLECTION, id,category, callback<String>);
        dbService.addRecord(COLLECTION, id, category, callback);
    }

    public void getCategories(DatabaseService.RealtimeCallback<List<CategoryModel>> callback) {
        dbService.listenAll(COLLECTION, CategoryModel.class, callback);
    }

    public void updateCategory(String categoryId, String categoryName, ArrayList<String> categories, DatabaseService.DatabaseCallback<String> callback) {
        CategoryModel category = new CategoryModel(categoryId, categoryName, categories, null, null);
        dbService.updateRecord(COLLECTION, categoryId, category, callback);
    }

    public void deleteCategory(String categoryId, DatabaseService.DatabaseCallback<String> callback){
        dbService.deleteById(COLLECTION, categoryId, callback);
    }
}
