package com.example.acdms_profile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Calendar;
import java.util.TimeZone;

public class ToDoAdapter extends FirestoreRecyclerAdapter<ToDoModel, ToDoAdapter.ToDoViewHolder> {

    Context context;

    public ToDoAdapter(@NonNull FirestoreRecyclerOptions<ToDoModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ToDoViewHolder holder, int position, @NonNull ToDoModel todoModel) {
        holder.taskTextView.setText(todoModel.title);
        String formattedDueDate = formatDueDate(todoModel);
        holder.dueDateTextView.setText(formattedDueDate);

        if (todoModel.subject != null && !todoModel.subject.isEmpty()) {
            holder.subjectTextView.setVisibility(View.VISIBLE);
            holder.subjectTextView.setText(todoModel.subject);
        } else {
            holder.subjectTextView.setVisibility(View.GONE);
        }



        holder.checkbox.setOnCheckedChangeListener(null);// Remove previous listener to avoid conflicts
        holder.checkbox.setChecked(todoModel.isChecked());

        // Handle checkbox state changes
        holder.checkbox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            // Update the ToDoModel with the new checkbox state
            todoModel.setChecked(isChecked);

            // Update the Firestore document with the new checkbox state
            updateCheckboxState(position, isChecked);

            if (isChecked) {
                cancelAlarmForToDo(position);
            } else {
                // If the to do is unchecked, set up the alarm for this to do
                setupAlarmForToDo(todoModel, position);
            }
        });



        if (!todoModel.isChecked()) {
            setupAlarmForToDo(todoModel, position);
        }

        holder.itemView.setOnLongClickListener(v -> {

            String title = todoModel.getTitle();
            String subject = todoModel.getSubject();
            String dueDay = todoModel.getDueDay();
            String dueTime = todoModel.getDueTime();
            String remindDay = todoModel.getRemindDay();
            String remindTime = todoModel.getRemindTime();

            ToDoActivity addToDoFragment = new ToDoActivity();

            // Create a Bundle to pass arguments
            Bundle args = new Bundle();
            String todoId = this.getSnapshots().getSnapshot(position).getId();
            args.putString("todoId", todoId);
            args.putString("title", title);
            args.putString("subject", subject);
            args.putString("dueDay", dueDay);
            args.putString("dueTime", dueTime);
            args.putString("remindDay", remindDay);
            args.putString("remindTime", remindTime);

            args.putInt("position", position);


            // Set the arguments to the fragment
            addToDoFragment.setArguments(args);

            // Show the fragment
            addToDoFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "AddNewToDoFragment");

            return true;
        });
    }

    private void updateCheckboxState(int position, boolean isChecked) {
        getSnapshots().getSnapshot(position).getReference().update("checked", isChecked)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Checkbox state updated successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating checkbox state", e));
    }

    private void setupAlarmForToDo(ToDoModel todomodel, int position) {
        // Get Remind date and time components
        String[] dateComponents = todomodel.getRemindDay().split("/");
        int year = Integer.parseInt(dateComponents[2]);
        int month = Integer.parseInt(dateComponents[0]); // Month is 0-based
        int day = Integer.parseInt(dateComponents[1]);

        String[] timeAndAmPm = todomodel.getRemindTime().split(" ");
        String time = timeAndAmPm[0];

        String[] timeComponents = time.split(":");
        int hour = Integer.parseInt(timeComponents[0]);
        int minute = Integer.parseInt(timeComponents[1]);

        if (timeAndAmPm[1].equalsIgnoreCase("PM") && hour != 12) {
            hour += 12;
        } else if (timeAndAmPm[1].equalsIgnoreCase("AM") && hour == 12) {
            hour = 0;
        }

        // Set up AlarmManager to trigger the AlarmReceiver at the specified Remind date and time
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, alarmIntent, PendingIntent.FLAG_MUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Month is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Log.d("AlarmTime", "Calculated alarm time: " + calendar.getTimeInMillis() + " (" + calendar.getTimeZone().getID() + ")");
        Log.d("SystemTime", "alarm Current system time: " + System.currentTimeMillis() + " (" + TimeZone.getDefault().getID() + ")");

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            // The Remind date and time have already passed; no need to set the alarm
            Log.d("AlarmSkipped", "Skipped setting alarm for todo at position " + position);
            return;
        }

        long alarmTime = calendar.getTimeInMillis();
        Log.d("AlarmTime", "Calculated alarm time: " + alarmTime);

        // Set a one-time alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    private void cancelAlarmForToDo(int position) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, alarmIntent, PendingIntent.FLAG_MUTABLE);

        // Cancel the alarm for the specified PendingIntent
        alarmManager.cancel(pendingIntent);
    }

    private String formatDueDate(ToDoModel todoModel) {
        String dueDay = todoModel.dueDay;
        String dueTime = todoModel.dueTime; // Assuming you have a field called dueTime in your ToDoModel
        return String.format("%s | %s", dueDay, dueTime);

    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_todo_item, parent,false);
        return new ToDoViewHolder(view);
    }



    class ToDoViewHolder extends RecyclerView.ViewHolder{
        MaterialCheckBox checkbox;
        TextView taskTextView, dueDateTextView, subjectTextView;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.taskTextView);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
            checkbox = itemView.findViewById(R.id.checkbox);
            subjectTextView = itemView.findViewById(R.id.subjectTextView);
        }
    }
}

