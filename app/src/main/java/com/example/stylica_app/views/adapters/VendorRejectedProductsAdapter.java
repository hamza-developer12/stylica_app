package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.views.activities.EditProductActivity;

import java.util.List;

public class VendorRejectedProductsAdapter extends
        RecyclerView.Adapter<VendorRejectedProductsAdapter.ViewHolder> {

    public interface OnResubmitListener {
        void onResubmit(ProductModel product);
    }

    private Context context;
    private List<ProductModel> products;
    private OnResubmitListener listener;

    public VendorRejectedProductsAdapter(Context context,
                                         List<ProductModel> products,
                                         OnResubmitListener listener) {
        this.context  = context;
        this.products = products;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtCategory,
                txtPrice, txtReason;
        Button btnEdit, btnResubmit;
        android.widget.ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct      = itemView.findViewById(R.id.imgProduct);
            txtProductName  = itemView.findViewById(R.id.txtProductName);
            txtCategory     = itemView.findViewById(R.id.txtCategory);
            txtPrice        = itemView.findViewById(R.id.txtPrice);
            txtReason       = itemView.findViewById(R.id.txtRejectionReason);
            btnEdit         = itemView.findViewById(R.id.btnEdit);
            btnResubmit     = itemView.findViewById(R.id.btnResubmit);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_rejected_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = products.get(position);

        // Product image
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_placeholder_bg)
                .into(holder.imgProduct);

        holder.txtProductName.setText(product.getProductName() != null
                ? product.getProductName() : "—");
        holder.txtCategory.setText(product.getCategory() != null
                ? product.getCategory() : "—");
        holder.txtPrice.setText("Rs " + product.getPrice());

        // Rejection reason
        String reason = product.getRejectionReason();
        holder.txtReason.setText(reason != null && !reason.isEmpty()
                ? "Reason: " + reason : "No reason provided");

        // Edit — open EditProductActivity
        holder.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, EditProductActivity.class);
            i.putExtra("productId", product.getProductId());
            context.startActivity(i);
        });

        // Resubmit with confirmation
        holder.btnResubmit.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Resubmit Product")
                        .setMessage("Submit \"" + product.getProductName()
                                + "\" for review again?")
                        .setPositiveButton("Resubmit", (d, w) ->
                                listener.onResubmit(product))
                        .setNegativeButton("Cancel", null)
                        .show());
    }

    public void removeItem(ProductModel product) {
        int index = products.indexOf(product);
        if (index != -1) {
            products.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }
}