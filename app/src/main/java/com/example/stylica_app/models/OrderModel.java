package com.example.stylica_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;

public class OrderModel implements Serializable {

    private String orderId;
    private String paymentMethodId;
    private String paymentMethodTitle;
    private String paymentMethodName;
    private String courierId;
    private String courierName;
    private String daysToDeliver;
    private String paymentStatus;       // "pending", "verified", "rejected"
    private String paymentScreenshotUrl;
    private double deliveryCharges;
    private double grandTotal;          // totalPrice + deliveryCharges

    @ServerTimestamp
    private Timestamp createdAt;

    public OrderModel() {}

    public OrderModel(String orderId, String paymentMethodId, String paymentMethodTitle,
                      String paymentMethodName, String courierId, String courierName,
                      String daysToDeliver, String paymentStatus, String paymentScreenshotUrl,
                      double deliveryCharges, double grandTotal) {
        this.orderId              = orderId;
        this.paymentMethodId      = paymentMethodId;
        this.paymentMethodTitle   = paymentMethodTitle;
        this.paymentMethodName    = paymentMethodName;
        this.courierId            = courierId;
        this.courierName          = courierName;
        this.daysToDeliver        = daysToDeliver;
        this.paymentStatus        = paymentStatus;
        this.paymentScreenshotUrl = paymentScreenshotUrl;
        this.deliveryCharges      = deliveryCharges;
        this.grandTotal           = grandTotal;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getPaymentMethodTitle() { return paymentMethodTitle; }
    public void setPaymentMethodTitle(String paymentMethodTitle) { this.paymentMethodTitle = paymentMethodTitle; }

    public String getPaymentMethodName() { return paymentMethodName; }
    public void setPaymentMethodName(String paymentMethodName) { this.paymentMethodName = paymentMethodName; }

    public String getCourierId() { return courierId; }
    public void setCourierId(String courierId) { this.courierId = courierId; }

    public String getCourierName() { return courierName; }
    public void setCourierName(String courierName) { this.courierName = courierName; }

    public String getDaysToDeliver() { return daysToDeliver; }
    public void setDaysToDeliver(String daysToDeliver) { this.daysToDeliver = daysToDeliver; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentScreenshotUrl() { return paymentScreenshotUrl; }
    public void setPaymentScreenshotUrl(String paymentScreenshotUrl) { this.paymentScreenshotUrl = paymentScreenshotUrl; }

    public double getDeliveryCharges() { return deliveryCharges; }
    public void setDeliveryCharges(double deliveryCharges) { this.deliveryCharges = deliveryCharges; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}