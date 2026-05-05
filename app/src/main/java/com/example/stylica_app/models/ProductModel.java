package com.example.stylica_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Arrays;

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

    private String status;


    private String userId;
    private String userName;

    private double[] reviews;
    private int reviewCount;

    @ServerTimestamp
    private Timestamp createdAt;
    @ServerTimestamp
    private Timestamp updatedAt;

    public ProductModel(){}

    public ProductModel(String productId, String productName, String description, String category, String subcategory, int stockQuantity, double price, String imageUrl, boolean isFeatured, boolean isNew, String status, String userId, String userName, double[] reviews, int reviewCount) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.subcategory = subcategory;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isFeatured = isFeatured;
        this.isNew = isNew;
        this.status = status;
        this.userId = userId;
        this.userName = userName;
        this.reviews = reviews;
        this.reviewCount = reviewCount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean getNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double[] getReviews() {
        return reviews;
    }

    public void setReviews(double[] reviews) {
        this.reviews = reviews;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

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

    @Override
    public String toString() {
        return "ProductModel{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", isFeatured=" + isFeatured +
                ", isNew=" + isNew +
                ", status=" + status +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", reviews=" + Arrays.toString(reviews) +
                ", reviewCount=" + reviewCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
