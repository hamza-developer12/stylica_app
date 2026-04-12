package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.stylica_app.models.UserModel;

public class ModeratorAdapter extends ArrayAdapter<UserModel> {
    public ModeratorAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return convertView;
    }
}
