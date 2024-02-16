package com.example.acdms_profile;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;


public class Tab_album extends Fragment {
    private FloatingActionButton fab;
    private GridView gridView;
    private ArrayList<DataClass> dataList;
    private AlbumAdapter adapter;


    private String courseId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseId = getCourseId(requireContext()); // Retrieve courseId
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_album, container, false);


        fab = view.findViewById(R.id.fab);
        gridView = view.findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        adapter = new AlbumAdapter(requireContext(), dataList);
        gridView.setAdapter(adapter);


        // Set long click listener for item deletion
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(dataList.get(position));
                return true;
            }
        });


        // Fetch data from Firestore
        fetchDataFromFirestore();


        // Set click listener for fab to add new items
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), AddtoAlbum.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });


        return view;
    }


    private void fetchDataFromFirestore() {
        CollectionReference firestoreReference = Utility.getCollectionReferenceForAlbum(courseId);
        firestoreReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Tab_album", "Error fetching images", e);
                    return;
                }


                dataList.clear(); // Clear the existing data
                for (QueryDocumentSnapshot document : querySnapshot) {
                    DataClass dataClass = document.toObject(DataClass.class);
                    dataClass.setId(document.getId());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }
        });
    }


    private void showDeleteConfirmationDialog(final DataClass itemToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this item?");


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItemFromFirestore(itemToDelete);
            }
        });


        builder.setNegativeButton("No", null);
        builder.show();
    }


    private void deleteItemFromFirestore(DataClass itemToDelete) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Album");


        // Get the document ID (unique identifier)
        String documentId = itemToDelete.getId();


        // Delete document from Firestore
        collectionReference.document(documentId).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Tab_album", "DocumentSnapshot successfully deleted from Firestore.");
                        // Notify the adapter that the data set has changed
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("Tab_album", "Error deleting document from Firestore", task.getException());
                    }
                });
    }




    private String getCourseId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("courseId", "");
    }
}





