package com.example.stylica_app.views.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stylica_app.R;
import com.example.stylica_app.controllers.ModeratorController;
import com.example.stylica_app.models.UserModel;
import com.example.stylica_app.services.DatabaseService;
import com.example.stylica_app.views.adapters.ModeratorAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ModeratorsListActivity extends BaseActivity {

    ProgressBar loader;
    ListView moderatorsList;
    SearchView searchView;
    FloatingActionButton fabAddModerator;
    Button btnStartDate, btnEndDate, btnApplyDateRange, btnClearDateRange;
    TextView tvDateRange;

    ModeratorController moderatorController;

    List<UserModel> moderators = new ArrayList<>();
    List<UserModel> filteredModerators = new ArrayList<>();

    // Date range variables
    private Date startDate = null;
    private Date endDate = null;
    private String currentSearchKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderators_list);

        setupAppBar("Moderators");

        loader = findViewById(R.id.loader);
        moderatorsList = findViewById(R.id.moderatorsList);
        searchView = findViewById(R.id.searchView);
        fabAddModerator = findViewById(R.id.fabAddModerator);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnApplyDateRange = findViewById(R.id.btnApplyDateRange);
        btnClearDateRange = findViewById(R.id.btnClearDateRange);
        tvDateRange = findViewById(R.id.tvDateRange);

        moderatorController = ModeratorController.getInstance(this);

        fetchModerators();

        setupSearchView();
        setupDateRangeFilters();

        fabAddModerator.setOnClickListener(v -> {
            startActivity(new Intent(ModeratorsListActivity.this, AddModeratorActivity.class));
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                currentSearchKeyword = s.trim();
                applyFilters();
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
        });
    }

    private void setupDateRangeFilters() {
        btnStartDate.setOnClickListener(v -> showStartDatePickerDialog());
        btnEndDate.setOnClickListener(v -> showEndDatePickerDialog());
        btnApplyDateRange.setOnClickListener(v -> applyFilters());
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
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showEndDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        // Set initial date to start date if available and if it's after current calendar
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
        btnStartDate.setText("Select Start Date");
        btnEndDate.setText("Select End Date");
        tvDateRange.setVisibility(View.GONE);
        applyFilters();
    }

    private void clearEndDate() {
        endDate = null;
        btnEndDate.setText("Select End Date");
        updateDateRangeDisplay();
    }

    private void updateDateRangeDisplay() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        if (startDate != null && endDate != null) {
            tvDateRange.setText(String.format("Showing moderators created between %s and %s",
                    displayFormat.format(startDate), displayFormat.format(endDate)));
            tvDateRange.setVisibility(View.VISIBLE);
        } else if (startDate != null) {
            tvDateRange.setText(String.format("Showing moderators created from %s onwards",
                    displayFormat.format(startDate)));
            tvDateRange.setVisibility(View.VISIBLE);
        } else if (endDate != null) {
            tvDateRange.setText(String.format("Showing moderators created until %s",
                    displayFormat.format(endDate)));
            tvDateRange.setVisibility(View.VISIBLE);
        } else {
            tvDateRange.setVisibility(View.GONE);
        }
    }

    private void applyFilters() {
        if (moderators.isEmpty()) return;

        filteredModerators.clear();

        for (UserModel moderator : moderators) {
            boolean matchesSearch = matchesSearchCriteria(moderator);
            boolean matchesDateRange = matchesDateRangeCriteria(moderator);

            if (matchesSearch && matchesDateRange) {
                filteredModerators.add(moderator);
            }
        }

        updateAdapter();


    }

    private boolean hasActiveFilters() {
        return startDate != null || endDate != null || !currentSearchKeyword.isEmpty();
    }

    private boolean matchesSearchCriteria(UserModel moderator) {
        if (currentSearchKeyword.isEmpty()) {
            return true;
        }

        return (moderator.getFirstName() != null && moderator.getFirstName().toLowerCase().contains(currentSearchKeyword.toLowerCase())) ||
                (moderator.getLastName() != null && moderator.getLastName().toLowerCase().contains(currentSearchKeyword.toLowerCase())) ||
                (moderator.getDomain() != null && moderator.getDomain().toLowerCase().contains(currentSearchKeyword.toLowerCase()));
    }

    private boolean matchesDateRangeCriteria(UserModel moderator) {
        // Parse the createdAt timestamp from the moderator object
        Date moderatorDate = parseTimestamp(moderator.getCreatedAt());

        if (moderatorDate == null) {
            return startDate == null && endDate == null; // If no date, only show when no date filter
        }

        // Check if date falls within the range
        boolean afterStartDate = startDate == null || !moderatorDate.before(startDate);
        boolean beforeEndDate = endDate == null || !moderatorDate.after(endDate);

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
            // Handle String format like "21 May 2026 at 12:45:00 UTC+5"
            else if (timestamp instanceof String) {
                String dateStr = (String) timestamp;
                // Try different date formats
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

    public void fetchModerators() {
        loading(true);
        moderatorController.getModerators(new DatabaseService.RealtimeCallback<List<UserModel>>() {
            @Override
            public void onDataChange(List<UserModel> data) {
                loading(false);
                moderators.clear();
                moderators.addAll(data);
                applyFilters(); // Apply any existing filters
            }

            @Override
            public void onFailure(String errorMessage) {
                loading(false);
                Toast.makeText(ModeratorsListActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdapter() {
        ModeratorAdapter adapter = new ModeratorAdapter(
                ModeratorsListActivity.this,
                R.layout.custom_list_layout,
                (ArrayList<UserModel>) filteredModerators
        );
        moderatorsList.setAdapter(adapter);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            loader.setVisibility(View.VISIBLE);
            moderatorsList.setVisibility(View.GONE);
        } else {
            loader.setVisibility(View.GONE);
            moderatorsList.setVisibility(View.VISIBLE);
        }
    }
}