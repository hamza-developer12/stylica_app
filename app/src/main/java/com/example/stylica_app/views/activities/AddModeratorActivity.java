package com.example.stylica_app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.CategoryController;
import com.example.stylica_app.controllers.ModeratorController;
import com.example.stylica_app.models.CategoryModel;
import com.example.stylica_app.services.DatabaseService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddModeratorActivity extends BaseActivity {

    EditText edtFirstName;
    EditText edtLastName;
    EditText edtEmail;
    Spinner spinnerProductDomain;
    EditText edtPassword;

    LinearLayout moderatorForm;

    CategoryController categoryController = CategoryController.getInstance();
    ModeratorController moderatorController;

    List<CategoryModel> categories = new ArrayList<CategoryModel>();
    List<String> categoryNames = new ArrayList<String>();
    ProgressBar loader;
    ProgressBar categoryLoader;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_moderator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        setupAppBar("Add Moderator");

        moderatorController = ModeratorController.getInstance(this);

        moderatorForm = findViewById(R.id.moderatorForm);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        spinnerProductDomain = findViewById(R.id.spinnerProductDomain);
        edtPassword = findViewById(R.id.edtPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        categoryLoader = findViewById(R.id.categoryLoader);
        loader = findViewById(R.id.loader);

        fetchCategories();

        btnSubmit.setOnClickListener(v->{
            addModerator();
        });
    }

    public void addModerator() {
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String domain = spinnerProductDomain.getSelectedItem().toString();
        String password = edtPassword.getText().toString();

        int position = spinnerProductDomain.getSelectedItemPosition();


        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()  || password.isEmpty()) {
            Toast.makeText(this, "Please Provide all details", Toast.LENGTH_SHORT).show();
            return;
        }
        if(position == 0) {
            Toast.makeText(this, "Please select a domain", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        loader.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);


        moderatorController.addModerator(firstName, lastName, email, password, domain, new ModeratorController.ModeratorCallback()  {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(AddModeratorActivity.this, message, Toast.LENGTH_SHORT).show();
                loader.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddModeratorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                loader.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
            }
            @Override
            public void onApiError(JSONObject error) {
                try {
                    String errorMsg = error.has("msg") ? error.getString("msg") : "Unknown API Error";
                    Toast.makeText(AddModeratorActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(AddModeratorActivity.this, "API Error occurred", Toast.LENGTH_SHORT).show();
                }
                loader.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
            }

        });

    }

    public void fetchCategories(){
        categoryLoader.setVisibility(View.VISIBLE);
        moderatorForm.setVisibility(View.GONE);
        categoryController.getAllCategories(new DatabaseService.DatabaseCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> data) {
                categoryLoader.setVisibility(View.GONE);
                moderatorForm.setVisibility(View.VISIBLE);
                categories.clear();
                categoryNames.clear();
                categoryNames.add("Select Domain");

                for(CategoryModel cat : data) {

                    categoryNames.add(cat.getCategoryName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddModeratorActivity.this,
                        android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProductDomain.setAdapter(adapter);

            }

            @Override
            public void onFailure(String errorMessage) {
                categoryLoader.setVisibility(View.GONE);
                moderatorForm.setVisibility(View.VISIBLE);
                Toast.makeText(AddModeratorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}