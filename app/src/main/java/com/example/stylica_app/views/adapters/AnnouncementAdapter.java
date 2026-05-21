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
import com.example.stylica_app.models.AnnouncementModel;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.activities.AddEditAnnouncementActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnnouncementAdapter extends
        RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private Context context;
    private List<AnnouncementModel> announcements;
    private OnDeleteListener listener;

    SessionService sessionService;
    public interface OnDeleteListener {
        void onDelete(AnnouncementModel announcement, int position);
    }

    public AnnouncementAdapter(Context context,
                               List<AnnouncementModel> announcements,
                               OnDeleteListener listener) {
        this.context       = context;
        this.announcements = announcements;
        this.listener      = listener;
        sessionService = new SessionService(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtType, txtDate, txtTitle, txtDescription;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtType        = itemView.findViewById(R.id.txtType);
            txtDate        = itemView.findViewById(R.id.txtDate);
            txtTitle       = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            btnEdit        = itemView.findViewById(R.id.btnEdit);
            btnDelete      = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_announcement_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnnouncementModel announcement = announcements.get(position);

        holder.txtType.setText(announcement.getType());
        holder.txtTitle.setText(announcement.getTitle());
        holder.txtDescription.setText(announcement.getDescription());

        // Format date
        if (announcement.getDate() != null) {
            Date date = announcement.getDate().toDate();
            String formatted = new SimpleDateFormat(
                    "dd MMM yyyy", Locale.getDefault()).format(date);
            holder.txtDate.setText(formatted);
        } else {
            holder.txtDate.setText("—");
        }
        String role = sessionService.getUserRole();
        if(!role.equals("admin")) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, AddEditAnnouncementActivity.class);
            i.putExtra("isEdit",      true);
            i.putExtra("id",          announcement.getId());
            i.putExtra("title",       announcement.getTitle());
            i.putExtra("description", announcement.getDescription());
            i.putExtra("type",        announcement.getType());
            if (announcement.getDate() != null) {
                i.putExtra("date", announcement.getDate().toDate().getTime());
            }
            context.startActivity(i);
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Announcement")
                    .setMessage("Delete \"" + announcement.getTitle() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        int currentPos = holder.getAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            listener.onDelete(announcement, currentPos);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return announcements != null ? announcements.size() : 0;
    }

    public void removeItem(AnnouncementModel announcement) {
        int position = announcements.indexOf(announcement);
        if (position != -1) {
            announcements.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, announcements.size());
        }
    }
}