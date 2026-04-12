package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.services.DatabaseService;

import java.util.ArrayList;
import java.util.Arrays;

public class EditCategoryActivity extends BaseActivity {

    String categoryName;
    ArrayList<String> subCategories;
    String categoryId;

    EditText categoryInput;
    EditText subCategoriesInput;

    Button submitBtn;

    CategoryController categoryController =  CategoryController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        AppBar...
        setupAppBar("Edit Category");
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");
        subCategories = (ArrayList<String>) getIntent().getSerializableExtra("subcategories");

        initializeInputFields();
        submitBtn = findViewById(R.id.btnSubmit);

    }

    private void initializeInputFields() {

        categoryInput = findViewById(R.id.edtCategory);
        subCategoriesInput = findViewById(R.id.edtSubCategories);
        categoryInput.setText(categoryName);
        subCategoriesInput.setText(String.join(",", subCategories));
    }

    public void updateCategory(View view) {

        String tmpCategory = categoryInput.getText().toString();

        if(tmpCategory.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading(true);

        String tempSub = subCategoriesInput.getText().toString();
        String[] subctgsArray = tempSub.split(",");
        ArrayList<String> subctgs = new ArrayList<String>(Arrays.asList(subctgsArray));
        categoryController.updateCategory(categoryId, tmpCategory, subctgs,new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                isLoading(false);
                Toast.makeText(EditCategoryActivity.this, data, Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading(false);
                Toast.makeText(EditCategoryActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
}