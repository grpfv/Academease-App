package com.example.acdms_profile;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Locale;

public class CourseActivity extends DialogFragment {

    EditText enterSubject, enterInstructor, addStartTime, addEndTime;
    private Spinner spinnerDay;
    TimePicker timePicker;

    String courseId, editInstructor, editschedSubject, editstartTime, editendTime;
    private int spinnerPosition;
    TextView instrucCourse;
    Button btnAdd, btnDelete, btnEdit;
    CardView cardInstructor, cardSubject, cardDay, cardTimein, cardTimeout;
    boolean editMode = false, isEditMode = false, isDeleteMode = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_course_activity, container, false);

        enterSubject = view.findViewById(R.id.enter_Subject);
        enterInstructor = view.findViewById(R.id.enter_Instructor);
        instrucCourse = view.findViewById(R.id.addCourse);

        spinnerDay = view.findViewById(R.id.spinnerDay);
        addStartTime = view.findViewById(R.id.addStartTime);
        addEndTime = view.findViewById(R.id.addEndTime);

        cardInstructor = view.findViewById(R.id.card_Instructor);
        cardSubject = view.findViewById(R.id.card_Subject);
        cardDay = view.findViewById(R.id.card_Day);
        cardTimein = view.findViewById(R.id.cardStartTime);
        cardTimeout = view.findViewById(R.id.cardEndTime);

        btnAdd = view.findViewById(R.id.btnAdd);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnEdit = view.findViewById(R.id.btnEdit);

        Bundle args = getArguments();
        if (args != null) {
            courseId = args.getString("courseId");


            editschedSubject = args.getString("subject");
            editInstructor = args.getString("instructor");
            spinnerPosition = args.getInt("spinnerPosition", 0);
            editstartTime = args.getString("startTime");
            editendTime = args.getString("endTime");
            isEditMode = args.getBoolean("editMode", false);
        }

        enterSubject.setText(editschedSubject);
        enterInstructor.setText(editInstructor);
        spinnerDay.setSelection(spinnerPosition);
        addStartTime.setText(editstartTime);
        addEndTime.setText(editendTime);

        if(courseId!=null && !courseId.isEmpty() && !isEditMode){
            isDeleteMode = true;
        }

        if(isDeleteMode){
            instrucCourse.setText("DELETE COURSE");
            btnAdd.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            cardSubject.setVisibility(View.GONE);
            cardInstructor.setVisibility(View.GONE);
            cardDay.setVisibility(View.GONE);
            cardTimein.setVisibility(View.GONE);
            cardTimeout.setVisibility(View.GONE);

            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v->deleteCourseToFirebase());
        }

        if(isEditMode){
            instrucCourse.setText("EDIT COURSE");
            btnAdd.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);

            btnEdit.setOnClickListener(v->addToCourses());
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCourses();
            }
        });

        addStartTime.setOnClickListener(view1 -> showTimePicker(addStartTime));

        // Set onClickListener for end time EditText
        addEndTime.setOnClickListener(view12 -> showTimePicker(addEndTime));

        return view;
    }


    void addToCourses() {
        String subject = enterSubject.getText().toString();
        String instructor = enterInstructor.getText().toString();

        String schedDay = spinnerDay.getSelectedItem().toString();
        String schedStartTime = addStartTime.getText().toString();
        String schedEndTime = addEndTime.getText().toString();


        if (subject.isEmpty()){
            Toast.makeText(requireContext(), "Please enter Subject", Toast.LENGTH_SHORT).show();
            enterSubject.setError("Subject Name is required");
            return;
        }
        if (instructor.isEmpty()){
            Toast.makeText(requireContext(), "Please enter Subject", Toast.LENGTH_SHORT).show();
            enterInstructor.setError("Instructor Name is required");
            return;
        }

        CourseModel course = new CourseModel();
        course.setSubject(subject);
        course.setInstructor(instructor);
        course.setStartTime(schedStartTime);
        course.setEndTime(schedEndTime);
        course.setSchedDay(schedDay);
        course.setTimestamp(Timestamp.now());

        saveCoursesToFirebase(course);
    }

    void saveCoursesToFirebase(CourseModel course){
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Utility.getCollectionReferenceForCourses().document(courseId);
        }else{
            documentReference = Utility.getCollectionReferenceForCourses().document();
        }
        documentReference.set(course).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(requireContext(),"Course Added",Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Toast.makeText(requireContext(),"Failed Adding Course",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void deleteCourseToFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForCourses().document(courseId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(requireContext(),"Course Deleted",Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Toast.makeText(requireContext(),"Failed Deleting Course",Toast.LENGTH_SHORT).show();
                }
            }
        });
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