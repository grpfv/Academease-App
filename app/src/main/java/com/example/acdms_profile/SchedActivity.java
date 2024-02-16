package com.example.acdms_profile;

//// Schedule Activity
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.ViewGroup;

import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class SchedActivity extends DialogFragment {

    private EditText addSubject, addStartTime, addEndTime;
    private Spinner spinnerDay;
    TimePicker timePicker;
    Button btnAdd, btnDelete, btnEdit;
    String schedId, editschedSubject, editstartTime, editendTime;
    private int spinnerPosition;
    TextView editSched;
    Boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sched, container, false);

        addSubject = view.findViewById(R.id.addSubject);
        spinnerDay = view.findViewById(R.id.spinnerDay);
        addStartTime = view.findViewById(R.id.addStartTime);
        addEndTime = view.findViewById(R.id.addEndTime);
        timePicker = view.findViewById(R.id.timePicker);
        editSched = view.findViewById(R.id.addSchedule);

        btnAdd = view.findViewById(R.id.btnAdd);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnEdit = view.findViewById(R.id.btnEdit);

        // Set onClickListener for start time EditText
        addStartTime.setOnClickListener(view1 -> showTimePicker(addStartTime));

        // Set onClickListener for end time EditText
        addEndTime.setOnClickListener(view12 -> showTimePicker(addEndTime));



        Bundle args = getArguments();
        if (args != null) {
            schedId = args.getString("schedId");
            editschedSubject = args.getString("schedSubject");
            spinnerPosition = args.getInt("spinnerPosition", 0);
            editstartTime = args.getString("startTime");
            editendTime = args.getString("endTime");
        }

        if(schedId!=null && !schedId.isEmpty()){
            isEditMode = true;
        }

        addSubject.setText(editschedSubject);
        addStartTime.setText(editstartTime);
        addEndTime.setText(editendTime);
        spinnerDay.setSelection(spinnerPosition);

        if(isEditMode){
            editSched.setText("EDIT SCHEDULE");

            btnAdd.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);


            btnDelete.setVisibility(View.VISIBLE);

        }

        btnAdd.setOnClickListener(v-> saveSchedule());
        btnEdit.setOnClickListener(v->saveSchedule());
        btnDelete.setOnClickListener(v->showDeleteConfirmationDialog());

        return view;
    }


    void saveSchedule(){
        String schedSubject = addSubject.getText().toString();
        String schedDay = spinnerDay.getSelectedItem().toString();
        String schedStartTime = addStartTime.getText().toString();
        String schedEndTime = addEndTime.getText().toString();

        if (schedSubject.isEmpty()){
            Toast.makeText(requireContext(), "Please enter Subject", Toast.LENGTH_SHORT).show();
            addSubject.setError("Subject Name is required");
            return;
        }
        if (schedStartTime.isEmpty()){
            Toast.makeText(requireContext(), "Please enter Start Time", Toast.LENGTH_SHORT).show();
            addStartTime.setError("Start Time is required");
            return;
        }
        if (schedEndTime.isEmpty()){
            Toast.makeText(requireContext(), "Please enter End Time", Toast.LENGTH_SHORT).show();
            addEndTime.setError("End Time is required");
            return;
        }

        SchedModel schedule = new SchedModel();
        schedule.setSchedSubject(schedSubject);
        schedule.setDay(schedDay);
        schedule.setSubjectTime(schedStartTime + " - "  +schedEndTime);


        saveScheduletoFirebase(schedule);
    }

    void saveScheduletoFirebase(SchedModel schedModel){
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Utility.getCollectionReferenceForSched().document(schedId);
        }else{
            documentReference = Utility.getCollectionReferenceForSched().document();
        }


        documentReference.set(schedModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(requireContext(),"Schedule Added",Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Toast.makeText(requireContext(),"Failed Adding Schedule",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void deleteScheduleFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForSched().document(schedId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(requireContext(),"Schedule Deleted",Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Toast.makeText(requireContext(),"Failed Deleting Schedule",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this schedule?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteScheduleFromFirebase();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void showTimePicker(final EditText editText) {
        // Inflate the custom layout for the time picker dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);

        // Create a dialog and set its content view
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(dialogView);

        // Get references to TimePicker and Button in the dialog
        final TimePicker dialogTimePicker = dialogView.findViewById(R.id.dialogTimePicker);
        Button btnSetTime = dialogView.findViewById(R.id.btnSetTime);

        // Set up the TimePicker
        dialogTimePicker.setIs24HourView(false);

        // Set a listener to handle the "Set Time" button click
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = dialogTimePicker.getHour();
                int minute = dialogTimePicker.getMinute();

                // Format the selected time in 12-hour format with AM/PM
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                        (hour == 0 || hour == 12) ? 12 : hour % 12, minute, (hour < 12) ? "AM" : "PM");

                // Set the selected time to the EditText
                editText.setText(selectedTime);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }
}
