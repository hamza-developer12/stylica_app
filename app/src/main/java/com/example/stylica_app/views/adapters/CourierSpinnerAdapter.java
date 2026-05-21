package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.stylica_app.R;
import com.example.stylica_app.models.CourierModel;

import java.util.List;

public class CourierSpinnerAdapter extends ArrayAdapter<CourierModel> {

    private final List<CourierModel> couriers;
    private final Context context;

    public CourierSpinnerAdapter(@NonNull Context context,
                                 List<CourierModel> couriers) {
        super(context, R.layout.spinner_dropdown_item, couriers);
        this.context  = context;
        this.couriers = couriers;

        setDropDownViewResource(R.layout.spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position,
                                @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.spinner_dropdown_item, parent, false);
        }

        CourierModel courier = couriers.get(position);
        TextView txt = convertView.findViewById(android.R.id.text1);
        txt.setText(courier.getCourierName()
                + " — Rs " + courier.getDeliveryCharges());

        return convertView;
    }

    @Override
    public CourierModel getItem(int position) {
        return couriers.get(position);
    }

    @Override
    public int getCount() {
        return couriers != null ? couriers.size() : 0;
    }
}