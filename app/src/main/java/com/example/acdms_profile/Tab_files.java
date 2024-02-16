package com.example.acdms_profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Tab_files extends Fragment {
    ListView listView;
    WebView webView;
    FloatingActionButton floatingActionButton;

    List<pdfClass> uploads;
    String courseId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseId = getCourseId(requireContext()); // Retrieve courseId
    }

    private String getCourseId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("courseId", "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_files, container, false);
        floatingActionButton = view.findViewById(R.id.float_btn);

        listView = view.findViewById(R.id.listview);
        webView = view.findViewById(R.id.Webview);

        uploads = new ArrayList<>();

        viewAllFiles();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pdfClass pdfUpload = uploads.get(i);
                String url = pdfUpload.getUrl();

                loadPDF(url);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position);
                return true;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AddtoFiles.class);
                intent.putExtra("courseId", courseId); // Pass courseId to AddtoFiles activity
                startActivity(intent);
            }
        });

        return view;
    }

    private void viewAllFiles() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Courses")
                .document(currentUser.getUid())
                .collection("my_Courses")
                .document(courseId)
                .collection("Files");

        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploads.clear(); // Clear the list before adding new data
                for (DocumentSnapshot document : task.getResult()) {
                    pdfClass pdf = document.toObject(pdfClass.class);
                    if (pdf != null) {
                        uploads.add(pdf);
                    }
                }

                if (uploads.isEmpty()) {
                    // Show a message indicating no files found
                    Toast.makeText(requireContext(), "No files found", Toast.LENGTH_SHORT).show();
                } else {
                    // Update the list view with the retrieved files
                    updateListView();
                }
            } else {
                // Show a message indicating failure to retrieve files
                Toast.makeText(requireContext(), "Failed to retrieve files", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateListView() {
        String[] fileNames = new String[uploads.size()];
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = uploads.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);
    }

    /* private void loadPDF(String url) {
         webView.getSettings().setJavaScriptEnabled(true);
         webView.setVisibility(View.VISIBLE);
         webView.setWebViewClient(new WebViewClient());
         webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);
     }*/
    private void loadPDF(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome"); // Specify the package name of Google Chrome

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Google Chrome is not installed, open with default browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }


    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete File");
        builder.setMessage("Are you sure you want to delete this file?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFile(position);
            }
        });

        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void deleteFile(int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Courses")
                .document(currentUser.getUid())
                .collection("my_Courses")
                .document(courseId)
                .collection("Files");

        collectionReference.document(uploads.get(position).getUrl()).delete()
                .addOnSuccessListener(aVoid -> {
                    // File deleted successfully
                    uploads.remove(position);
                    updateListView();
                    Toast.makeText(requireContext(), "File deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to delete file
                    Toast.makeText(requireContext(), "Failed to delete file", Toast.LENGTH_SHORT).show();
                });
    }

}