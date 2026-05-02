package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.stylica_app.models.CategoryModel;

public class CategorySpinnerAdapter extends ArrayAdapter<CategoryModel> {
    public CategorySpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
