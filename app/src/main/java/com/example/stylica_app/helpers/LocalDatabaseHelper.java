package com.example.stylica_app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    SharedPreferences.Editor editor;

    private static final String DATABASE_NAME = "stylica_app";
    private static final int DATABASE_VERSION = 1;

    public LocalDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "userId TEXT PRIMARY KEY," +
                "firstName TEXT," +
                "lastName TEXT," +
                "gender TEXT," +
                "domain TEXT," +
                "email TEXT," +
                "role TEXT," +
                "contactNumber TEXT," +
                "address TEXT," +
                "verified INTEGER DEFAULT 0," +
                "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
