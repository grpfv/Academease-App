package com.example.acdms_profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class CourseDetailsActivity extends AppCompatActivity {
    private EditText addSubject, addStartTime, addEndTime, addInstructor;
    private Spinner spinnerDay;
    TimePicker timePicker;
    Button btnAdd, btnDelete, btnEdit;
    String schedId, editschedSubject, editstartTime, editendTime;
    private int spinnerPosition;
    TextView editSched;
    Boolean isEditMode = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details2);

        addSubject = findViewById(R.id.addSubject);
        spinnerDay = findViewById(R.id.spinnerDay);
        addStartTime = findViewById(R.id.addStartTime);
        addEndTime = findViewById(R.id.addEndTime);
        timePicker = findViewById(R.id.timePicker);
        editSched = findViewById(R.id.addSchedule);
        addInstructor = findViewById(R.id.addInstructor);

        btnAdd = findViewById(R.id.btnAdd);

        addStartTime.setOnClickListener(view -> showTimePicker(addStartTime));
        addEndTime.setOnClickListener(view -> showTimePicker(addEndTime));

        Bundle args = getIntent().getExtras();
        if (args != null) {
            schedId = args.getString("schedId");
            editschedSubject = args.getString("schedSubject");
            spinnerPosition = args.getInt("spinnerPosition", 0);
            editstartTime = args.getString("startTime");
            editendTime = args.getString("endTime");
            addInstructor.setText(args.getString("instructor"));

        }

        if (schedId != null && !schedId.isEmpty()) {
            isEditMode = true;
        }

        addSubject.setText(editschedSubject);
        addStartTime.setText(editstartTime);
        addEndTime.setText(editendTime);
        addInstructor.setText(args.getString("instructor"));
        spinnerDay.setSelection(spinnerPosition);

        if (isEditMode) {
            editSched.setText("EDIT SCHEDULE");
            btnAdd.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnAdd.setOnClickListener(v-> saveSchedule());

    }

    void saveSchedule() {
        String schedSubject = addSubject.getText().toString();
        String schedDay = spinnerDay.getSelectedItem().toString();
        String schedStartTime = addStartTime.getText().toString();
        String schedEndTime = addEndTime.getText().toString();
        String instructor = addInstructor.getText().toString();

        if (schedSubject.isEmpty() || schedStartTime.isEmpty() || schedEndTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SchedModel schedule = new SchedModel(schedSubject, schedDay, schedStartTime + " - " + schedEndTime, instructor);

        saveScheduletoFirebase(schedule);
    }

    void saveScheduletoFirebase(SchedModel schedModel) {
        DocumentReference documentReference;
        if (isEditMode) {
            documentReference = FirebaseFirestore.getInstance().collection("Schedule").document(schedId);
        } else {
            documentReference = FirebaseFirestore.getInstance().collection("Schedule").document();
        }

        documentReference.set(schedModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Schedule Added", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, CourseDetails.class);
                intent.putExtra("subject", schedModel.getSchedSubject());
                intent.putExtra("instructor", schedModel.getInstructor());
                intent.putExtra("startTime", schedModel.getStartTime());
                intent.putExtra("endTime", schedModel.getEndTime());
                intent.putExtra("spinnerPosition", getSpinnerPositionForDay(schedModel.getDay()));
                startActivity(intent);

                finish();
            } else {
                Toast.makeText(this, "Failed Adding Schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getSpinnerPositionForDay(String day) {
        // Implement your logic to map the day to the spinner position here
        // For example:
        if (day.equals("Monday")) {
            return 0;
        } else if (day.equals("Tuesday")) {
            return 1;
        } else if (day.equals("Wednesday")) {
            return 2;
        }
        // Add more cases as needed
        return 0; // Default to position 0 if day is not found
    }

    private void showTimePicker(final EditText editText) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Time")
                .setView(dialogView)
                .setPositiveButton("Set Time", null)
                .setNegativeButton("Cancel", null)
                .create();

        final TimePicker dialogTimePicker = dialogView.findViewById(R.id.dialogTimePicker);
        dialogTimePicker.setIs24HourView(false);

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                int hour = dialogTimePicker.getCurrentHour();
                int minute = dialogTimePicker.getCurrentMinute();
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                        (hour == 0 || hour == 12) ? 12 : hour % 12, minute, (hour < 12) ? "AM" : "PM");
                editText.setText(selectedTime);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

}