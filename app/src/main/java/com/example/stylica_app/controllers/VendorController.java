package com.example.stylica_app.controllers;

import android.content.Context;

import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.ApiService;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendorController extends UserController{

    private static VendorController instance;

    String baseUrl = "https://stylica-backend.vercel.app/api";
    String addUserUrl = baseUrl+"/users/create-user";
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    ApiService apiService;

    Context context;

    // Collection name constant
    private static final String COLLECTION = "users";

    public VendorController(FirebaseAuth auth, FirebaseFirestore firestore, Context context) {
        super(auth, firestore);
        this.firestore = firestore;
        this.auth = auth;
        this.context = context;
        apiService = ApiService.getInstance(context);
    }
    public static synchronized VendorController getInstance(Context context) {
        if (instance == null) {
            instance = new VendorController(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), context);
        }
        return instance;
    }




    public void getAllVendors(DatabaseService.DatabaseCallback callback) {
        Map conditions = new HashMap();
        conditions.put("verificationStatus", "approved");
        conditions.put("role", "vendor");
        dbService.findWhereMultipleConditions(COLLECTION, conditions, UserModel.class, callback);
    }

    public void listenForVendors(DatabaseService.RealtimeCallback callback) {
        Map conditions = new HashMap();
        conditions.put("verificationStatus", "approved");
        conditions.put("role", "vendor");
        dbService.listenWhere(COLLECTION, conditions, UserModel.class, callback);
    }

    public void addVendor(String firstName, String lastName, String email, String password, String domain, VendorCallback callback)  {

        try{
            JSONObject body = new JSONObject();
            body.put("firstName", firstName);
            body.put("lastName", lastName);
            body.put("email", email);
            body.put("password", password);
            body.put("domain", domain);
            body.put("role", "vendor");
            body.put("verificationStatus", "approved");

            apiService.post(addUserUrl, body, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSuccess("Vendor Added Successfully");
                }

                @Override
                public void onError(JSONObject error) {
                    callback.onApiError(error);
                }
            });
        }catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void deleteVendor(String userId, DatabaseService.DatabaseCallback<String> callback) {
        dbService.deleteById(COLLECTION, userId, callback);
    }

    public interface VendorCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
        void onApiError(JSONObject error);
    }
}