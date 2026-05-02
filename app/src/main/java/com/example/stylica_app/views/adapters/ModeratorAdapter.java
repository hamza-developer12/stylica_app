package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.stylica_app.controllers.ModeratorController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.activities.EditCategoryActivity;

import java.util.ArrayList;
import java.util.List;

public class ModeratorAdapter extends ArrayAdapter<UserModel> {

    List<UserModel> moderators = new ArrayList<>();
    ModeratorController moderatorController;

    public ModeratorAdapter(Context context, int resource, ArrayList<UserModel> moderators) {
        super(context, resource, moderators);
       this.moderatorController = ModeratorController.getInstance(context);
       this.moderators = moderators;

    }


    @Nullable
    @Override
    public UserModel getItem(int position) {
        return this.moderators.get(position);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.custom_list_layout, parent, false);
        }

        TextView moderatorName = convertView.findViewById(R.id.itemText);
        ImageView editBtn = convertView.findViewById(R.id.btnEdit);
        ImageView deleteBtn = convertView.findViewById(R.id.btnDelete);
        UserModel model = getItem(position);
        if (model != null) {

            moderatorName.setText(model.getFirstName());
            editBtn.setOnClickListener(v->{
                moveToEditScreen(model);
            });
            editBtn.setOnClickListener(v->{
                moveToEditScreen(model);
            });

            deleteBtn.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Step 1: UI turant update
                            moderators.remove(model);
                            notifyDataSetChanged();

                            // Step 2: Firebase delete background mein
//                            ModeratorController.getInstance(getContext()).deleteCategory(model.getUserId(),
//                                    new DatabaseService.DatabaseCallback<String>() {
//                                        @Override
//                                        public void onSuccess(String data) {
//                                            Toast.makeText(getContext(), "Moderator Deleted Successfully", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        @Override
//                                        public void onFailure(String errorMessage) {
//                                            Toast.makeText(getContext(), "Delete failed: " + errorMessage, Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                            );
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            });

        }

        return convertView;
    }

    private void moveToEditScreen(UserModel model) {
//        Intent i = new Intent(getContext(), EditCategoryActivity.class);
//        i.putExtra("categoryName",model.getCategoryName());
//        i.putExtra("subcategories", new ArrayList<>(model.getSubCategories()));
//        i.putExtra("categoryId", model.getCategoryId());
//
//        getContext().startActivity(i);
    }

}
