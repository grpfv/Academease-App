package com.example.acdms_profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class CourseActivity extends DialogFragment {

    EditText enterSubject, enterInstructor;
    String courseId;
    TextView instrucCourse;
    Button btnAdd, btnDelete;
    CardView cardInstructor, cardSubject;
    boolean isDeleteMode = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_course_activity, container, false);

        enterSubject = view.findViewById(R.id.enter_Subject);
        enterInstructor = view.findViewById(R.id.enter_Instructor);
        instrucCourse = view.findViewById(R.id.addCourse);
        cardInstructor = view.findViewById(R.id.card_Instructor);
        cardSubject = view.findViewById(R.id.card_Subject);

        btnAdd = view.findViewById(R.id.btnAdd);
        btnDelete = view.findViewById(R.id.btnDelete);

        Bundle args = getArguments();
        if (args != null) {
            courseId = args.getString("courseId");
        }

        if(courseId!=null && !courseId.isEmpty()){
            isDeleteMode = true;
        }

        if(isDeleteMode){
            instrucCourse.setText("DETELE COURSE?");
            btnAdd.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v->deleteCourseToFirebase());
            cardSubject.setVisibility(View.GONE);
            cardInstructor.setVisibility(View.GONE);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCourses();
            }
        });

        return view;
    }


    void addToCourses() {
        String subject = enterSubject.getText().toString();
        String instructor = enterInstructor.getText().toString();

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
        course.setTimestamp(Timestamp.now());

        saveCoursesToFirebase(course);
    }

    void saveCoursesToFirebase(CourseModel course){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForCourses().document();

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



}