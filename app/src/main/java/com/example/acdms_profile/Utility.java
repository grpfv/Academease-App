package com.example.acdms_profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
}
