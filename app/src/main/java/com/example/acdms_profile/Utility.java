package com.example.acdms_profile;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Utility {

    static CollectionReference getCollectionReferenceForSched(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("Schedule").document(currentUser.getUid()).collection("my_Schedule");

    }

    static CollectionReference getCollectionReferenceForCourses(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("Courses").document(currentUser.getUid()).collection("my_Courses");

    }

    static CollectionReference getCollectionReferenceForToDo(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("ToDoList").document(currentUser.getUid()).collection("my_ToDoList");

    }


    static CollectionReference getCollectionReferenceForAlbum(String courseId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance().collection("Courses")
                    .document(currentUser.getUid())
                    .collection("my_Courses")
                    .document(courseId)
                    .collection("Album");
        } else {

            return null;
        }
    }
    static CollectionReference getCollectionReferenceForNotes(String courseId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance().collection("Courses")
                    .document(currentUser.getUid())
                    .collection("my_Courses")
                    .document(courseId)
                    .collection("Notes");
        } else {

            return null;
        }
    }

    static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("MM/dd/yyyy").format(timestamp.toDate());
    }
}
