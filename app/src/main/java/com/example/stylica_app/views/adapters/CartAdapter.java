package com.example.stylica_app.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylica_app.R;
import com.example.stylica_app.helpers.CartDatabaseHelper;
import com.example.stylica_app.models.CartModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartModel> cartItems;
    private CartDatabaseHelper cartDb;
    private OnCartChangeListener listener;

    // Listener to notify activity when cart changes
    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartModel> cartItems,
                       OnCartChangeListener listener) {
        this.context   = context;
        this.cartItems = cartItems;
        this.cartDb    = CartDatabaseHelper.getInstance(context);
        this.listener  = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice, txtQuantity;
        Button btnIncrease, btnDecrease;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct      = itemView.findViewById(R.id.imgProduct);
            txtProductName  = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtQuantity     = itemView.findViewById(R.id.txtQuantity);
            btnIncrease     = itemView.findViewById(R.id.btnIncrease);
            btnDecrease     = itemView.findViewById(R.id.btnDecrease);
            btnRemove       = itemView.findViewById(R.id.btnRemove);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cart_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartModel item = cartItems.get(position);

        holder.txtProductName.setText(item.getProductName());
        holder.txtProductPrice.setText("Rs " + item.getTotalPrice());
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(context)
                .load(item.getProductImage())
                .placeholder(R.drawable.image_placeholder_bg)
                .centerCrop()
                .into(holder.imgProduct);



        holder.btnIncrease.setOnClickListener(v -> {
            // Check stock quantity
            if (item.getQuantity() < item.getStockQuantity()) {
                int newQty = item.getQuantity() + 1;
                item.setQuantity(newQty);
                cartDb.updateQuantity(item.getId(), newQty);
                holder.txtQuantity.setText(String.valueOf(newQty));
                holder.txtProductPrice.setText("Rs " + item.getTotalPrice());
                listener.onCartChanged();
            } else {

                Toast.makeText(context,
                        "Only " + item.getStockQuantity()
                                + " items available in stock",
                        Toast.LENGTH_SHORT).show();
            }
        });


        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQty = item.getQuantity() - 1;
                item.setQuantity(newQty);
                cartDb.updateQuantity(item.getId(), newQty);
                holder.txtQuantity.setText(String.valueOf(newQty));
                holder.txtProductPrice.setText("Rs " + item.getTotalPrice());
                listener.onCartChanged();
            } else {
                // If quantity is 1 and minus is pressed → remove item
                removeItem(holder.getAdapterPosition());
            }
        });


        holder.btnRemove.setOnClickListener(v -> {
            removeItem(holder.getAdapterPosition());
        });
    }

    private void removeItem(int position) {
        CartModel item = cartItems.get(position);
        cartDb.removeItem(item.getId());
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());
        listener.onCartChanged();
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }
}