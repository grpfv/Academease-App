package com.example.acdms_profile;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CourseAdapter extends FirestoreRecyclerAdapter<CourseModel, CourseAdapter.CourseViewHolder> {

    Context context;

    public CourseAdapter(@NonNull FirestoreRecyclerOptions<CourseModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CourseViewHolder holder, int position, @NonNull CourseModel course) {
        holder.courseSubject.setText(course.subject);

        holder.itemView.setOnLongClickListener(v -> {
            // Create a new instance of the AddCourses fragment
            CourseActivity addCoursesFragment = new CourseActivity();

            // Create a Bundle to pass arguments
            Bundle args = new Bundle();
            String courseId = this.getSnapshots().getSnapshot(position).getId();
            args.putString("courseId", courseId);

            // Set the arguments to the fragment
            addCoursesFragment.setArguments(args);

            // Show the fragment
            addCoursesFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "AddCoursesFragment");

            return true;
        });

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, CourseDetails.class);
            intent.putExtra("subject",course.subject);
            intent.putExtra("instructor", course.instructor);
            intent.putExtra("endTime", course.endTime);
            intent.putExtra("startTime", course.startTime);
            intent.putExtra("schedDay", course.schedDay);


            String courseId = this.getSnapshots().getSnapshot(position).getId();
            saveCourseId(context, courseId); // Save courseId
            context.startActivity(intent);
        });
    }
    private void saveCourseId(Context context, String courseId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("courseId", courseId);
        editor.apply();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_course_item, parent, false);
        return new CourseViewHolder(view);
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{

        TextView courseSubject;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            courseSubject = itemView.findViewById(R.id.course_Subject);
        }
    }
}
