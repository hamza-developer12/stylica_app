package com.example.stylica_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class CourierModel {

    String courierId;
    String courierName;
    String phoneNumber;
    String email;
    double deliveryCharges;

    String deliveryDays;

    @ServerTimestamp
    private Timestamp createdAt;

    @ServerTimestamp
    private Timestamp updatedAt;

    public CourierModel() {}

    public CourierModel(String courierId, String courierName,
                        String phoneNumber, String email,
                        double deliveryCharges,
                        String deliveryDays
                        ) {
        this.courierId       = courierId;
        this.courierName     = courierName;
        this.phoneNumber     = phoneNumber;
        this.email           = email;
        this.deliveryCharges = deliveryCharges;
        this.deliveryDays = deliveryDays;
    }

    public String getCourierId() { return courierId; }
    public void setCourierId(String courierId) { this.courierId = courierId; }

    public String getCourierName() { return courierName; }
    public void setCourierName(String courierName) { this.courierName = courierName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getDeliveryCharges() { return deliveryCharges; }
    public void setDeliveryCharges(double deliveryCharges) { this.deliveryCharges = deliveryCharges; }

    public void setDeliveryDays(String deliveryDays) {
          this.deliveryDays = deliveryDays;
    }
    public String getDeliveryDays() {
        return this.deliveryDays;
    }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }



    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "CourierModel{" +
                "courierId='" + courierId + '\'' +
                ", courierName='" + courierName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", deliveryCharges=" + deliveryCharges +
                '}';
    }
}