package com.example.stylica_app.views.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.AnnouncementController;
import com.example.stylica_app.services.DatabaseService;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditAnnouncementActivity extends BaseActivity {

    EditText edtTitle, edtDescription;
    TextView chipNews, chipEvent, chipGeneral;
    TextView txtSelectedDate;
    LinearLayout datePickerBtn;
    Button btnSave;
    ProgressBar loader;

    AnnouncementController controller;

    String selectedType = "Sale";
    Calendar selectedDate = Calendar.getInstance();
    boolean dateSelected = false;

    boolean isEdit = false;
    String editId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_announcement);


        isEdit = getIntent().getBooleanExtra("isEdit", false);
        setupAppBar(isEdit ? "Edit Announcement" : "Add Announcement");

        edtTitle       = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        chipNews       = findViewById(R.id.chipNews);
        chipEvent      = findViewById(R.id.chipEvent);
        chipGeneral    = findViewById(R.id.chipGeneral);
        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        datePickerBtn  = findViewById(R.id.datePickerBtn);
        btnSave        = findViewById(R.id.btnSave);
        loader         = findViewById(R.id.loader);

        controller = AnnouncementController.getInstance();

        setupTypeChips();
        setupDatePicker();

        // Pre-fill if edit mode
        if (isEdit) {
            editId = getIntent().getStringExtra("id");
            edtTitle.setText(getIntent().getStringExtra("title"));
            edtDescription.setText(getIntent().getStringExtra("description"));
            String type = getIntent().getStringExtra("type");
            if (type != null) {
                selectedType = type;
                updateChips(type);
            }
            long dateMs = getIntent().getLongExtra("date", -1);
            if (dateMs != -1) {
                selectedDate.setTimeInMillis(dateMs);
                dateSelected = true;
                txtSelectedDate.setText(new SimpleDateFormat(
                        "dd MMM yyyy", Locale.getDefault())
                        .format(selectedDate.getTime()));
                txtSelectedDate.setTextColor(getColor(R.color.text_primary));
            }
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void setupTypeChips() {
        chipNews.setOnClickListener(v -> updateChips("News"));
        chipEvent.setOnClickListener(v -> updateChips("Event"));
        chipGeneral.setOnClickListener(v -> updateChips("General"));
    }

    private void updateChips(String selected) {
        selectedType = selected;

        // Reset all chips
        chipNews.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipNews.setTextColor(getColor(R.color.text_secondary));

        chipEvent.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipEvent.setTextColor(getColor(R.color.text_secondary));

        chipGeneral.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipGeneral.setTextColor(getColor(R.color.text_secondary));

        // Highlight selected
        switch (selected) {
            case "News":
                chipNews.setBackgroundResource(R.drawable.chip_selected_bg);
                chipNews.setTextColor(getColor(R.color.text_white));
                break;

            case "Event":
                chipEvent.setBackgroundResource(R.drawable.chip_selected_bg);
                chipEvent.setTextColor(getColor(R.color.text_white));
                break;
            case "General":
                chipGeneral.setBackgroundResource(R.drawable.chip_selected_bg);
                chipGeneral.setTextColor(getColor(R.color.text_white));
                break;
        }
    }

    private void setupDatePicker() {
        datePickerBtn.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate.set(year, month, day);
                dateSelected = true;
                String formatted = new SimpleDateFormat(
                        "dd MMM yyyy", Locale.getDefault())
                        .format(selectedDate.getTime());
                txtSelectedDate.setText(formatted);
                txtSelectedDate.setTextColor(getColor(R.color.text_primary));
            },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void save() {
        String title       = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dateSelected) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        processing(true);
        Timestamp timestamp = new Timestamp(new Date(selectedDate.getTimeInMillis()));

        if (isEdit) {
            controller.updateAnnouncement(editId, title, description,
                    selectedType, timestamp,
                    new AnnouncementController.UpdateCallback() {
                        @Override
                        public void onSuccess() {
                            processing(false);
                            Toast.makeText(AddEditAnnouncementActivity.this,
                                    "Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String error) {
                            processing(false);
                            Toast.makeText(AddEditAnnouncementActivity.this,
                                    "Failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            controller.addAnnouncement(title, description,
                    selectedType, timestamp,
                    new DatabaseService.DatabaseCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            processing(false);
                            Toast.makeText(AddEditAnnouncementActivity.this,
                                    "Announcement added", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            processing(false);
                            Toast.makeText(AddEditAnnouncementActivity.this,
                                    "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void processing(boolean isProcessing) {
        loader.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
        btnSave.setVisibility(isProcessing ? View.GONE : View.VISIBLE);
    }
}