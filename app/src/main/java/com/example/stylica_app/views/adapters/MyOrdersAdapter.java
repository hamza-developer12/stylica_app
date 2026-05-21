package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.models.SubOrderModel;
import com.example.stylica_app.views.activities.OrderDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyOrdersAdapter extends
        RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    private Context context;
    private List<SubOrderModel> subOrders;

    public MyOrdersAdapter(Context context, List<SubOrderModel> subOrders) {
        this.context   = context;
        this.subOrders = subOrders;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtStatus, txtItems,
                txtPaymentMethod, txtAddress, txtDate, txtTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId       = itemView.findViewById(R.id.txtOrderId);
            txtStatus        = itemView.findViewById(R.id.txtStatus);
            txtItems         = itemView.findViewById(R.id.txtItems);
            txtPaymentMethod = itemView.findViewById(R.id.txtPaymentMethod);
            txtAddress       = itemView.findViewById(R.id.txtAddress);
            txtDate          = itemView.findViewById(R.id.txtDate);
            txtTotal         = itemView.findViewById(R.id.txtTotal);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubOrderModel subOrder = subOrders.get(position);

        // SubOrder ID — show last 8 chars
        String subOrderId = subOrder.getSubOrderId() != null
                ? "#" + subOrder.getSubOrderId()
                .substring(Math.max(0, subOrder.getSubOrderId().length() - 8))
                .toUpperCase()
                : "#—";
        holder.txtOrderId.setText(subOrderId);

        // Status - show paymentStatus if still pending, else show fulfillment status
        String paymentStatus = subOrder.getPaymentStatus() != null
                ? subOrder.getPaymentStatus() : "pending";
        String fulfillmentStatus = subOrder.getStatus() != null
                ? subOrder.getStatus() : "pending";

        String displayStatus = paymentStatus.equals("pending") || paymentStatus.equals("rejected")
                ? paymentStatus
                : fulfillmentStatus;

        holder.txtStatus.setText(displayStatus.substring(0, 1).toUpperCase()
                + displayStatus.substring(1));
        setStatusColor(holder.txtStatus, displayStatus);

        // Product name + quantity
        holder.txtItems.setText(subOrder.getProductName() + " x" + subOrder.getQuantity());

        // Payment method — not in SubOrder, show delivery days instead
        holder.txtPaymentMethod.setText("Delivery: " +
                (subOrder.getDeliveryDays() != null ? subOrder.getDeliveryDays() + " days" : "—"));

        // Domain
        holder.txtAddress.setText("Domain: " +
                (subOrder.getDomain() != null ? subOrder.getDomain() : "—"));

        // Date
        if (subOrder.getCreatedAt() != null) {
            Date date = subOrder.getCreatedAt().toDate();
            holder.txtDate.setText(new SimpleDateFormat(
                    "dd MMM yyyy", Locale.getDefault()).format(date));
        } else {
            holder.txtDate.setText("—");
        }

        // Total
        holder.txtTotal.setText("Rs " + subOrder.getTotalPrice());

        // Open detail on click — pass subOrderId
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, OrderDetailActivity.class);
            i.putExtra("subOrderId", subOrder.getSubOrderId());
            i.putExtra("orderId", subOrder.getOrderId());
            context.startActivity(i);
        });
    }

    private void setStatusColor(TextView view, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                view.setBackgroundColor(Color.parseColor("#FFA000"));
                break;
            case "verified":
            case "confirmed":
                view.setBackgroundColor(Color.parseColor("#1976D2"));
                break;
            case "packed":
                view.setBackgroundColor(Color.parseColor("#7B1FA2"));
                break;
            case "shipped":
                view.setBackgroundColor(Color.parseColor("#0288D1"));
                break;
            case "delivered":
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
        return subOrders != null ? subOrders.size() : 0;
    }
}