package com.example.stylica_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class ProductModel {

    private String productId;
    private String productName;
    private String description;
    private String category;
    private String subcategory;
    private int stockQuantity;
    private double price;
    private String imageUrl;


    private boolean isFeatured;
    private boolean isNew;

    private boolean isVerified;


    private String moderatorId;
    private String moderatorName;

    private double[] reviews;
    private int reviewCount;

    @ServerTimestamp
    private Timestamp createdAt;
    @ServerTimestamp
    private Timestamp updatedAt;

    public ProductModel(){}


}
