package com.example.acdms_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ToDoList extends AppCompatActivity {

    RecyclerView recyclerView;
    ToDoAdapter taskAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        recyclerView = findViewById(R.id.todorecyclerview);
        ImageButton addTaskButton = findViewById(R.id.addTaskButton);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ToDoList.this));

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                showScheduleDialog();
            }
        });

        setupRecyclerView();
    }

    private void showScheduleDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ToDoActivity scheduleDialog = new ToDoActivity();
        scheduleDialog.show(fragmentManager, "ToDoActivity");
    }

    void setupRecyclerView(){
        Query query = Utility.getCollectionReferenceForToDo().orderBy("dueDateTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ToDoModel> options = new FirestoreRecyclerOptions.Builder<ToDoModel>().setQuery(query, ToDoModel.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new ToDoAdapter(options, this);
        recyclerView.setAdapter(taskAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        taskAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        taskAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskAdapter.notifyDataSetChanged();
    }
}