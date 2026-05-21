package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.controllers.ProductController;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.views.activities.AddProductActivity;
import com.example.stylica_app.views.activities.EditProductActivity;
import com.example.stylica_app.views.activities.SingleProductActivity;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductModel> products;
    private ProductController productController;
    private String userRole;

    public ProductAdapter(Context context, List<ProductModel> products, String userRole) {
        this.context = context;
        this.products = products;
        this.productController = ProductController.getInstance(context);
        this.userRole = userRole;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductPrice, txtCategory;
        ImageView imgProduct;
        ImageButton btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(userRole.equals("customer")){
            view = LayoutInflater.from(context).inflate(R.layout.item_product_card,parent, false);
        }else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_product, parent, false);

        }
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = products.get(position);

        holder.txtProductName.setText(product.getProductName());
        holder.txtProductPrice.setText("Rs " + product.getPrice());

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_placeholder_bg)
                .error(R.drawable.image_placeholder_bg)
                .centerCrop()
                .into(holder.imgProduct);


//        role specific
        if(userRole.equals("vendor") || userRole.equals("admin")) {
            String categoryText = product.getCategory() != null ? product.getCategory()
                    : product.getSubcategory() != null ? product.getSubcategory()
                    : "Uncategorized";
            holder.txtCategory.setText(categoryText);
            holder.btnEdit.setOnClickListener(v -> {
                Intent i = new Intent(context, EditProductActivity.class);
                i.putExtra("productId", product.getProductId());
                context.startActivity(i);
            });

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete \"" + product.getProductName() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {

                            int currentPos = holder.getAdapterPosition();

                            if (currentPos == RecyclerView.NO_ID) return;

                            productController.deleteProduct(product.getProductId(), new ProductController.DeleteCallback() {
                                @Override
                                public void onSuccess() {
                                    products.remove(product);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Product deleted ", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(String error) {
                                    Toast.makeText(context, "Delete failed: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });


        }else if(userRole.equals("moderator")) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
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