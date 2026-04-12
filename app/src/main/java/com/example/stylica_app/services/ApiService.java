package com.example.stylica_app.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ApiService {

    private static ApiService instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private ApiService(Context context) {
        ctx = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    // 🔥 GET Request
    public void get(String url, ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> callback.onSuccess(response),
                error -> callback.onError(error.toString())
        );

        addToRequestQueue(request);
    }

    // 🔥 POST Request
    public void post(String url, JSONObject body, ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> callback.onSuccess(response),
                error -> callback.onError(error.toString())
        );

        addToRequestQueue(request);
    }

    // 🔥 PUT Request
    public void put(String url, JSONObject body, ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                response -> callback.onSuccess(response),
                error -> callback.onError(error.toString())
        );

        addToRequestQueue(request);
    }

    // 🔥 DELETE Request
    public void delete(String url, ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> callback.onSuccess(response),
                error -> callback.onError(error.toString())
        );

        addToRequestQueue(request);
    }

    // 🔥 Callback Interface
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
}