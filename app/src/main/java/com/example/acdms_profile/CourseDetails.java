package com.example.acdms_profile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CourseDetails extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Album"));
        tabLayout.addTab(tabLayout.newTab().setText("Notes"));
        tabLayout.addTab(tabLayout.newTab().setText("Files"));


        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager.setAdapter(adapter);

        String subject = getIntent().getStringExtra("subject");
        String instructor = getIntent().getStringExtra("instructor");
        String courseId = getIntent().getStringExtra("courseId");

        // Update UI with the retrieved data
        TextView subjectTextView = findViewById(R.id.subject);
        TextView instructorTextView = findViewById(R.id.Instructor);

        subjectTextView.setText(" " + subject);
        instructorTextView.setText(" " + instructor);


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