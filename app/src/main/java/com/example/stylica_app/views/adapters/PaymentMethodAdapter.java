package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.models.PaymentMethodModel;
import com.example.stylica_app.views.activities.AddEditPaymentActivity;

import java.util.List;

public class PaymentMethodAdapter extends
        RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {

    private Context context;
    private List<PaymentMethodModel> methods;
    private OnDeleteListener listener;

    public interface OnDeleteListener {
        void onDelete(PaymentMethodModel method, int position);
    }

    public PaymentMethodAdapter(Context context,
                                List<PaymentMethodModel> methods,
                                OnDeleteListener listener) {
        this.context  = context;
        this.methods  = methods;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIcon, txtType, txtAccountTitle,
                txtAccountNumber, txtInstructions;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIcon          = itemView.findViewById(R.id.txtIcon);
            txtType          = itemView.findViewById(R.id.txtType);
            txtAccountTitle  = itemView.findViewById(R.id.txtAccountTitle);
            txtAccountNumber = itemView.findViewById(R.id.txtAccountNumber);
            txtInstructions  = itemView.findViewById(R.id.txtInstructions);
            btnEdit          = itemView.findViewById(R.id.btnEdit);
            btnDelete        = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_payment_method_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentMethodModel method = methods.get(position);

        holder.txtType.setText(method.getType());
        holder.txtAccountTitle.setText(method.getAccountTitle());
        holder.txtAccountNumber.setText(method.getAccountNumber());
        holder.txtInstructions.setText(method.getInstructions());

        // Icon based on type
        switch (method.getType()) {
            case "card":      holder.txtIcon.setText("💳"); break;
            case "jazzcash":  holder.txtIcon.setText("📱"); break;
            case "easypaisa": holder.txtIcon.setText("💚"); break;
            default:          holder.txtIcon.setText("💰");
        }

        // Edit — open AddEditPaymentActivity with payment data
        holder.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, AddEditPaymentActivity.class);
            i.putExtra("paymentId",      method.getId());
            i.putExtra("type",           method.getType());
            i.putExtra("accountTitle",   method.getAccountTitle());
            i.putExtra("accountNumber",  method.getAccountNumber());
            i.putExtra("instructions",   method.getInstructions());
            i.putExtra("isEdit",         true);
            context.startActivity(i);
        });

        // Delete with confirmation
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Payment Method")
                    .setMessage("Delete " + method.getAccountTitle() + "?")
                    .setPositiveButton("Delete", (dialog, which) ->
                            listener.onDelete(method, holder.getAdapterPosition()))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return methods != null ? methods.size() : 0;
    }

    public void removeItem(int position) {
        methods.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, methods.size());
    }
}