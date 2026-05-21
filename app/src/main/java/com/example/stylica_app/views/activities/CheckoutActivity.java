package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CourierController;
import com.example.stylica_app.controllers.OrderController;
import com.example.stylica_app.controllers.PaymentController;
import com.example.stylica_app.helpers.CartDatabaseHelper;
import com.example.stylica_app.helpers.CloudinaryHelper;
import com.example.stylica_app.models.CartModel;
import com.example.stylica_app.models.CourierModel;
import com.example.stylica_app.models.OrderModel;
import com.example.stylica_app.models.PaymentMethodModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.adapters.CourierSpinnerAdapter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckoutActivity extends BaseActivity {

    // Views
    LinearLayout orderItemsContainer, paymentMethodsContainer, screenshotPicker;
    EditText edtDeliveryAddress, edtNotes;
    TextView txtTotalAmount, txtDeliveryCharges, txtGrandTotal;
    ImageView imgScreenshot;
    Button btnPlaceOrder;
    ProgressBar loader, paymentLoader, courierLoader;
    Spinner couriersSpinner;

    // Controllers & helpers
    PaymentController paymentController;
    OrderController orderController;
    CourierController courierController;
    CartDatabaseHelper cartDb;
    CloudinaryHelper cloudinaryHelper;
    SessionService sessionService;

    // Data
    List<CartModel> cartItems;
    double totalAmount;
    double deliveryCharges = 0;
    PaymentMethodModel selectedPayment = null;
    CourierModel selectedCourier = null;
    Bitmap screenshotBitmap = null;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkout);

        setupAppBar("Checkout");

        // Init views
        orderItemsContainer    = findViewById(R.id.orderItemsContainer);
        paymentMethodsContainer = findViewById(R.id.paymentMethodsContainer);
        edtDeliveryAddress     = findViewById(R.id.edtDeliveryAddress);
        edtNotes               = findViewById(R.id.edtNotes);
        txtTotalAmount         = findViewById(R.id.txtTotalAmount);
        txtDeliveryCharges     = findViewById(R.id.txtDeliveryCharges);
        txtGrandTotal          = findViewById(R.id.txtGrandTotal);
        screenshotPicker       = findViewById(R.id.screenshotPicker);
        imgScreenshot          = findViewById(R.id.imgScreenshot);
        btnPlaceOrder          = findViewById(R.id.btnPlaceOrder);
        loader                 = findViewById(R.id.loader);
        paymentLoader          = findViewById(R.id.paymentLoader);
        courierLoader          = findViewById(R.id.courierLoader);
        couriersSpinner        = findViewById(R.id.couriersSpinner);

        // Init controllers
        paymentController  = PaymentController.getInstance();
        orderController    = OrderController.getInstance();
        courierController  = CourierController.getInstance();
        cartDb             = CartDatabaseHelper.getInstance(this);
        cloudinaryHelper   = new CloudinaryHelper(this);
        sessionService     = new SessionService(this);

        // Load data
        cartItems   = cartDb.getAllCartItems();
        totalAmount = cartDb.getCartTotal();

        showOrderSummary();
        loadCouriers();
        loadPaymentMethods();

        // Screenshot
        screenshotPicker.setOnClickListener(v -> pickImage());
        imgScreenshot.setOnClickListener(v -> pickImage());

        btnPlaceOrder.setOnClickListener(v -> validateAndPlaceOrder());
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    private void showOrderSummary() {
        orderItemsContainer.removeAllViews();

        for (CartModel item : cartItems) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin =
                    (int) (8 * getResources().getDisplayMetrics().density);
            row.setLayoutParams(params);

            TextView txtItem = new TextView(this);
            txtItem.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            txtItem.setText(item.getProductName()
                    + " x" + item.getQuantity());
            txtItem.setTextColor(getColor(R.color.text_secondary));
            txtItem.setTextSize(13);
            row.addView(txtItem);

            TextView txtPrice = new TextView(this);
            txtPrice.setText("Rs " + item.getTotalPrice());
            txtPrice.setTextColor(getColor(R.color.text_primary));
            txtPrice.setTextSize(13);
            txtPrice.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(txtPrice);

            orderItemsContainer.addView(row);
        }

        txtTotalAmount.setText("Rs " + totalAmount);
        updateGrandTotal();
    }

    private void updateGrandTotal() {
        double grandTotal = totalAmount + deliveryCharges;
        txtDeliveryCharges.setText("Rs " + deliveryCharges);
        txtGrandTotal.setText("Rs " + grandTotal);
    }

    // Load couriers into spinner
    private void loadCouriers() {
        courierLoader.setVisibility(View.VISIBLE);

        courierController.getAllCouriers(
                new DatabaseService.DatabaseCallback<List<CourierModel>>() {
                    @Override
                    public void onSuccess(List<CourierModel> data) {
                        courierLoader.setVisibility(View.GONE);

                        if (data == null || data.isEmpty()) {
                            Toast.makeText(CheckoutActivity.this,
                                    "No courier services available",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }


                        CourierSpinnerAdapter adapter =
                                new CourierSpinnerAdapter(
                                        CheckoutActivity.this, data);
                        couriersSpinner.setAdapter(adapter);


                        selectedCourier = data.get(0);
                        deliveryCharges = selectedCourier
                                .getDeliveryCharges();
                        updateGrandTotal();


                        couriersSpinner.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(
                                            AdapterView<?> parent,
                                            View view, int pos, long id) {
                                        selectedCourier = data.get(pos);
                                        deliveryCharges = selectedCourier
                                                .getDeliveryCharges();
                                        updateGrandTotal();
                                    }

                                    @Override
                                    public void onNothingSelected(
                                            AdapterView<?> parent) {}
                                });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        courierLoader.setVisibility(View.GONE);
                        Toast.makeText(CheckoutActivity.this,
                                "Failed to load couriers",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Load payment methods
    private void loadPaymentMethods() {
        paymentLoader.setVisibility(View.VISIBLE);

        paymentController.getAllPaymentMethods(
                new DatabaseService.DatabaseCallback<List<PaymentMethodModel>>() {
                    @Override
                    public void onSuccess(List<PaymentMethodModel> data) {
                        paymentLoader.setVisibility(View.GONE);

                        if (data == null || data.isEmpty()) {
                            TextView empty = new TextView(CheckoutActivity.this);
                            empty.setText("No payment methods available");
                            empty.setTextColor(getColor(R.color.text_hint));
                            empty.setTextSize(13);
                            paymentMethodsContainer.addView(empty);
                            return;
                        }

                        for (PaymentMethodModel method : data) {
                            addPaymentMethodCard(method);
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        paymentLoader.setVisibility(View.GONE);
                    }
                });
    }

    private void addPaymentMethodCard(PaymentMethodModel method) {
        androidx.cardview.widget.CardView card =
                new androidx.cardview.widget.CardView(this);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin =
                (int) (10 * getResources().getDisplayMetrics().density);
        card.setLayoutParams(cardParams);
        card.setRadius(12 * getResources().getDisplayMetrics().density);
        card.setCardElevation(2 * getResources().getDisplayMetrics().density);
        card.setCardBackgroundColor(getColor(R.color.card_background));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        content.setPadding(pad, pad, pad, pad);

        TextView icon = new TextView(this);
        int size = (int) (44 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(size, size);
        iconParams.setMarginEnd(
                (int) (12 * getResources().getDisplayMetrics().density));
        icon.setLayoutParams(iconParams);
        icon.setGravity(android.view.Gravity.CENTER);
        icon.setBackgroundResource(R.drawable.chip_unselected_bg);
        switch (method.getType()) {
            case "card":      icon.setText("💳"); break;
            case "jazzcash":  icon.setText("📱"); break;
            case "easypaisa": icon.setText("💚"); break;
            default:          icon.setText("💰");
        }
        icon.setTextSize(20);
        content.addView(icon);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView title = new TextView(this);
        title.setText(method.getAccountTitle());
        title.setTextColor(getColor(R.color.text_primary));
        title.setTextSize(14);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(title);

        TextView number = new TextView(this);
        number.setText(method.getAccountNumber());
        number.setTextColor(getColor(R.color.text_secondary));
        number.setTextSize(12);
        info.addView(number);

        TextView instructions = new TextView(this);
        instructions.setText(method.getInstructions());
        instructions.setTextColor(getColor(R.color.text_hint));
        instructions.setTextSize(11);
        info.addView(instructions);

        content.addView(info);

        TextView checkMark = new TextView(this);
        checkMark.setText("○");
        checkMark.setTextColor(getColor(R.color.text_hint));
        checkMark.setTextSize(20);
        content.addView(checkMark);

        card.addView(content);

        card.setOnClickListener(v -> {
            selectedPayment = method;

            // Reset all
            for (int i = 0; i < paymentMethodsContainer.getChildCount(); i++) {
                androidx.cardview.widget.CardView c =
                        (androidx.cardview.widget.CardView)
                                paymentMethodsContainer.getChildAt(i);
                c.setCardBackgroundColor(getColor(R.color.card_background));
                LinearLayout cl = (LinearLayout) c.getChildAt(0);
                TextView cm = (TextView) cl.getChildAt(
                        cl.getChildCount() - 1);
                cm.setText("○");
                cm.setTextColor(getColor(R.color.text_hint));
            }

            card.setCardBackgroundColor(
                    getColor(R.color.primary_light_variant_20));
            checkMark.setText("✓");
            checkMark.setTextColor(getColor(R.color.primary_light_variant));
        });

        paymentMethodsContainer.addView(card);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                screenshotBitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), uri);
                imgScreenshot.setImageBitmap(screenshotBitmap);
                imgScreenshot.setVisibility(View.VISIBLE);
                screenshotPicker.setVisibility(View.GONE);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateAndPlaceOrder() {
        String address = edtDeliveryAddress.getText().toString().trim();
        String notes   = edtNotes.getText().toString().trim();

        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter delivery address",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCourier == null) {
            Toast.makeText(this, "Please select a courier service",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedPayment == null) {
            Toast.makeText(this, "Please select a payment method",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (screenshotBitmap == null) {
            Toast.makeText(this, "Please upload payment screenshot",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        processing(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String screenshotUrl =
                        cloudinaryHelper.uploadBitmap(screenshotBitmap);
                handler.post(() -> placeOrder(address, notes, screenshotUrl));
            } catch (IOException e) {
                handler.post(() -> {
                    processing(false);
                    Toast.makeText(this,
                            "Screenshot upload failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void placeOrder(String address, String notes, String screenshotUrl) {
        String customerId   = sessionService.getUserId();
        String customerName = sessionService.getUserName();
        double grandTotal   = totalAmount + deliveryCharges;

        OrderModel order = new OrderModel(
                null,
                selectedPayment.getId(),
                selectedPayment.getAccountTitle(),
                selectedPayment.getType(),
                selectedCourier.getCourierId(),
                selectedCourier.getCourierName(),
                selectedCourier.getDeliveryDays(),
                "pending",
                screenshotUrl,
                deliveryCharges,
                grandTotal
        );

        orderController.placeOrder(
                order,
                cartItems,
                customerId,
                customerName,
                selectedCourier.getDeliveryDays(),
                new OrderController.PlaceOrderCallback() {
                    @Override
                    public void onSuccess(String orderId) {
                        cartDb.clearCart();
                        processing(false);
                        Toast.makeText(CheckoutActivity.this,
                                "Order placed successfully!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(CheckoutActivity.this, MyOrdersActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        processing(false);
                        Toast.makeText(CheckoutActivity.this,
                                "Order failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processing(boolean isProcessing) {
        loader.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
        btnPlaceOrder.setVisibility(
                isProcessing ? View.GONE : View.VISIBLE);
    }
}