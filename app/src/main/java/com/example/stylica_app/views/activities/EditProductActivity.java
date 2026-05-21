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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProductActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // Controllers & helpers
    ProductController productController;
    CategoryController categoryController;
    CloudinaryHelper cloudinaryHelper;

    // UI views
    ProgressBar categoryLoader, loader;
    LinearLayout productForm;
    Spinner spinnerProductCategory, spinnerProductSubCategory;
    ImageView productImage;
    EditText edtProductName, edtProductPrice, edtProductQuantity, edtProductDescription;
    Button btnSubmit;
    CheckBox cbNewArrival, cbFeatured;

    // Data
    List<CategoryModel> categories = new ArrayList<>();
    List<String> categoryNames = new ArrayList<>();
    Bitmap selectedImage = null; // null means user didn't change image
    String existingImageUrl = null;
    String productId = null;
    ProductModel existingProduct = null;

    SessionService sessionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_product);


        setupAppBar("Edit Product");

        sessionService = new SessionService(this);

        // Get productId passed from adapter
        productId = getIntent().getStringExtra("productId");
        if (productId == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Init views
        categoryLoader = findViewById(R.id.categoryLoader);
        productForm = findViewById(R.id.productForm);
        spinnerProductCategory = findViewById(R.id.spinnerProductCategory);
        spinnerProductSubCategory = findViewById(R.id.spinnerProductSubCategory);
        productImage = findViewById(R.id.productImage);
        loader = findViewById(R.id.loader);
        btnSubmit = findViewById(R.id.btnSubmit);
        cbNewArrival = findViewById(R.id.cbNewArrival);
        cbFeatured = findViewById(R.id.cbFeatured);
        edtProductName = findViewById(R.id.edtProductName);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        edtProductQuantity = findViewById(R.id.edtProductQuantity);
        edtProductDescription = findViewById(R.id.edtProductDescription);

        cloudinaryHelper = new CloudinaryHelper(this);
        categoryController = CategoryController.getInstance();
        productController = ProductController.getInstance(this);

        //  image change on click
        productImage.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            startActivityForResult(i, PICK_IMAGE_REQUEST);
        });

        // First fetch categories, then load product data
        fetchCategories();

        btnSubmit.setOnClickListener(v -> updateProduct());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), selectedImageUri);
                productImage.setImageBitmap(selectedImage);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
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

                for (CategoryModel cat : data) {
                    categories.add(cat);
                    categoryNames.add(cat.getCategoryName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        EditProductActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProductCategory.setAdapter(adapter);

                spinnerProductCategory.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int i, long id) {
                                if (i == 0) {
                                    spinnerProductSubCategory.setVisibility(View.GONE);
                                    return;
                                }
                                CategoryModel selected = categories.get(i - 1);
                                setupSubCategorySpinner(selected.getSubCategories());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });

                loadProductData();
            }

            @Override
            public void onFailure(String errorMessage) {
                isCategoriesLoading(false);
                Toast.makeText(EditProductActivity.this,
                        "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductData() {
        isCategoriesLoading(true);

        productController.getProductById(productId,
                new DatabaseService.DatabaseCallback<ProductModel>() {
                    @Override
                    public void onSuccess(ProductModel product) {
                        isCategoriesLoading(false);
                        existingProduct = product;
                        existingImageUrl = product.getImageUrl();
                        preFillForm(product);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        isCategoriesLoading(false);
                        Toast.makeText(EditProductActivity.this,
                                "Failed to load product", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    private void preFillForm(ProductModel product) {
        edtProductName.setText(product.getProductName());
        edtProductPrice.setText(String.valueOf(product.getPrice()));
        edtProductQuantity.setText(String.valueOf(product.getStockQuantity()));
        edtProductDescription.setText(product.getDescription());
        cbNewArrival.setChecked(product.getNew());
        cbFeatured.setChecked(product.getFeatured());

        // Load existing image with Glide
        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.select_image)
                .into(productImage);

        // Pre-select category spinner
        if (product.getCategory() != null) {
            for (int i = 0; i < categoryNames.size(); i++) {
                if (categoryNames.get(i).equalsIgnoreCase(product.getCategory())) {
                    spinnerProductCategory.setSelection(i);
                    break;
                }
            }
        }


        spinnerProductCategory.postDelayed(() -> {
            if (product.getSubcategory() != null) {
                for (int i = 0; i < spinnerProductSubCategory.getCount(); i++) {
                    String item = spinnerProductSubCategory.getItemAtPosition(i).toString();
                    if (item.equalsIgnoreCase(product.getSubcategory())) {
                        spinnerProductSubCategory.setSelection(i);
                        break;
                    }
                }
            }
        }, 300);
    }


    private void updateProduct() {
        String productName = edtProductName.getText().toString().trim();
        String productPrice = edtProductPrice.getText().toString().trim();
        String productQuantity = edtProductQuantity.getText().toString().trim();
        String productDescription = edtProductDescription.getText().toString().trim();
        int position = spinnerProductCategory.getSelectedItemPosition();
        String selectedCategory = spinnerProductCategory.getSelectedItem().toString();
        String selectedSubCategory = spinnerProductSubCategory.getSelectedItem() != null
                ? spinnerProductSubCategory.getSelectedItem().toString() : "";

        // Validation
        if (productName.isEmpty() || productPrice.isEmpty()
                || productQuantity.isEmpty() || productDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = sessionService.getUserRole();
        String domain = sessionService.getDomain();

        if(role.equals("vendor") && !domain.equals(selectedCategory)) {
            Toast.makeText(this, "Please select category matching your domain", Toast.LENGTH_SHORT).show();
            return;
        }

        if (position == 0) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isNewArrival = cbNewArrival.isChecked();
        boolean isFeatured = cbFeatured.isChecked();
        int quantity = Integer.parseInt(productQuantity);
        double price = Double.parseDouble(productPrice);

        processing(true);

        // If user picked a new image → upload it first
        if (selectedImage != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    String newImageUrl = cloudinaryHelper.uploadBitmap(selectedImage);
                    handler.post(() -> saveUpdate(productName, price, quantity,
                            selectedCategory, selectedSubCategory,
                            productDescription, newImageUrl,
                            isNewArrival, isFeatured));
                } catch (IOException e) {
                    handler.post(() -> {
                        processing(false);
                        Toast.makeText(this, "Image upload failed: "
                                + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });

        } else {

            //  use existing image URL
            saveUpdate(productName, price, quantity,
                    selectedCategory, selectedSubCategory,
                    productDescription, existingImageUrl,
                    isNewArrival, isFeatured);
        }
    }

    private void saveUpdate(String name, double price, int quantity,
                            String category, String subcategory,
                            String description, String imageUrl,
                            boolean isNewArrival, boolean isFeatured) {


        productController.updateProduct(
                productId, name, price, quantity,
                category, subcategory, description, imageUrl,
                isNewArrival, isFeatured,
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(EditProductActivity.this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(EditProductActivity.this, "Unable to update :" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupSubCategorySpinner(List<String> subCategories) {
        if (subCategories != null && !subCategories.isEmpty()) {
            spinnerProductSubCategory.setVisibility(View.VISIBLE);
            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, subCategories);
            subAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            spinnerProductSubCategory.setAdapter(subAdapter);
        } else {
            spinnerProductSubCategory.setVisibility(View.GONE);
        }
    }

    private void isCategoriesLoading(boolean loading) {
        if (loading) {
            categoryLoader.setVisibility(View.VISIBLE);
            productForm.setVisibility(View.GONE);
        } else {
            categoryLoader.setVisibility(View.GONE);
            productForm.setVisibility(View.VISIBLE);
        }
    }

    private void processing(boolean isProcessing) {
        if (isProcessing) {
            loader.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        } else {
            loader.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
    }
}