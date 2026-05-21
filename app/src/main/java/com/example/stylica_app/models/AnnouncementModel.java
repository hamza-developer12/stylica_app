package com.example.stylica_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class AnnouncementModel {

    private String id;
    private String title;
    private String description;
    private String type;
    private Timestamp date;
    private boolean isActive;

    @ServerTimestamp
    Timestamp createdAt;
    @ServerTimestamp
    Timestamp updatedAt;
    public AnnouncementModel() {}

    public AnnouncementModel(String id, String title, String description,
                             String type, Timestamp date, boolean isActive) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.type        = type;
        this.date        = date;
        this.isActive    = isActive;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}