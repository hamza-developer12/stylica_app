package com.example.stylica_app.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService<T> {
    FirebaseFirestore firestore;

    public  DatabaseService(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }


    public void addRecord(String collection, String docId, T record, DatabaseCallback<String> callback){
        firestore.collection(collection).document(docId).set(record).addOnSuccessListener(result->{
            callback.onSuccess("Record Added Successfully");
        }).addOnFailureListener(e->{
            callback.onFailure(e.getMessage());
        });
    }

    public void findAll(String collection, Class<T> modelClass, DatabaseCallback<List<T>> callback) {
      firestore.collection(collection).get().addOnSuccessListener(querySnapshot->{
          List<T> records = new ArrayList<>();
          for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
              T record = doc.toObject(modelClass);
              if(record != null) {
                  records.add(record);
              }
          }
          callback.onSuccess(records);
      }).addOnFailureListener(e->{
          callback.onFailure(e.getMessage());
      });
    }


    public void findById(String collection, String id, Class<T> modelClass, DatabaseCallback<T> callback) {
        firestore.collection(collection).document(id).get().addOnSuccessListener(data->{

            if(data.exists()) {
                T record = data.toObject(modelClass);
                if(record != null) {
                    callback.onSuccess(record);
                }else {
                    callback.onFailure("Failed to parse document into model");
                }
            }else{
                callback.onFailure("Record Not Found");
            }
        }).addOnFailureListener(e->{
            callback.onFailure(e.getMessage());
        });
    }
    public void findWhere(String collection, String key, String value, Class<T> modelClass, DatabaseCallback<List<T>> callback) {
        firestore.collection(collection).whereEqualTo(key,value).get().addOnSuccessListener(data->{

           List<T> records = new ArrayList<T>();
           for(DocumentSnapshot doc : data.getDocuments()) {
               T record = doc.toObject(modelClass);
               if(record != null) {
                   records.add(record);
               }
           }
           callback.onSuccess(records);
        }).addOnFailureListener(e->{
            callback.onFailure(e.getMessage());
        });
    }

    public void deleteById(String collection, String id, DatabaseCallback<String> callback) {
        firestore.collection(collection).document(id).delete().addOnSuccessListener(aVoid->{
            callback.onSuccess("Record Deleted Successfully");
        }).addOnFailureListener(e->{
            callback.onFailure(e.getMessage());
        });
    }
    public interface DatabaseCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMessage);
    }
}
