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

import com.example.stylica_app.R;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.views.activities.ProfileActivity;
import com.example.stylica_app.views.activities.SingleProductActivity;

import java.util.List;

public class PendingUsersAdapter extends RecyclerView.Adapter<PendingUsersAdapter.ViewHolder> {


    private Context context;
    private List<UserModel> users;

    private OnActionListener listener;
    public PendingUsersAdapter(Context context, List<UserModel> users, OnActionListener listener){
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_user_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.txtUserName.setText(user.getFirstName() + " " + user.getLastName());
        holder.txtUserRole.setText(user.getRole());
        holder.txtDomain.setText("Domain: "+ user.getDomain());

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ProfileActivity.class);
            i.putExtra("userId", user.getUserId());
            context.startActivity(i);
        });

        holder.btnApprove.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Approve Product")
                    .setMessage("Approve \"" + user.getFirstName() + " " + user.getLastName() + "\"?")
                    .setPositiveButton("Approve", (dialog, which) -> {
                        listener.onApprove(user, holder.getAdapterPosition());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Reject with confirmation
        holder.btnReject.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Reject Product")
                    .setMessage("Reject \"" + user.getFirstName() + " " + user.getLastName()  + "\"?")
                    .setPositiveButton("Reject", (dialog, which) -> {
                        listener.onReject(user, holder.getAdapterPosition());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtUserName;
        TextView txtUserRole;
        TextView txtDomain;

        Button btnApprove;
        Button btnReject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserRole = itemView.findViewById(R.id.txtUserRole);
            txtDomain = itemView.findViewById(R.id.txtDomain);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);

        }
    }

    public void removeItem(int position) {
        users.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, users.size());
    }

    public interface OnActionListener {
        void onApprove(UserModel user, int position);
        void onReject(UserModel product, int position);
    }
}