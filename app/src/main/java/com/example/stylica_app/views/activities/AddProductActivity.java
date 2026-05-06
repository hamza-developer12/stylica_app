package com.example.stylica_app.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.controllers.ProductController;
import com.example.stylica_app.helpers.CloudinaryHelper;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.models.ProductModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddProductActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ProductController productController;
    ProgressBar categoryLoader;
    List<CategoryModel> categories = new ArrayList<CategoryModel>();
    List<String> categoryNames = new ArrayList<String>();


    CloudinaryHelper cloudinaryHelper;



    List<String> subCategories = new ArrayList<String>();
    CategoryController categoryController;
    SessionService sessionService;
    LinearLayout productForm;
    Spinner spinnerProductCategory;
    Spinner spinnerProductSubCategory;
    ImageView productImage;
    Bitmap selectedImage = null;

    EditText edtProductName, edtProductPrice, edtProductQuantity, edtProductDescription;
    Button btnSubmit;

    CheckBox cbNewArrival, cbFeatured;
    ProgressBar loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        setupAppBar("Add Product");
        sessionService = new SessionService(this);
        categoryLoader = findViewById(R.id.categoryLoader);
        productForm = findViewById(R.id.productForm);
        spinnerProductCategory = findViewById(R.id.spinnerProductCategory);
        spinnerProductSubCategory = findViewById(R.id.spinnerProductSubCategory);
        productImage = findViewById(R.id.productImage);
        cloudinaryHelper = new CloudinaryHelper(this);
        loader = findViewById(R.id.loader);
        btnSubmit = findViewById(R.id.btnSubmit);


        cbNewArrival = findViewById(R.id.cbNewArrival);
        cbFeatured = findViewById(R.id.cbFeatured);



        categoryController = CategoryController.getInstance();
        productController = ProductController.getInstance(this);

        fetchCategories();

        selectImageFromGallery();

        initializeInputFields();

        btnSubmit.setOnClickListener(v->{
            try {
                addProduct();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initializeInputFields() {
        edtProductName = findViewById(R.id.edtProductName);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        edtProductQuantity = findViewById(R.id.edtProductQuantity);
        edtProductDescription = findViewById(R.id.edtProductDescription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();


            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            productImage.setImageBitmap(selectedImage);
        }
    }

    public void addProduct() throws IOException {
        String productName = edtProductName.getText().toString().trim();
        String productPrice = edtProductPrice.getText().toString().trim();
        String productQuantity = edtProductQuantity.getText().toString().trim();
        String productDescription = edtProductDescription.getText().toString().trim();
        int position = spinnerProductCategory.getSelectedItemPosition();

        String selectedCategory = spinnerProductCategory.getSelectedItem().toString();

        String selectedSubCategory = spinnerProductSubCategory.getSelectedItem().toString();


        String role = sessionService.getUserRole();
        String domain = sessionService.getDomain();
        if(role.equals("moderator")&& !(selectedCategory.equals(domain))) {
            Toast.makeText(this, "Please select domain specific category", Toast.LENGTH_LONG).show();
            return;
        }

        if(selectedImage == null) {
            Toast.makeText(this, "Please provide product iamge", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if(productName.isEmpty() || productPrice.isEmpty() || productQuantity.isEmpty() || productDescription.isEmpty()) {
            Toast.makeText(this, "Please provide all details", Toast.LENGTH_SHORT).show();
            return;
        }

        if(position == 0) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isNewArrival = cbNewArrival.isChecked();
        boolean isFeatured = cbFeatured.isChecked();

        int quantity = Integer.parseInt(productQuantity);
        double price = Double.parseDouble(productPrice);
        String userName = sessionService.getUserName();
        String userId = sessionService.getUserId();

        String status;
        if(role.equals("admin")) {
            status = "approved";
        }else {
            status = "pending";
        }
        processing(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String imageUrl = cloudinaryHelper.uploadBitmap(selectedImage);

                // Back to UI thread
                handler.post(() -> {
                    productController.addProduct(
                            imageUrl,
                            productName,
                            price,
                            quantity,
                            selectedCategory,
                            selectedSubCategory,
                            productDescription,
                            status,
                            userId,
                            userName,
                            isNewArrival,
                            isFeatured,
                            new DatabaseService.DatabaseCallback<ProductModel>() {
                                @Override
                                public void onSuccess(ProductModel data) {
                                    processing(false);
                                    Toast.makeText(AddProductActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    processing(false);
                                    Toast.makeText(AddProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                });

            } catch (IOException e) {
                handler.post(() -> {
                    processing(false);
                    Toast.makeText(AddProductActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });


    }
    private void fetchCategories() {

        isCategoriesLoading(true);

        categoryController.getAllCategories(new DatabaseService.DatabaseCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> data) {
                isCategoriesLoading(false);
                categories.clear();
                categoryNames.clear();
                categoryNames.add("Select Category");
                for(CategoryModel cat : data) {
                    categories.add(cat);
                    categoryNames.add(cat.getCategoryName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddProductActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProductCategory.setAdapter(adapter);

                spinnerProductCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                        if(i == 0){
                            spinnerProductSubCategory.setVisibility(View.GONE);
                            return;
                        }


                        CategoryModel selectedCategory = categories.get(i - 1);

                        setupSubCategorySpinner(selectedCategory.getSubCategories());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });


    }
    private void isCategoriesLoading(boolean loading) {
        if(loading == true) {
            categoryLoader.setVisibility(View.VISIBLE);
            productForm.setVisibility(View.GONE);
        }else {
            categoryLoader.setVisibility(View.GONE);
            productForm.setVisibility(View.VISIBLE);
        }
    }

    private void setupSubCategorySpinner(List<String> subCategories) {

        if (subCategories != null && !subCategories.isEmpty()) {

            spinnerProductSubCategory.setVisibility(View.VISIBLE);

            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    subCategories
            );

            subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerProductSubCategory.setAdapter(subAdapter);

        } else {
            spinnerProductSubCategory.setVisibility(View.GONE);
        }
    }

    private void selectImageFromGallery() {
        productImage.setOnClickListener(v-> {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            startActivityForResult(i, PICK_IMAGE_REQUEST);
        });
    }

    private void processing(boolean isProcessing) {
        if(isProcessing == true) {
            loader.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }else {
            loader.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
    }
}