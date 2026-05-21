package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.models.OrderModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionsAdapter extends
        RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    public interface OnOrderClickListener {
        void onClick(String orderId);
    }

    private Context context;
    private List<OrderModel> orders;
    private OnOrderClickListener listener;

    public TransactionsAdapter(Context context, List<OrderModel> orders,
                               OnOrderClickListener listener) {
        this.context  = context;
        this.orders   = orders;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtPaymentStatus, txtPaymentMethod,
                txtAmount, txtDate, txtCourier;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId       = itemView.findViewById(R.id.txtOrderId);
            txtPaymentStatus = itemView.findViewById(R.id.txtPaymentStatus);
            txtPaymentMethod = itemView.findViewById(R.id.txtPaymentMethod);
            txtAmount        = itemView.findViewById(R.id.txtTotal);
            txtDate          = itemView.findViewById(R.id.txtDate);
            txtCourier       = itemView.findViewById(R.id.txtCourier);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orders.get(position);

        // Order ID
        String id = order.getOrderId() != null
                ? "#" + order.getOrderId()
                .substring(Math.max(0, order.getOrderId().length() - 8))
                .toUpperCase()
                : "#—";
        holder.txtOrderId.setText(id);

        // Payment status
        String ps = order.getPaymentStatus() != null
                ? order.getPaymentStatus() : "pending";
        holder.txtPaymentStatus.setText(
                ps.substring(0, 1).toUpperCase() + ps.substring(1));
        setStatusColor(holder.txtPaymentStatus, ps);

        // Payment method
        holder.txtPaymentMethod.setText(
                order.getPaymentMethodName() != null
                        ? order.getPaymentMethodName() : "—");

        // Courier
        holder.txtCourier.setText(
                order.getCourierName() != null
                        ? order.getCourierName() : "—");

        // Grand total
        holder.txtAmount.setText("Rs " + order.getGrandTotal());

        // Date
        if (order.getCreatedAt() != null) {
            Date date = order.getCreatedAt().toDate();
            holder.txtDate.setText(new SimpleDateFormat(
                    "dd MMM yyyy", Locale.getDefault()).format(date));
        } else {
            holder.txtDate.setText("—");
        }

        holder.itemView.setOnClickListener(v ->
                listener.onClick(order.getOrderId()));
    }

    private void setStatusColor(TextView view, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                view.setBackgroundColor(Color.parseColor("#FFA000"));
                break;
            case "verified":
                view.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            case "rejected":
                view.setBackgroundColor(Color.parseColor("#D32F2F"));
                break;
            default:
                view.setBackgroundResource(R.drawable.chip_selected_bg);
        }
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }
}