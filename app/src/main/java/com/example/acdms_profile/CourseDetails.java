package com.example.acdms_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    TextView addSubject, addStartTime, addEndTime, addInstructor;
    String editschedSubject, editstartTime, editendTime;
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

        tabLayout.addTab(tabLayout.newTab().setText("Album"));
        tabLayout.addTab(tabLayout.newTab().setText("Notes"));
        tabLayout.addTab(tabLayout.newTab().setText("Files"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager.setAdapter(adapter);

        String subject = getIntent().getStringExtra("subject");
        String instructor = getIntent().getStringExtra("instructor");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");

        // Update UI with the retrieved data
        TextView subjectTextView = findViewById(R.id.subject);
        TextView instructorTextView = findViewById(R.id.addInstructor);
        TextView startTimeTextView = findViewById(R.id.addStartTime);
        TextView endTimeTextView = findViewById(R.id.addEndTime);

        subjectTextView.setText("" + subject);
        instructorTextView.setText("" + instructor);
        startTimeTextView.setText("TIME IN: " + startTime);
        endTimeTextView.setText("TIME OUT: " + endTime);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, CourseDetailsActivity.class);
            intent.putExtra("subject", addSubject.getText().toString());
            intent.putExtra("startTime", addStartTime.getText().toString());
            intent.putExtra("endTime", addEndTime.getText().toString());
            intent.putExtra("instructor", addInstructor.getText().toString());

            startActivity(intent);
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
}