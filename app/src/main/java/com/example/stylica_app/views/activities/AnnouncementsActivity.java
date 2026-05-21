package com.example.stylica_app.views.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.AnnouncementController;
import com.example.stylica_app.models.AnnouncementModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.services.SessionService;
import com.example.stylica_app.views.adapters.AnnouncementAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnnouncementsActivity extends BaseActivity {

    RecyclerView recyclerView;
    ProgressBar loader;
    LinearLayout emptyView;
    EditText searchAnnouncement;
    LinearLayout chipGroup;
    FloatingActionButton fabAdd;
    Button btnStartDate, btnEndDate, btnClearDateRange;
    TextView tvDateRange;

    AnnouncementController controller;
    AnnouncementAdapter adapter;

    SessionService sessionService;

    List<AnnouncementModel> allAnnouncements = new ArrayList<>();
    String selectedType = "All";

    // Date range variables
    private Date startDate = null;
    private Date endDate = null;

    // Type options
    String[] types = {"All", "News", "Event", "General"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        setupAppBar("Announcements");

        sessionService = new SessionService(this);
        recyclerView = findViewById(R.id.announcementsView);
        loader = findViewById(R.id.loader);
        emptyView = findViewById(R.id.emptyView);
        searchAnnouncement = findViewById(R.id.searchAnnouncement);
        chipGroup = findViewById(R.id.chipGroup);
        fabAdd = findViewById(R.id.fabAdd);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnClearDateRange = findViewById(R.id.btnClearDateRange);
        tvDateRange = findViewById(R.id.tvDateRange);

        controller = AnnouncementController.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Build type filter chips
        buildChips();

        // Setup date range filters
        setupDateRangeFilters();

        String role = sessionService.getUserRole();
        if (!role.equals("admin")) {
            fabAdd.setVisibility(View.GONE);
        }

        // Floating action button
        fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditAnnouncementActivity.class);
            i.putExtra("isEdit", false);
            startActivity(i);
        });

        // Real-time search
        searchAnnouncement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadAnnouncements();
    }

    private void setupDateRangeFilters() {
        btnStartDate.setOnClickListener(v -> showStartDatePickerDialog());
        btnEndDate.setOnClickListener(v -> showEndDatePickerDialog());
        btnClearDateRange.setOnClickListener(v -> clearDateRangeFilter());
    }

    private void showStartDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, dayOfMonth, 0, 0, 0);
                    startDate = selectedCal.getTime();

                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    btnStartDate.setText(displayFormat.format(startDate));

                    // If end date is before start date, clear end date
                    if (endDate != null && endDate.before(startDate)) {
                        clearEndDate();
                    }

                    updateDateRangeDisplay();
                    applyFilters();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showEndDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        if (startDate != null && startDate.before(calendar.getTime())) {
            calendar.setTime(startDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, dayOfMonth, 23, 59, 59);
                    endDate = selectedCal.getTime();

                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    btnEndDate.setText(displayFormat.format(endDate));

                    // Validate date range
                    if (startDate != null && endDate.before(startDate)) {
                        Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                        clearEndDate();
                    } else {
                        updateDateRangeDisplay();
                        applyFilters();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void clearDateRangeFilter() {
        startDate = null;
        endDate = null;
        btnStartDate.setText("Select");
        btnEndDate.setText("Select");
        tvDateRange.setVisibility(View.GONE);
        applyFilters();
    }

    private void clearEndDate() {
        endDate = null;
        btnEndDate.setText("Select");
        updateDateRangeDisplay();
    }

    private void updateDateRangeDisplay() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        if (startDate != null && endDate != null) {
            tvDateRange.setText(String.format("📅 %s - %s",
                    displayFormat.format(startDate), displayFormat.format(endDate)));
            tvDateRange.setVisibility(View.VISIBLE);
        } else if (startDate != null) {
            tvDateRange.setText(String.format("📅 From %s onwards", displayFormat.format(startDate)));
            tvDateRange.setVisibility(View.VISIBLE);
        } else if (endDate != null) {
            tvDateRange.setText(String.format("📅 Until %s", displayFormat.format(endDate)));
            tvDateRange.setVisibility(View.VISIBLE);
        } else {
            tvDateRange.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();
    }

    private void buildChips() {
        chipGroup.removeAllViews();
        for (String type : types) {
            TextView chip = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) (36 * getResources().getDisplayMetrics().density));
            params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
            chip.setLayoutParams(params);
            chip.setText(type);
            chip.setGravity(android.view.Gravity.CENTER);
            int px = (int) (16 * getResources().getDisplayMetrics().density);
            chip.setPadding(px, 0, px, 0);
            chip.setTextSize(13);

            if (type.equals(selectedType)) {
                chip.setBackgroundResource(R.drawable.chip_selected_bg);
                chip.setTextColor(getColor(R.color.text_white));
            } else {
                chip.setBackgroundResource(R.drawable.chip_unselected_bg);
                chip.setTextColor(getColor(R.color.text_secondary));
            }

            chip.setOnClickListener(v -> {
                selectedType = type;
                buildChips();
                applyFilters();
            });

            chipGroup.addView(chip);
        }
    }

    private void loadAnnouncements() {
        loading(true);

        controller.getAllAnnouncements(
                new DatabaseService.DatabaseCallback<List<AnnouncementModel>>() {
                    @Override
                    public void onSuccess(List<AnnouncementModel> data) {
                        loading(false);
                        if (data == null) data = new ArrayList<>();
                        allAnnouncements = data;
                        applyFilters();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        loading(false);
                        Toast.makeText(AnnouncementsActivity.this,
                                "Failed to load", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyFilters() {
        String keyword = searchAnnouncement.getText().toString();
        List<AnnouncementModel> filtered = new ArrayList<>();

        for (AnnouncementModel announcement : allAnnouncements) {
            boolean typeMatch = selectedType.equals("All")
                    || (announcement.getType() != null
                    && announcement.getType().equalsIgnoreCase(selectedType));

            boolean searchMatch = keyword == null || keyword.trim().isEmpty()
                    || (announcement.getTitle() != null
                    && announcement.getTitle().toLowerCase()
                    .contains(keyword.toLowerCase()));

            boolean dateMatch = matchesDateRangeCriteria(announcement);

            if (typeMatch && searchMatch && dateMatch) {
                filtered.add(announcement);
            }
        }

        if (filtered.isEmpty()) {
            showEmpty(true);
            if (hasActiveFilters()) {
                Toast.makeText(this, "No announcements found matching criteria", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        showEmpty(false);

        adapter = new AnnouncementAdapter(this, filtered, (announcement, position) -> {
            controller.deleteAnnouncement(announcement.getId(),
                    new AnnouncementController.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            // Remove from master list too
                            allAnnouncements.remove(announcement);
                            adapter.removeItem(announcement);
                            Toast.makeText(AnnouncementsActivity.this,
                                    "Deleted ✓", Toast.LENGTH_SHORT).show();
                            if (adapter.getItemCount() == 0) showEmpty(true);
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(AnnouncementsActivity.this,
                                    "Failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        recyclerView.setAdapter(adapter);

        if (hasActiveFilters() && !filtered.isEmpty()) {
            Toast.makeText(this, String.format("Found %d announcements", filtered.size()), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean matchesDateRangeCriteria(AnnouncementModel announcement) {
        if (startDate == null && endDate == null) {
            return true;
        }

        Date announcementDate = parseTimestamp(announcement.getCreatedAt());

        if (announcementDate == null) {
            return startDate == null && endDate == null;
        }

        boolean afterStartDate = startDate == null || !announcementDate.before(startDate);
        boolean beforeEndDate = endDate == null || !announcementDate.after(endDate);

        return afterStartDate && beforeEndDate;
    }

    private Date parseTimestamp(Object timestamp) {
        if (timestamp == null) return null;

        try {
            // Handle Firestore Timestamp
            if (timestamp instanceof com.google.firebase.Timestamp) {
                return ((com.google.firebase.Timestamp) timestamp).toDate();
            }
            // Handle Date object
            else if (timestamp instanceof Date) {
                return (Date) timestamp;
            }
            // Handle Long (milliseconds)
            else if (timestamp instanceof Long) {
                return new Date((Long) timestamp);
            }
            // Handle String format
            else if (timestamp instanceof String) {
                String dateStr = (String) timestamp;
                String[] formats = {
                        "dd MMM yyyy 'at' HH:mm:ss z",
                        "dd MMM yyyy",
                        "yyyy-MM-dd HH:mm:ss",
                        "yyyy-MM-dd"
                };

                for (String format : formats) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                        return sdf.parse(dateStr);
                    } catch (ParseException e) {
                        // Try next format
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean hasActiveFilters() {
        return startDate != null || endDate != null ||
                !searchAnnouncement.getText().toString().isEmpty() ||
                !selectedType.equals("All");
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