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
import com.example.stylica_app.controllers.ModeratorController;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.activities.EditProfileActivity;
import com.example.stylica_app.views.activities.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class VendorAdapter extends ArrayAdapter<UserModel> {

    List<UserModel> moderators = new ArrayList<>();
    ModeratorController moderatorController;

    public VendorAdapter(Context context, int resource,
                            ArrayList<UserModel> moderators) {
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
    public View getView(int position, View convertView,
                        @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.custom_list_layout, parent, false);
        }

        TextView moderatorName = convertView.findViewById(R.id.itemText);
        ImageView editBtn      = convertView.findViewById(R.id.btnEdit);
        ImageView deleteBtn    = convertView.findViewById(R.id.btnDelete);

        UserModel model = getItem(position);

        if (model != null) {
            moderatorName.setText(model.getFirstName()
                    + " " + model.getLastName());


            convertView.setOnClickListener(v->{
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("userId", model.getUserId());
                getContext().startActivity(i);
            });
            editBtn.setOnClickListener(v -> moveToEditScreen(model));


            deleteBtn.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Vendor")
                        .setMessage("Are you sure you want to delete "
                                + model.getFirstName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            moderators.remove(model);
                            notifyDataSetChanged();

                            moderatorController.deleteModerator(
                                    model.getUserId(),
                                    new DatabaseService.DatabaseCallback<String>() {
                                        @Override
                                        public void onSuccess(String data) {
                                            Toast.makeText(getContext(),
                                                    "Moderator deleted",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            Toast.makeText(getContext(),
                                                    "Delete failed: " + errorMessage,
                                                    Toast.LENGTH_SHORT).show();
                                            // Re-add if Firebase delete failed
                                            moderators.add(model);
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

    // Open ProfileActivity with moderator's userId
    private void moveToEditScreen(UserModel model) {
        Intent i = new Intent(getContext(), EditProfileActivity.class);
        i.putExtra("userId", model.getUserId());
        getContext().startActivity(i);
    }
}