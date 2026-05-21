package com.example.stylica_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;

public class SubOrderModel implements Serializable {

    private String subOrderId;
    private String orderId;
    private String productName;
    private double productPrice;
    private int quantity;
    private double totalPrice;
    private String productImageUrl;
    private String domain;
    private String status;            // "confirmed", "packed", "shipped", "delivered"
    private String deliveryDays;
    private String customerId;
    private String customerName;
    private String paymentStatus;     // "pending", "verified", "rejected"

    private String vendorId;

    @ServerTimestamp
    private Timestamp createdAt;

    public SubOrderModel() {}

    public SubOrderModel(String subOrderId, String orderId, String productName,
                         double productPrice, int quantity, double totalPrice,
                         String productImageUrl, String domain, String status,
                         String deliveryDays, String customerId, String customerName,
                         String paymentStatus, String vendorId) {
        this.subOrderId = subOrderId;
        this.orderId = orderId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.productImageUrl = productImageUrl;
        this.domain = domain;
        this.status = status;
        this.deliveryDays = deliveryDays;
        this.customerId = customerId;
        this.customerName = customerName;
        this.paymentStatus = paymentStatus;
        this.vendorId = vendorId;
    }

    public String getSubOrderId() { return subOrderId; }
    public void setSubOrderId(String subOrderId) { this.subOrderId = subOrderId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(String deliveryDays) { this.deliveryDays = deliveryDays; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPaymentStatus() { return paymentStatus; }

    public void setVendorId(String vendorId) {this.vendorId = vendorId;}
    public String getVendorId() {return this.vendorId;}
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}