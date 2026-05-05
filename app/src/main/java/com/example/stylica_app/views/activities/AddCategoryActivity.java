package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {

    ImageView backBtn;
    TextView screenTitle;

    EditText category;
    EditText subCategories;
    CategoryController categoryController =  CategoryController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       initializeAppBar();

       initializeInputFields();



    }

    public void addCategory(View view) {

        String tmpCategory = category.getText().toString();

        if(tmpCategory.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading(true);

        String tempSub = subCategories.getText().toString().trim();
        String[] subctgsArray = tempSub.split(",");

        ArrayList<String> subctgs = new ArrayList<>();
        for (String sub : subctgsArray) {
            sub = sub.trim();
            if (!sub.isEmpty()) {
                subctgs.add(sub);
            }
        }


        categoryController.addCategory(tmpCategory, subctgs, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                isLoading(false);
                Toast.makeText(AddCategoryActivity.this, data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading(false);
                Toast.makeText(AddCategoryActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isLoading(Boolean loading){
        if(loading == true) {
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSubmit).setVisibility(View.GONE);

        }else {
            findViewById(R.id.loader).setVisibility(View.GONE);
            findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
        }
    }
    private void initializeInputFields() {
        category = findViewById(R.id.edtCategory);
        subCategories = findViewById(R.id.edtSubCategories);
    }

    private void initializeAppBar() {
        backBtn = findViewById(R.id.btnBack);
        screenTitle = findViewById(R.id.screenTitle);

        backBtn.setOnClickListener(v->{
            finish();
        });

        screenTitle.setText("Add Category");

    }



}