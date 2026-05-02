package com.example.stylica_app.views.activities;

import android.os.Bundle;
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

public class AddCourierActivity extends BaseActivity  {

    CourierController courierController;

    EditText edtCourierName, edtPhoneNumber, edtEmail;

    Button btnSubmit;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_courier);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAppBar("Add Courier");
        initializeUI();
        courierController = CourierController.getInstance();

        btnSubmit.setOnClickListener(v->{
            addCourier();
        });
    }

    private void initializeUI() {
        edtCourierName = findViewById(R.id.edtCourierName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEmail = findViewById(R.id.edtEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        loader = findViewById(R.id.loader);
    }

    public void addCourier(){
        String courierName = edtCourierName.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if(courierName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please provide all details", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading(true);
        courierController.addCourier(courierName, phoneNumber, email, new DatabaseService.DatabaseCallback<String>() {

            @Override
            public void onSuccess(String data) {
                isLoading(false);
                Toast.makeText(AddCourierActivity.this, "Courier added Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading(false);
                Toast.makeText(AddCourierActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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