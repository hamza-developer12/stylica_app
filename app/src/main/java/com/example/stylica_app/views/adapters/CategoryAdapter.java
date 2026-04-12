package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.activities.EditCategoryActivity;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategoryModel> {

    ArrayList<CategoryModel> categories = new ArrayList<CategoryModel>();
    CategoryController categoryController = CategoryController.getInstance();

    public CategoryAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<CategoryModel> categories) {
        super(context, resource, textViewResourceId, categories);

        this.categories = categories;
    }



    @Nullable
    @Override
    public CategoryModel getItem(int position) {
        return this.categories.get(position);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.category_list_layout, parent, false);
        }

        TextView categoryText = convertView.findViewById(R.id.categoryText);
        ImageView editBtn = convertView.findViewById(R.id.btnEdit);
        ImageView deleteBtn = convertView.findViewById(R.id.btnDelete);
        CategoryModel model = getItem(position);
        if (model != null) {
            categoryText.setText(model.getCategoryName());
            editBtn.setOnClickListener(v->{
               moveToEditScreen(model);
            });

            deleteBtn.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Step 1: UI turant update
                            categories.remove(model);
                            notifyDataSetChanged();

                            // Step 2: Firebase delete background mein
                            CategoryController.getInstance().deleteCategory(model.getCategoryId(),
                                    new DatabaseService.DatabaseCallback<String>() {
                                        @Override
                                        public void onSuccess(String data) {
                                            Toast.makeText(getContext(), "Category Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            Toast.makeText(getContext(), "Delete failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            });

        }

        return convertView;
    }

    private void moveToEditScreen(CategoryModel model) {
        Intent i = new Intent(getContext(), EditCategoryActivity.class);
        i.putExtra("categoryName",model.getCategoryName());
        i.putExtra("subcategories", new ArrayList<>(model.getSubCategories()));
        i.putExtra("categoryId", model.getCategoryId());

        getContext().startActivity(i);
    }
}
