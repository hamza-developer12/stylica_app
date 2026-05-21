package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.stylica_app.models.SubOrderModel;
import com.example.stylica_app.views.activities.VendorOrderDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VendorOrdersAdapter extends
        RecyclerView.Adapter<VendorOrdersAdapter.ViewHolder> {

    private Context context;
    private List<SubOrderModel> subOrders;
    private OnStatusChangeListener listener;

    private String role;

    public interface OnStatusChangeListener {
        void onConfirm(SubOrderModel subOrder, int position);
        void onPack(SubOrderModel subOrder, int position);
        void onShip(SubOrderModel subOrder, int position);
        void onDeliver(SubOrderModel subOrder, int position);
    }

    public VendorOrdersAdapter(Context context,
                               List<SubOrderModel> subOrders,
                               String role,
                               OnStatusChangeListener listener) {
        this.context   = context;
        this.subOrders = subOrders;
        this.listener  = listener;
        this.role = role;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtStatus, txtCustomerName,
                txtProduct, txtDate, txtTotal;
        Button btnConfirm, btnPack, btnShip, btnDeliver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId      = itemView.findViewById(R.id.txtOrderId);
            txtStatus       = itemView.findViewById(R.id.txtStatus);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtProduct      = itemView.findViewById(R.id.txtItems);
            txtDate         = itemView.findViewById(R.id.txtDate);
            txtTotal        = itemView.findViewById(R.id.txtTotal);
            btnConfirm      = itemView.findViewById(R.id.btnConfirm);
            btnPack         = itemView.findViewById(R.id.btnPack);
            btnShip         = itemView.findViewById(R.id.btnShip);
            btnDeliver      = itemView.findViewById(R.id.btnDelivered);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_moderator_order_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubOrderModel subOrder = subOrders.get(position);

        // SubOrder ID
        String id = subOrder.getSubOrderId() != null
                ? "#" + subOrder.getSubOrderId()
                .substring(Math.max(0, subOrder.getSubOrderId().length() - 8))
                .toUpperCase()
                : "#—";
        holder.txtOrderId.setText(id);

        // Status
        String status = subOrder.getStatus() != null ? subOrder.getStatus() : "pending";
        holder.txtStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
        setStatusColor(holder.txtStatus, status);

        // Customer
        holder.txtCustomerName.setText(subOrder.getCustomerName() != null
                ? subOrder.getCustomerName() : "—");

        // Product + qty
        holder.txtProduct.setText(subOrder.getProductName() + " x" + subOrder.getQuantity());

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

        // Show buttons based on current status
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnPack.setVisibility(View.GONE);
        holder.btnShip.setVisibility(View.GONE);
        holder.btnDeliver.setVisibility(View.GONE);

        if(role.equals("vendor")) {
            switch (status.toLowerCase()) {
                case "pending":
                    holder.btnConfirm.setVisibility(View.VISIBLE);
                    break;
                case "confirmed":
                    holder.btnPack.setVisibility(View.VISIBLE);
                    break;
                case "packed":
                    holder.btnShip.setVisibility(View.VISIBLE);
                    break;
                case "shipped":
                    holder.btnDeliver.setVisibility(View.VISIBLE);
                    break;
                // delivered - no buttons
            }
        }

        // Button listeners
        holder.btnConfirm.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Order")
                        .setMessage("Confirm this order?")
                        .setPositiveButton("Confirm", (d, w) ->
                                listener.onConfirm(subOrder, holder.getAdapterPosition()))
                        .setNegativeButton("Cancel", null)
                        .show());

        holder.btnPack.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Mark as Packed")
                        .setMessage("Mark this order as packed?")
                        .setPositiveButton("Pack", (d, w) ->
                                listener.onPack(subOrder, holder.getAdapterPosition()))
                        .setNegativeButton("Cancel", null)
                        .show());

        holder.btnShip.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Mark as Shipped")
                        .setMessage("Mark this order as shipped?")
                        .setPositiveButton("Ship", (d, w) ->
                                listener.onShip(subOrder, holder.getAdapterPosition()))
                        .setNegativeButton("Cancel", null)
                        .show());

        holder.btnDeliver.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Mark as Delivered")
                        .setMessage("Mark this order as delivered?")
                        .setPositiveButton("Yes", (d, w) ->
                                listener.onDeliver(subOrder, holder.getAdapterPosition()))
                        .setNegativeButton("Cancel", null)
                        .show());

        // Open detail on card click
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, VendorOrderDetailActivity.class);
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
            default:
                view.setBackgroundResource(R.drawable.chip_selected_bg);
        }
    }

    @Override
    public int getItemCount() {
        return subOrders != null ? subOrders.size() : 0;
    }
}