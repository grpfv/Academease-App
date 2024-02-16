package com.example.acdms_profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Schedule extends AppCompatActivity {

    private Button sunButton,monButton, tueButton, wedButton, thuButton, friButton, satButton;
    SchedAdapter scheduleAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        ImageButton launchScheduleButton = findViewById(R.id.launchScheduleButton);
        recyclerView = findViewById(R.id.sched_recycler_view);

        launchScheduleButton.setOnClickListener(v -> showScheduleDialog());{

        }

        // Add space between items in RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing); // Define spacing dimension in resources
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        // Get references to TextView elements
        TextView dateTextView = findViewById(R.id.dateText);
        TextView dayTextView = findViewById(R.id.dayText);
        TextView timeTextView = findViewById(R.id.timeText);

        // Get current date, day, and time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String currentDay = dayFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());

        // Set the text of TextView elements with current date, day, and time
        dateTextView.setText(currentDate);
        dayTextView.setText(currentDay);
        timeTextView.setText(currentTime);



        // Initialize day buttons
        sunButton = findViewById(R.id.sun_btn);
        monButton = findViewById(R.id.mon_btn);
        tueButton = findViewById(R.id.tue_btn);
        wedButton = findViewById(R.id.wed_btn);
        thuButton = findViewById(R.id.thu_btn);
        friButton = findViewById(R.id.fri_btn);
        satButton = findViewById(R.id.sat_btn);


        // Set click listeners for each day button
        sunButton.setOnClickListener(view -> onDayButtonClick(sunButton));
        monButton.setOnClickListener(view -> onDayButtonClick(monButton));

        tueButton.setOnClickListener(view -> onDayButtonClick(tueButton));
        wedButton.setOnClickListener(view -> onDayButtonClick(wedButton));
        thuButton.setOnClickListener(view -> onDayButtonClick(thuButton));
        friButton.setOnClickListener(view -> onDayButtonClick(friButton));
        satButton.setOnClickListener(view -> onDayButtonClick(satButton));

        onDayButtonClick(sunButton);
    }

    private void setupRecyclerView(String selectedDay) {
        Query query = Utility.getCollectionReferenceForSched().whereEqualTo("day", selectedDay);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        FirestoreRecyclerOptions<SchedModel> options = new FirestoreRecyclerOptions.Builder<SchedModel>()
                .setQuery(query, SchedModel.class).build();

        recyclerView.setLayoutManager(layoutManager);

        scheduleAdapter = new SchedAdapter(options, this);
        recyclerView.setAdapter(scheduleAdapter);
        scheduleAdapter.startListening();
    }


    private void onDayButtonClick(Button selectedButton) {
        // Reset background color for all day buttons
        resetButtonColors();

        selectedButton.setTextColor(ContextCompat.getColor(this, R.color.clicked_text_color));
        // Set background color for the selected day button
        selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.selected_button_color));

        setupRecyclerView(selectedButton.getText().toString());
    }


    @Override
    protected void onStart() {
        super.onStart();
        scheduleAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scheduleAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduleAdapter.notifyDataSetChanged();
    }

    private void showScheduleDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SchedActivity scheduleDialog = new SchedActivity();
        scheduleDialog.show(fragmentManager, "ScheduleActivity");
    }

    private void resetButtonColors() {
        // List of day buttons
        List<Button> dayButtons = Arrays.asList(sunButton, monButton, tueButton, wedButton, thuButton, friButton, satButton);

        for (Button button : dayButtons) {
            // Reset background color for all day buttons
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

            // Reset text color for all day buttons
            button.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }

}

