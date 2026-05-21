package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.models.SubOrderModel;

import java.util.List;

public class ModeratorOrdersAdapter extends RecyclerView.Adapter<ModeratorOrdersAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(SubOrderModel subOrder);
    }

    Context context;
    List<SubOrderModel> orders;
    OnItemClickListener listener;

    public ModeratorOrdersAdapter(Context context, List<SubOrderModel> orders, OnItemClickListener listener) {
        this.context  = context;
        this.orders   = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_moderator_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubOrderModel order = orders.get(position);

        holder.txtProductName.setText(order.getProductName());
        holder.txtCustomerName.setText("Customer: " + order.getCustomerName());
        holder.txtPrice.setText("Rs " + order.getTotalPrice());
        holder.txtDeliveryDays.setText("Delivery: " + order.getDeliveryDays() + " days");
        holder.txtStatus.setText(capitalize(order.getStatus()));
        holder.txtStatus.setBackgroundColor(getStatusColor(order.getStatus()));

        Glide.with(context)
                .load(order.getProductImageUrl())
                .placeholder(R.drawable.image_placeholder_bg)
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v -> listener.onClick(order));
    }

    @Override
    public int getItemCount() { return orders.size(); }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private int getStatusColor(String status) {
        if (status == null) return Color.GRAY;
        switch (status.toLowerCase()) {
            case "confirmed": return Color.parseColor("#2196F3"); // blue
            case "packed":    return Color.parseColor("#FF9800"); // orange
            case "shipped":   return Color.parseColor("#9C27B0"); // purple
            case "delivered": return Color.parseColor("#4CAF50"); // green
            default:          return Color.GRAY;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtCustomerName, txtPrice, txtDeliveryDays, txtStatus;

        ViewHolder(View itemView) {
            super(itemView);
            imgProduct      = itemView.findViewById(R.id.imgProduct);
            txtProductName  = itemView.findViewById(R.id.txtProductName);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtPrice        = itemView.findViewById(R.id.txtPrice);
            txtDeliveryDays = itemView.findViewById(R.id.txtDeliveryDays);
            txtStatus       = itemView.findViewById(R.id.txtStatus);
        }
    }
}