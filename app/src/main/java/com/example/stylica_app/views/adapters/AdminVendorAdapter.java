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
import com.example.stylica_app.controllers.VendorController;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.activities.EditProfileActivity;
import com.example.stylica_app.views.activities.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminVendorAdapter extends ArrayAdapter<UserModel> {

    List<UserModel> vendors = new ArrayList<>();
    VendorController adminVendorController;

    public AdminVendorAdapter(Context context, int resource,
                              ArrayList<UserModel> vendors) {
        super(context, resource, vendors);
        this.adminVendorController = VendorController.getInstance(context);
        this.vendors = vendors;
    }

    @Nullable
    @Override
    public UserModel getItem(int position) {
        return this.vendors.get(position);
    }

    @Override
    public View getView(int position, View convertView,
                        @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.custom_list_layout, parent, false);
        }

        TextView vendorName = convertView.findViewById(R.id.itemText);
        ImageView editBtn   = convertView.findViewById(R.id.btnEdit);
        ImageView deleteBtn = convertView.findViewById(R.id.btnDelete);

        UserModel model = getItem(position);

        if (model != null) {
            vendorName.setText(model.getFirstName()
                    + " " + model.getLastName());

            convertView.setOnClickListener(v -> {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("userId", model.getUserId());
                getContext().startActivity(i);
            });

            editBtn.setOnClickListener(v -> moveToEditScreen(model));

            deleteBtn.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Vendor")
                        .setMessage("Are you sure you want to delete vendor "
                                + model.getFirstName() + " " + model.getLastName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            vendors.remove(model);
                            notifyDataSetChanged();

                            adminVendorController.deleteVendor(
                                    model.getUserId(),
                                    new DatabaseService.DatabaseCallback<String>() {
                                        @Override
                                        public void onSuccess(String data) {
                                            Toast.makeText(getContext(),
                                                    "Vendor deleted successfully",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            Toast.makeText(getContext(),
                                                    "Delete failed: " + errorMessage,
                                                    Toast.LENGTH_SHORT).show();
                                            // Re-add if Firebase delete failed
                                            vendors.add(model);
                                            notifyDataSetChanged();
                                        }
                                    });
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        return convertView;
    }

    // Open EditProfileActivity with vendor's userId
    private void moveToEditScreen(UserModel model) {
        Intent i = new Intent(getContext(), EditProfileActivity.class);
        i.putExtra("userId", model.getUserId());
        getContext().startActivity(i);
    }
}