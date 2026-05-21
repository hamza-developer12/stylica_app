package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.views.activities.SingleProductActivity;

import java.util.List;

public class PendingProductAdapter extends
        RecyclerView.Adapter<PendingProductAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> products;
    private OnActionListener listener;

    public interface OnActionListener {
        void onApprove(ProductModel product, int position);
        void onReject(ProductModel product, int position, String reason);
    }

    public PendingProductAdapter(Context context,
                                 List<ProductModel> products,
                                 OnActionListener listener) {
        this.context  = context;
        this.products = products;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtCategory, txtPrice, txtSeller;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct      = itemView.findViewById(R.id.imgProduct);
            txtProductName  = itemView.findViewById(R.id.txtProductName);
            txtCategory     = itemView.findViewById(R.id.txtCategory);
            txtPrice        = itemView.findViewById(R.id.txtPrice);
            txtSeller       = itemView.findViewById(R.id.txtSeller);
            btnApprove      = itemView.findViewById(R.id.btnApprove);
            btnReject       = itemView.findViewById(R.id.btnReject);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_pending_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = products.get(position);

        holder.txtProductName.setText(product.getProductName());
        holder.txtCategory.setText(product.getCategory() != null
                ? product.getCategory() : "Uncategorized");
        holder.txtPrice.setText("Rs " + product.getPrice());
        holder.txtSeller.setText("By: " + (product.getUserName() != null
                ? product.getUserName() : "Unknown"));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_placeholder_bg)
                .centerCrop()
                .into(holder.imgProduct);

        // view product screen
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, SingleProductActivity.class);
            i.putExtra("productId", product.getProductId());
            context.startActivity(i);
        });

        // Approve with confirmation
        holder.btnApprove.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Approve Product")
                    .setMessage("Approve \"" + product.getProductName() + "\"?")
                    .setPositiveButton("Approve", (dialog, which) -> {
                        listener.onApprove(product, holder.getAdapterPosition());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Reject with confirmation
        holder.btnReject.setOnClickListener(v -> {
            // Show input dialog for rejection reason
            android.widget.EditText reasonInput = new android.widget.EditText(context);
            reasonInput.setHint("Enter rejection reason...");
            reasonInput.setPadding(48, 24, 48, 24);

            new AlertDialog.Builder(context)
                    .setTitle("Reject Product")
                    .setMessage("Rejecting \"" + product.getProductName() + "\"")
                    .setView(reasonInput)
                    .setPositiveButton("Reject", (dialog, which) -> {
                        String reason = reasonInput.getText().toString().trim();
                        if (reason.isEmpty()) {
                            Toast.makeText(context, "Please enter a reason",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listener.onReject(product, holder.getAdapterPosition(), reason);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    // Remove item from list after action
    public void removeItem(int position) {
        products.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, products.size());
    }
}