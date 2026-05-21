package com.example.stylica_app.models;

public class CartModel {

    private int id;
    private String productId;
    private String productName;
    private String productImage;
    private double productPrice;
    private String category;
    private int quantity;
    private int stockQuantity;

    private String vendorId;


    public CartModel() {}

    public CartModel(String productId, String productName,
                     String productImage, double productPrice,
                     String category, int quantity, int stockQuantity, String vendorId) {
        this.productId    = productId;
        this.productName  = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.category     = category;
        this.quantity     = quantity;
        this.stockQuantity = stockQuantity;
        this.vendorId = vendorId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        return productPrice * quantity;
    }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public void setVendorId(String vendorId) {this.vendorId = vendorId;}

    public String getVendorId() {return this.vendorId;}
}