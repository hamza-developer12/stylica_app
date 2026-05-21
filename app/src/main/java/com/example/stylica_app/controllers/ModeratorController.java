package com.example.stylica_app.controllers;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.ApiService;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModeratorController {
    String baseUrl = "https://stylica-backend.vercel.app/api";
    String addUserUrl = baseUrl+"/users/create-user";
    private static ModeratorController instance;

    final String COLLECTION = "users";

    DatabaseService<UserModel> dbService;
    SessionService sessionService;
    boolean hasError = false;
    String errorMessage = "";
    ApiService apiService;

    CategoryController categoryController = CategoryController.getInstance();


    List<UserModel> moderators = new ArrayList<>();

    FirebaseAuth auth;
    private ModeratorController(Context context){
        dbService = new DatabaseService<>(FirebaseFirestore.getInstance());
        apiService = ApiService.getInstance(context);
        sessionService = new SessionService(context);
        auth = FirebaseAuth.getInstance();
    }

    public static ModeratorController getInstance(Context ctx) {
        if(instance == null) {
            instance = new ModeratorController(ctx);

        }
        return instance;
    }


    public void getModerators(DatabaseService.RealtimeCallback<List<UserModel>> callback){
        Map conditions = new HashMap();
        conditions.put("verificationStatus", "approved");
        conditions.put("role", "moderator");
        dbService.listenWhere(COLLECTION, conditions, UserModel.class,callback);
    }
    public void addModerator(String firstName, String lastName, String email, String password, String domain, ModeratorCallback callback)  {

        try{
            JSONObject body = new JSONObject();
            body.put("firstName", firstName);
            body.put("lastName", lastName);
            body.put("email", email);
            body.put("password", password);
            body.put("domain", domain);
            body.put("role", "moderator");
            body.put("verificationStatus", "approved");

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

    public void deleteModerator(String userId, DatabaseService.DatabaseCallback<String> callback) {
        dbService.deleteById(COLLECTION, userId, callback);
    }

    public interface ModeratorCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
        void onApiError(JSONObject error);
    }


        public void logout(AppCompatActivity context) {
            sessionService = new SessionService(context);
            auth.signOut();
            sessionService.clearUser();
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            context.finish();
        }


}
