package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.models.ProductModel;

import java.util.List;

public class VendorPendingProductsAdapter extends
        RecyclerView.Adapter<VendorPendingProductsAdapter.ViewHolder> {

    public interface OnEditListener {
        void onEdit(ProductModel product);
    }

    private Context context;
    private List<ProductModel> products;
    private OnEditListener listener;

    public VendorPendingProductsAdapter(Context context,
                                        List<ProductModel> products,
                                        OnEditListener listener) {
        this.context  = context;
        this.products = products;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtCategory,
                txtPrice, txtStatus;
        Button btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct     = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtCategory    = itemView.findViewById(R.id.txtCategory);
            txtPrice       = itemView.findViewById(R.id.txtPrice);
            txtStatus      = itemView.findViewById(R.id.txtStatus);
            btnEdit        = itemView.findViewById(R.id.btnEdit);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_vendor_pending_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = products.get(position);

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_placeholder_bg)
                .centerCrop()
                .into(holder.imgProduct);

        holder.txtProductName.setText(product.getProductName() != null
                ? product.getProductName() : "—");
        holder.txtCategory.setText(product.getCategory() != null
                ? product.getCategory() : "—");
        holder.txtPrice.setText("Rs " + product.getPrice());

        // Status badge - always "Pending" here but shown for clarity
        holder.txtStatus.setText("Under Review");
        holder.txtStatus.setBackgroundColor(
                android.graphics.Color.parseColor("#FFA000"));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(product));
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }
}