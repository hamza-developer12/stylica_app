package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.views.activities.SingleProductActivity;

import java.util.List;

public class CustomerHomeProductAdapter extends RecyclerView.Adapter<CustomerHomeProductAdapter.ViewHolder>{

    Context context;
    List<ProductModel> products;
    public CustomerHomeProductAdapter(Context context, List<ProductModel> products) {
        this.context = context;
        this.products = products;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = products.get(position);

        holder.txtProductName.setText(product.getProductName());
        holder.txtPrice.setText("Rs " + product.getPrice());
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_placeholder_bg)
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v-> {
            Intent i = new Intent(context, SingleProductActivity.class);
            i.putExtra("productId", product.getProductId());
            context.startActivity(i);
        });


    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }





}
