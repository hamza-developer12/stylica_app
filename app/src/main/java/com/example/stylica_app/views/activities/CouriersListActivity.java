package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CourierController;
import com.example.stylica_app.models.CourierModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.adapters.CourierAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CouriersListActivity extends BaseActivity {

    FloatingActionButton fabAddCourier;

    CourierController courierController;

    CourierAdapter adapter;

    List<CourierModel> couriers;

    ListView itemsList;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_couriers_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAppBar("Couriers");

        couriers = new ArrayList<>();
        fabAddCourier = findViewById(R.id.fabAddCourier);
        courierController = CourierController.getInstance();
        loader = findViewById(R.id.loader);
        itemsList = findViewById(R.id.itemsList);

        fetchCouriers();
//        Add Courier Screen Route...
        fabAddCourier.setOnClickListener(v->{
            Intent i = new Intent(CouriersListActivity.this, AddCourierActivity.class);
            startActivity(i);
        });
    }

    public void fetchCouriers(){
        isLoading(true);
        courierController.listenForCouriers(new DatabaseService.RealtimeCallback<List<CourierModel>>() {
            @Override
            public void onDataChange(List<CourierModel> data) {

                isLoading(false);
                couriers.clear();
                couriers.addAll(data);
                Log.d("Couriers Data", couriers.toString());
                if(!couriers.isEmpty()) {
                    adapter = new CourierAdapter(CouriersListActivity.this, R.layout.custom_list_layout,(ArrayList<CourierModel>) couriers);
                    itemsList.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading(false);
            }
        });
    }

    private void isLoading(Boolean loading){
        if(loading == true) {
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.itemsList).setVisibility(View.GONE);

        }else {
            findViewById(R.id.loader).setVisibility(View.GONE);
            findViewById(R.id.itemsList
            ).setVisibility(View.VISIBLE);
        }
    }
}