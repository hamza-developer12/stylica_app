package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CourierController;
import com.example.stylica_app.services.DatabaseService;

public class AddCourierActivity extends BaseActivity {

    CourierController courierController;

    EditText edtCourierName, edtPhoneNumber,
            edtEmail, edtDeliveryCharges, edtDeliveryDays;
    Button btnSubmit;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_courier);


        setupAppBar("Add Courier");
        initializeUI();
        courierController = CourierController.getInstance();

        btnSubmit.setOnClickListener(v -> addCourier());
    }

    private void initializeUI() {
        edtCourierName = findViewById(R.id.edtCourierName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEmail = findViewById(R.id.edtEmail);
        edtDeliveryCharges = findViewById(R.id.edtDeliveryCharges);
        edtDeliveryDays = findViewById(R.id.edtDeliveryDays);
        btnSubmit = findViewById(R.id.btnSubmit);
        loader = findViewById(R.id.loader);
    }

    public void addCourier() {
        String courierName = edtCourierName.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String chargesStr = edtDeliveryCharges.getText().toString().trim();
        String deliveryDays = edtDeliveryDays.getText().toString().trim();

        // Validation
        if (courierName.isEmpty() || phoneNumber.isEmpty()
                || email.isEmpty() || chargesStr.isEmpty() || deliveryDays.isEmpty()) {
            Toast.makeText(this, "Please provide all details",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        double deliveryCharges;
        try {
            deliveryCharges = Double.parseDouble(chargesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid delivery charges",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading(true);


        courierController.addCourier(
                courierName, phoneNumber, email, deliveryCharges,deliveryDays,
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        isLoading(false);
                        Toast.makeText(AddCourierActivity.this,
                                "Courier added successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        isLoading(false);
                        Toast.makeText(AddCourierActivity.this,
                                errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void isLoading(boolean loading) {
        loader.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}