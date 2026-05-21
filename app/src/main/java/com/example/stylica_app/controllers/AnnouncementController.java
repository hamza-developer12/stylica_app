package com.example.stylica_app.controllers;

import com.example.stylica_app.models.AnnouncementModel;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementController {

    private static final String COLLECTION = "announcements";
    private static AnnouncementController instance;
    DatabaseService<AnnouncementModel> dbService;
    FirebaseFirestore firestore;

    public interface DeleteCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private AnnouncementController() {
        firestore = FirebaseFirestore.getInstance();
        dbService = new DatabaseService<AnnouncementModel>(firestore);
    }

    public static AnnouncementController getInstance() {
        if (instance == null) instance = new AnnouncementController();
        return instance;
    }

    public void addAnnouncement(String title, String description,
                                String type, Timestamp date,
                                DatabaseService.DatabaseCallback callback) {
        String id = firestore.collection(COLLECTION).document().getId();
        AnnouncementModel model = new AnnouncementModel(
                id, title, description, type, date, true);
        dbService.addRecord(COLLECTION, id, model, callback);
    }


    public void getAllAnnouncements(
            DatabaseService.DatabaseCallback<List<AnnouncementModel>> callback) {
        firestore.collection(COLLECTION)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<AnnouncementModel> list = snapshot
                            .toObjects(AnnouncementModel.class);
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    public void updateAnnouncement(String id, String title,
                                   String description, String type,
                                   Timestamp date, UpdateCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title",       title);
        updates.put("description", description);
        updates.put("type",        type);
        updates.put("date",        date);

        dbService.updateRecord(COLLECTION, id, updates,
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String data) { callback.onSuccess(); }
                    @Override
                    public void onFailure(String error) { callback.onFailure(error); }
                });
    }


    public void deleteAnnouncement(String id, DeleteCallback callback) {
        dbService.deleteById(COLLECTION, id,
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String data) { callback.onSuccess(); }
                    @Override
                    public void onFailure(String error) { callback.onFailure(error); }
                });
    }
}