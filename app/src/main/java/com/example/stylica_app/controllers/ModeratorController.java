package com.example.stylica_app.controllers;

import android.content.Context;

import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.ApiService;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ModeratorController {
    String baseUrl = "https://stylica-backend.vercel.app/api";
    String addUserUrl = baseUrl+"/users/create-user";
    private static ModeratorController instance;

    final String COLLECTION = "users";

    DatabaseService<UserModel> dbService;
    boolean hasError = false;
    String errorMessage = "";
    ApiService apiService;

    CategoryController categoryController = CategoryController.getInstance();
    Context context;

    List<UserModel> moderators = new ArrayList<>();
    private ModeratorController(Context context){
        dbService = new DatabaseService<>(FirebaseFirestore.getInstance());
        apiService = ApiService.getInstance(context);
    }

    public static ModeratorController getInstance(Context ctx) {
        if(instance == null) {
            instance = new ModeratorController(ctx);

        }
        return instance;
    }


    public void getModerators(DatabaseService.RealtimeCallback<List<UserModel>> callback){
        dbService.listenWhere(COLLECTION, "role", "moderator", UserModel.class,callback);
    }
    public void addModerator(String firstName, String lastName, String email, String password, String domain, ModeratorCallback callback)  {

        try{
            JSONObject body = new JSONObject();
            body.put("firstName", firstName);
            body.put("lastName", lastName);
            body.put("email", email);
            body.put("password", password);
            body.put("domain", domain);

            apiService.post(addUserUrl, body, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSuccess("Moderator Added Successfully");
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

    public interface ModeratorCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
        void onApiError(JSONObject error);
    }

}
