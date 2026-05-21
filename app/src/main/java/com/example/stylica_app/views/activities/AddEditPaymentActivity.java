package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.PaymentController;
import com.example.stylica_app.models.PaymentMethodModel;
import com.example.stylica_app.services.DatabaseService;

public class AddEditPaymentActivity extends BaseActivity {


    TextView chipCard, chipJazzcash, chipEasypaisa;
    EditText edtAccountTitle, edtAccountNumber, edtInstructions;
    Button btnSave;
    ProgressBar loader;

    PaymentController paymentController;

    String selectedType = "account";
    boolean isEdit = false;
    String paymentId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_payment);


        // Check if edit mode
        isEdit    = getIntent().getBooleanExtra("isEdit", false);
        paymentId = getIntent().getStringExtra("paymentId");

        setupAppBar(isEdit ? "Edit Payment Method" : "Add Payment Method");

        // Init views
        chipCard       = findViewById(R.id.chipCard);
        chipJazzcash   = findViewById(R.id.chipJazzcash);
        chipEasypaisa  = findViewById(R.id.chipEasypaisa);
        edtAccountTitle  = findViewById(R.id.edtAccountTitle);
        edtAccountNumber = findViewById(R.id.edtAccountNumber);
        edtInstructions  = findViewById(R.id.edtInstructions);
        btnSave          = findViewById(R.id.btnSave);
        loader           = findViewById(R.id.loader);

        paymentController = PaymentController.getInstance();

        // If edit — pre-fill fields
        if (isEdit) {
            preFill();
        }

        // Type chips
        setupTypeChips();

        // Save button
        btnSave.setOnClickListener(v -> savePaymentMethod());
    }

    // Pre-fill form for edit mode
    private void preFill() {
        selectedType = getIntent().getStringExtra("type");
        edtAccountTitle.setText(getIntent().getStringExtra("accountTitle"));
        edtAccountNumber.setText(getIntent().getStringExtra("accountNumber"));
        edtInstructions.setText(getIntent().getStringExtra("instructions"));

        // Update chip selection
        updateChips(selectedType);
    }

    // Chip selection logic
    private void setupTypeChips() {
        chipCard.setOnClickListener(v -> {
            selectedType = "account";
            updateChips("account");
        });

        chipJazzcash.setOnClickListener(v -> {
            selectedType = "jazzcash";
            updateChips("jazzcash");
        });

        chipEasypaisa.setOnClickListener(v -> {
            selectedType = "easypaisa";
            updateChips("easypaisa");
        });
    }

    // Update chip UI — selected and un-selected unselected
    private void updateChips(String selected) {
        // Reset all
        chipCard.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipCard.setTextColor(getColor(R.color.text_secondary));
        chipJazzcash.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipJazzcash.setTextColor(getColor(R.color.text_secondary));
        chipEasypaisa.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipEasypaisa.setTextColor(getColor(R.color.text_secondary));

        // Highlight selected
        switch (selected) {
            case "account":
                chipCard.setBackgroundResource(R.drawable.chip_selected_bg);
                chipCard.setTextColor(getColor(R.color.text_white));
                break;
            case "jazzcash":
                chipJazzcash.setBackgroundResource(R.drawable.chip_selected_bg);
                chipJazzcash.setTextColor(getColor(R.color.text_white));
                break;
            case "easypaisa":
                chipEasypaisa.setBackgroundResource(R.drawable.chip_selected_bg);
                chipEasypaisa.setTextColor(getColor(R.color.text_white));
                break;
        }
    }

    private void savePaymentMethod() {
        String accountTitle  = edtAccountTitle.getText().toString().trim();
        String accountNumber = edtAccountNumber.getText().toString().trim();
        String instructions  = edtInstructions.getText().toString().trim();

        // Validation
        if (accountTitle.isEmpty() || accountNumber.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        processing(true);

        if (isEdit) {
            // Update existing
            paymentController.updatePaymentMethod(
                    paymentId, selectedType, accountTitle,
                    accountNumber, instructions,
                    new PaymentController.UpdateCallback() {
                        @Override
                        public void onSuccess() {
                            processing(false);
                            Toast.makeText(AddEditPaymentActivity.this,
                                    "Updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String error) {
                            processing(false);
                            Toast.makeText(AddEditPaymentActivity.this,
                                    "Failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Add new
            PaymentMethodModel method = new PaymentMethodModel(
                    null, selectedType, accountTitle, accountNumber, instructions);

            paymentController.addPaymentMethod(method,
                    new DatabaseService.DatabaseCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            processing(false);
                            Toast.makeText(AddEditPaymentActivity.this,
                                    "Payment method added", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            processing(false);
                            Toast.makeText(AddEditPaymentActivity.this,
                                    "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void processing(boolean isProcessing) {
        if (isProcessing) {
            loader.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
        } else {
            loader.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        }
    }
}