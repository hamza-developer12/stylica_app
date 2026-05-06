package com.example.stylica_app.controllers;

import com.example.stylica_app.models.PaymentMethodModel;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentController {

    private static final String COLLECTION = "payment_methods";
    private static PaymentController instance;
    DatabaseService<PaymentMethodModel> dbService;

    public interface DeleteCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private PaymentController() {
        dbService = new DatabaseService<>(FirebaseFirestore.getInstance());
    }

    public static PaymentController getInstance() {
        if (instance == null) instance = new PaymentController();
        return instance;
    }


    public void addPaymentMethod(PaymentMethodModel method,
                                 DatabaseService.DatabaseCallback callback) {
        String id = FirebaseFirestore.getInstance()
                .collection(COLLECTION).document().getId();
        method.setId(id);
        dbService.addRecord(COLLECTION, id, method, callback);
    }


    public void getAllPaymentMethods(DatabaseService.DatabaseCallback<List<PaymentMethodModel>> callback) {
        dbService.findAll(COLLECTION, PaymentMethodModel.class, callback);
    }


    public void updatePaymentMethod(String id, String type, String accountTitle,
                                    String accountNumber, String instructions,
                                    UpdateCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("type", type);
        updates.put("accountTitle", accountTitle);
        updates.put("accountNumber", accountNumber);
        updates.put("instructions", instructions);

        dbService.updateRecord(COLLECTION, id, updates,
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String data) { callback.onSuccess(); }
                    @Override
                    public void onFailure(String errorMessage) { callback.onFailure(errorMessage); }
                });
    }


    public void deletePaymentMethod(String id, DeleteCallback callback) {
        dbService.deleteById(COLLECTION, id,
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String data) { callback.onSuccess(); }
                    @Override
                    public void onFailure(String errorMessage) { callback.onFailure(errorMessage); }
                });
    }
}