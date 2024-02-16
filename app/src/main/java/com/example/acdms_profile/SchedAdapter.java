package com.example.acdms_profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SchedAdapter extends FirestoreRecyclerAdapter<SchedModel, SchedAdapter.ScheduleViewHolder> {

    Context context;

    public SchedAdapter(@NonNull FirestoreRecyclerOptions<SchedModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position, @NonNull SchedModel schedModel) {
        holder.scheduleSubject.setText(schedModel.schedSubject);
        holder.scheduleTime.setText(schedModel.subjectTime);

        holder.itemView.setOnLongClickListener(v -> {

            String schedSubject = schedModel.getSchedSubject();
            String schedDay = schedModel.getDay();
            String startTime = schedModel.getStartTime();
            String endTime = schedModel.getEndTime();
            // Create a new instance of the AddCourses fragment
            SchedActivity addSchedFragment = new SchedActivity();
            int spinnerPosition = getSpinnerPosition(schedDay);

            // Create a Bundle to pass arguments
            Bundle args = new Bundle();
            String schedId = this.getSnapshots().getSnapshot(position).getId();
            args.putString("schedId", schedId);
            args.putString("schedSubject", schedSubject);
            args.putInt("spinnerPosition", spinnerPosition);
            args.putString("startTime", startTime);
            args.putString("endTime", endTime);


            // Set the arguments to the fragment
            addSchedFragment.setArguments(args);

            // Show the fragment
            addSchedFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "ScheduleActivityFragment");

            return true;
        });


    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_schedule_item,parent,false);
        return new ScheduleViewHolder(view);
    }


    class ScheduleViewHolder extends RecyclerView.ViewHolder{
        TextView scheduleSubject, scheduleTime;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            scheduleSubject = itemView.findViewById(R.id.schedule_Subject);
            scheduleTime = itemView.findViewById(R.id.schedule_Time);

        }
    }

    private int getSpinnerPosition(String day) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(day)) {
                return i;
            }
        }

        return 0; // Default position if not found
    }
}

