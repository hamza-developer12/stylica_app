package com.example.stylica_app.views.adapters;

import com.example.stylica_app.R;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.controllers.CourierController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.CourierModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.activities.EditCategoryActivity;
import com.example.stylica_app.views.activities.EditCourierActivity;

import java.util.ArrayList;
import java.util.List;

public class CourierAdapter extends ArrayAdapter<CourierModel> {

    List<CourierModel> couriers = new ArrayList<>();
    CourierController courierController = CourierController.getInstance();
    public CourierAdapter(@NonNull Context context, int resource, List<CourierModel> couriers) {
        super(context, resource, couriers);
        this.couriers = couriers;
    }

    @Nullable
    @Override
    public CourierModel getItem(int position) {
        return this.couriers.get(position);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_layout, parent,false);
        }

        TextView courierName = convertView.findViewById(R.id.itemText);
        ImageView editBtn = convertView.findViewById(R.id.btnEdit);
        ImageView deleteBtn = convertView.findViewById(R.id.btnDelete);
        CourierModel model = getItem(position);

        if(model != null) {
            courierName.setText(model.getCourierName().toString());
            Log.d("Courier Model", model.toString());
            editBtn.setOnClickListener(v->{
                moveToEditScreen(model);
            });
            deleteBtn.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Step 1: UI turant update
                            couriers.remove(model);
                            notifyDataSetChanged();

                            // Step 2: Firebase delete background mein
                            courierController.deleteCourier(model.getCourierId(), new DatabaseService.DatabaseCallback<String>() {
                                @Override
                                public void onSuccess(String data) {
                                    Toast.makeText(getContext(), "Courier Removed Successfully", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(getContext(), "Delete failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });

                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            });

        }

        return convertView;
    }
    private void moveToEditScreen(CourierModel model) {
        Intent i = new Intent(getContext(), EditCourierActivity.class);
        i.putExtra("courierName",model.getCourierName());
        i.putExtra("phoneNumber", model.getPhoneNumber());
        i.putExtra("email", model.getEmail());
        i.putExtra("courierId", model.getCourierId());


        getContext().startActivity(i);
    }
}