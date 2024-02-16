package com.example.acdms_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CourseDetails extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentAdapter adapter;
    Button btnEdit;
    TextView addSubject, addStartTime, addEndTime, addInstructor, addscheDay;
    int spinnerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        btnEdit = findViewById(R.id.editButton);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        addSubject = findViewById(R.id.subject);
        addStartTime = findViewById(R.id.addStartTime);
        addEndTime = findViewById(R.id.addEndTime);
        addInstructor = findViewById(R.id.addInstructor);
        addscheDay = findViewById(R.id.addschedDay);

        tabLayout.addTab(tabLayout.newTab().setText("Album"));
        tabLayout.addTab(tabLayout.newTab().setText("Notes"));
        tabLayout.addTab(tabLayout.newTab().setText("Files"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager.setAdapter(adapter);

        String subject = getIntent().getStringExtra("subject");
        String instructor = getIntent().getStringExtra("instructor");
        String schedDay = getIntent().getStringExtra("schedDay");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        String courseId = getIntent().getStringExtra("courseId");

        // Update UI with the retrieved data
        TextView subjectTextView = findViewById(R.id.subject);
        TextView instructorTextView = findViewById(R.id.addInstructor);
        TextView startTimeTextView = findViewById(R.id.addStartTime);
        TextView endTimeTextView = findViewById(R.id.addEndTime);
        TextView addschedDayTextView = findViewById(R.id.addschedDay);

        subjectTextView.setText("" + subject);
        instructorTextView.setText("" + instructor);
        startTimeTextView.setText(startTime+" -");
        endTimeTextView.setText( endTime);
        addschedDayTextView.setText(schedDay+" |");

        btnEdit.setOnClickListener(v -> {
            CourseActivity editCoursesFragment = new CourseActivity();

            Bundle args = new Bundle();
            args.putString("subject", subject);
            args.putString("instructor", instructor);

            int spinnerPosition = getSpinnerPosition(schedDay);;
            args.putInt("spinnerPosition", spinnerPosition);
            args.putString("startTime", startTime);
            args.putString("endTime", endTime);
            args.putString("courseId", courseId);

            boolean editMode = true;
            args.putBoolean("editMode", editMode);

            editCoursesFragment.setArguments(args);

            // Show the fragment
            editCoursesFragment.show(((AppCompatActivity) this).getSupportFragmentManager(), "EditCoursesFragment");

        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Albums");
                    break;
                case 1:
                    tab.setText("Notes");
                    break;
                case 2:
                    tab.setText("Files");
                    break;
            }
        }).attach();
    }


    private int getSpinnerPosition(String day) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CourseDetails.this, R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(day)) {
                return i;
            }
        }

        return 0; // Default position if not found
    }
}