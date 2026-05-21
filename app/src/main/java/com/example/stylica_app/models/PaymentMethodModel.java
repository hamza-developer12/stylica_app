package com.example.stylica_app.models;

public class PaymentMethodModel {

    private String id;
    private String type;
    private String accountTitle;
    private String accountNumber;
    private String instructions;

    public PaymentMethodModel() {}

    public PaymentMethodModel(String id, String type, String accountTitle,
                              String accountNumber, String instructions) {
        this.id             = id;
        this.type           = type;
        this.accountTitle   = accountTitle;
        this.accountNumber  = accountNumber;
        this.instructions   = instructions;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAccountTitle() { return accountTitle; }
    public void setAccountTitle(String accountTitle) { this.accountTitle = accountTitle; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}