package com.example.acdms_profile;


import android.os.Bundle;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class Courses extends AppCompatActivity{

    RecyclerView recyclerView;
    CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        ImageButton launchScheduleButton = findViewById(R.id.launchCoursesButton);
        recyclerView = findViewById(R.id.recycler_view);

        launchScheduleButton.setOnClickListener(v -> showCourseDialog());
        setupRecyclerView();


    }

    void showCourseDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CourseActivity scheduleDialog = new CourseActivity();
        scheduleDialog.show(fragmentManager, "CourseActivity");
    }

    void setupRecyclerView(){
        Query query = Utility.getCollectionReferenceForCourses();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        FirestoreRecyclerOptions<CourseModel> options = new FirestoreRecyclerOptions.Builder<CourseModel>().setQuery(query, CourseModel.class).build();
        recyclerView.setLayoutManager(layoutManager);

        courseAdapter = new CourseAdapter(options,this);
        recyclerView.setAdapter(courseAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        courseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        courseAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        courseAdapter.notifyDataSetChanged();
    }
}
