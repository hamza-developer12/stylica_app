package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.PaymentController;
import com.example.stylica_app.models.PaymentMethodModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.adapters.PaymentMethodAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsActivity extends BaseActivity {

    RecyclerView recyclerView;
    ProgressBar loader;
    LinearLayout emptyView;
    FloatingActionButton fabAdd;

    PaymentController paymentController;
    PaymentMethodAdapter adapter;
    List<PaymentMethodModel> methods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_methods);


        setupAppBar("Payment Methods");

        recyclerView      = findViewById(R.id.paymentMethodsView);
        loader            = findViewById(R.id.loader);
        emptyView         = findViewById(R.id.emptyView);
        fabAdd            = findViewById(R.id.fabAddPayment);
        paymentController = PaymentController.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // FAB → open Add screen
        fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditPaymentActivity.class);
            i.putExtra("isEdit", false);
            startActivity(i);
        });

        loadPaymentMethods();
    }

    // Reload list every time screen comes back
    @Override
    protected void onResume() {
        super.onResume();
        loadPaymentMethods();
    }

    private void loadPaymentMethods() {
        loading(true);

        paymentController.getAllPaymentMethods(
                new DatabaseService.DatabaseCallback<List<PaymentMethodModel>>() {
                    @Override
                    public void onSuccess(List<PaymentMethodModel> data) {
                        loading(false);

                        if (data == null || data.isEmpty()) {
                            showEmpty(true);
                            return;
                        }

                        showEmpty(false);
                        methods = data;

                        adapter = new PaymentMethodAdapter(
                                PaymentMethodsActivity.this,
                                methods,
                                (method, position) -> {
                                    // Delete
                                    paymentController.deletePaymentMethod(
                                            method.getId(),
                                            new PaymentController.DeleteCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    adapter.removeItem(position);
                                                    Toast.makeText(PaymentMethodsActivity.this,
                                                            "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    if (adapter.getItemCount() == 0) {
                                                        showEmpty(true);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(String error) {
                                                    Toast.makeText(PaymentMethodsActivity.this,
                                                            "Failed: " + error,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });

                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loading(false);
                        showEmpty(true);
                    }
                });
    }

    private void loading(boolean isLoading) {
        loader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmpty(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}