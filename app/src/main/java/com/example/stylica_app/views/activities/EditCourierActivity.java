package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CourierController;
import com.example.stylica_app.services.DatabaseService;

public class EditCourierActivity extends BaseActivity {
    String courierId;
    String courierName;
    String email;
    String phoneNumber;

    double deliveryCharges;

    String deliveryDays;
    Button btnSubmit;
    ProgressBar loader;

    CourierController courierController;

    EditText edtCourierName, edtPhoneNumber, edtEmail, edtDeliveryCharges, edtDeliveryDays;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_courier);


        setupAppBar("Edit Courier");

        courierController = CourierController.getInstance();
        courierId = getIntent().getStringExtra("courierId");
        courierName = getIntent().getStringExtra("courierName");
        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        deliveryCharges = getIntent().getDoubleExtra("deliveryCharges",0);
        deliveryDays = getIntent().getStringExtra("deliveryDays");

        initializeFields();
        btnSubmit = findViewById(R.id.btnSubmit);
        loader = findViewById(R.id.loader);


        btnSubmit.setOnClickListener(v->{
            updateCourier();
        });
    }

    private void initializeFields() {
        edtCourierName = findViewById(R.id.edtCourierName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtDeliveryCharges = findViewById(R.id.edtDeliveryCharges);
        edtDeliveryDays = findViewById(R.id.edtDeliveryDays);


        edtCourierName.setText(courierName);
        edtPhoneNumber.setText(phoneNumber);
        edtEmail.setText(email);

        String tmpDeliveryChargesString = deliveryCharges+"";
        edtDeliveryCharges.setText(tmpDeliveryChargesString);
        edtDeliveryDays.setText(deliveryDays);
    }

    public void updateCourier() {
        String tmpCourierName = edtCourierName.getText().toString().trim();
        String tmpPhoneNumber = edtPhoneNumber.getText().toString().trim();
        String tmpEmail = edtEmail.getText().toString().trim();
        String tmpDeliveryCharges = edtDeliveryCharges.getText().toString().trim();
        String tmpDeliveryDays = edtDeliveryDays.getText().toString().trim();
        if(tmpCourierName.isEmpty() || tmpPhoneNumber.isEmpty() || tmpEmail.isEmpty()) {
            Toast.makeText(this, "Please provide all details", Toast.LENGTH_SHORT).show();
            return;
        }

        double charges;
        try{
            charges = Double.parseDouble(tmpDeliveryCharges);
        }catch (NumberFormatException e) {
            charges = 0;
        }

//        TODO:

        courierController.updateCourier(courierId, tmpCourierName, tmpPhoneNumber, tmpEmail,charges,tmpDeliveryDays ,new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                finish();
                Toast.makeText(EditCourierActivity.this, data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(EditCourierActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isLoading(Boolean loading){
        if(loading == true) {
            loader.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);

        }else {
            loader.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
    }
}