package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.ModeratorController;
import com.example.stylica_app.controllers.VendorController;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.adapters.AdminVendorAdapter;
import com.example.stylica_app.views.adapters.ModeratorAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class VendorsListActivity extends BaseActivity {

    ProgressBar loader;

    //Views
    ListView vendorsList;
    SearchView searchView;

    FloatingActionButton fabAddVendor;
    // Controller
    VendorController vendorController;

    List<UserModel> vendors = new ArrayList<>();
    List<UserModel> filteredVendors = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors_list);

        setupAppBar("Vendors");

        loader = findViewById(R.id.loader);
        vendorsList = findViewById(R.id.vendorsList);

        searchView = findViewById(R.id.searchView);
        fabAddVendor = findViewById(R.id.fabAddVendor);

        //Controllers
        vendorController = vendorController.getInstance(this);

        fetchVendors();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                searchVendor(s.trim());
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
        });


        fabAddVendor.setOnClickListener(v->{
            Intent i = new Intent(VendorsListActivity.this, AddVendorActivity.class);
            startActivity(i);
        });
    }


    public void fetchVendors() {
        loading(true);

        vendorController.listenForVendors(new DatabaseService.RealtimeCallback<List<UserModel>>() {
            @Override
            public void onDataChange(List<UserModel> data) {
                loading(false);
                vendors.addAll(data);
                AdminVendorAdapter adapter = new AdminVendorAdapter(VendorsListActivity.this,R.layout.custom_list_layout, (ArrayList<UserModel>) data);
                vendorsList.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                loading(false);
                Toast.makeText(VendorsListActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            loader.setVisibility(View.VISIBLE);
            vendorsList.setVisibility(View.GONE);
        } else {
            loader.setVisibility(View.GONE);
            vendorsList.setVisibility(View.VISIBLE);
        }
    }

    private void searchVendor(String keyword) {
        filteredVendors.clear();
        for(UserModel vendor : vendors) {
            if(
                    vendor.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                            vendor.getLastName().toLowerCase().contains(keyword.toLowerCase()) ||
                            vendor.getDomain().toLowerCase().contains(keyword.toLowerCase())
            ) {
                filteredVendors.add(vendor);

            }


        }

        AdminVendorAdapter adapter = new AdminVendorAdapter(
                VendorsListActivity.this,
                R.layout.custom_list_layout,
                (ArrayList<UserModel>) filteredVendors
        );

        vendorsList.setAdapter(adapter);
    }
}