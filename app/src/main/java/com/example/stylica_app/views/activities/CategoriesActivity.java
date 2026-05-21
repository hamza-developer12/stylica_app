package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.adapters.CategoryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends BaseActivity {



    ProgressBar loader;
    ListView listView;
    FloatingActionButton floatingActionButton;
    String error = "";

    ArrayList<CategoryModel> categories = new ArrayList<CategoryModel>();
    CategoryController categoryController =  CategoryController.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_categories);



        setupAppBar("Categories");
        loader = findViewById(R.id.loader);
        listView = findViewById(R.id.categoryList);
        floatingActionButton = findViewById(R.id.fabAddCategory);
        getCategories();


        floatingActionButton.setOnClickListener(v->{
            Intent i = new Intent(CategoriesActivity.this, AddCategoryActivity.class);
            startActivity(i);
        });
    }





    public void getCategories(){
        isLoading(true);
        categoryController.listenCategories(new DatabaseService.RealtimeCallback<List<CategoryModel>>(){
            @Override
            public void onDataChange(List<CategoryModel> data) {
                isLoading(false);
                categories.clear();
                categories = (ArrayList<CategoryModel>) data;
                if(!categories.isEmpty()) {
                    CategoryAdapter adapter = new CategoryAdapter(CategoriesActivity.this, R.layout.category_list_layout, R.id.categoryText, categories);
                    listView.setAdapter(adapter);
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
            findViewById(R.id.categoryList).setVisibility(View.GONE);

        }else {
            findViewById(R.id.loader).setVisibility(View.GONE);
            findViewById(R.id.categoryList
            ).setVisibility(View.VISIBLE);
        }
    }
}