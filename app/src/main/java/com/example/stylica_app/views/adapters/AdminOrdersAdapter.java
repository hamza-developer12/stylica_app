package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.models.OrderModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrdersAdapter extends
        RecyclerView.Adapter<AdminOrdersAdapter.ViewHolder> {

    public interface OrderActionListener {
        void onVerify(OrderModel order, int position);
        void onReject(OrderModel order, int position);
        void onViewScreenshot(String url);
    }

    private Context context;
    private List<OrderModel> orders;
    private OrderActionListener listener;

    public AdminOrdersAdapter(Context context, List<OrderModel> orders,
                              OrderActionListener listener) {
        this.context  = context;
        this.orders   = orders;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtPaymentStatus, txtPaymentMethod,
                txtAmount, txtDate;
        Button btnVerify, btnReject, btnScreenshot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId       = itemView.findViewById(R.id.txtOrderId);
            txtPaymentStatus = itemView.findViewById(R.id.txtPaymentStatus);
            txtPaymentMethod = itemView.findViewById(R.id.txtPaymentMethod);
            txtAmount        = itemView.findViewById(R.id.txtTotal);
            txtDate          = itemView.findViewById(R.id.txtDate);
            btnVerify        = itemView.findViewById(R.id.btnVerify);
            btnReject        = itemView.findViewById(R.id.btnReject);
            btnScreenshot    = itemView.findViewById(R.id.btnScreenshot);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_order_card, parent, false);
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
        setPaymentStatusColor(holder.txtPaymentStatus, ps);

        // Payment method
        holder.txtPaymentMethod.setText(
                order.getPaymentMethodName() != null
                        ? order.getPaymentMethodName() : "—");

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

        // Show/hide verify + reject based on paymentStatus
        if (ps.equals("pending")) {
            holder.btnVerify.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        } else {
            holder.btnVerify.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        // Screenshot button — always visible if url exists
        boolean hasScreenshot = order.getPaymentScreenshotUrl() != null
                && !order.getPaymentScreenshotUrl().isEmpty();
        holder.btnScreenshot.setVisibility(
                hasScreenshot ? View.VISIBLE : View.GONE);

        holder.btnVerify.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Verify Payment")
                        .setMessage("Confirm this payment is verified?")
                        .setPositiveButton("Verify", (d, w) ->
                                listener.onVerify(order, holder.getAdapterPosition()))
                        .setNegativeButton("Cancel", null)
                        .show());

        holder.btnReject.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Reject Payment")
                        .setMessage("Reject this payment?")
                        .setPositiveButton("Reject", (d, w) ->
                                listener.onReject(order, holder.getAdapterPosition()))
                        .setNegativeButton("Cancel", null)
                        .show());

        holder.btnScreenshot.setOnClickListener(v ->
                listener.onViewScreenshot(order.getPaymentScreenshotUrl()));
    }

    private void setPaymentStatusColor(TextView view, String status) {
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