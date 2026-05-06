package com.example.stylica_app.services;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionService {


    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGGEDIN = "isLoggedIn";
    private static final String KEY_VERIFIED = "verified";
    private static  final String KEY_DOMAIN = "domain";
    private static final String KEY_USER_NAME = "name";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public SessionService(Context context){
        prefs = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUser(String userId, String name, String role, String email,boolean isLoggedIn,  boolean verified, String domain) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_VERIFIED, verified);
        editor.putBoolean(KEY_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_DOMAIN, domain);
        editor.putString(KEY_USER_NAME, name);
        editor.apply(); // async commit
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }
    public String getUserRole() {
        return prefs.getString(KEY_ROLE, "");
    }
    public String getUserEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public boolean getUserVerifiedStatus() {
        return prefs.getBoolean(KEY_VERIFIED, false);
    }
    public boolean getUserLoggedInStatus() {
        return prefs.getBoolean(KEY_LOGGEDIN, false);
    }

    public String getUserName(){return prefs.getString(KEY_USER_NAME, "");}

    public String getDomain() {
        return prefs.getString(KEY_DOMAIN,"");
    }
    public void clearUser() {
        editor.clear();
        editor.apply();
    }
}
