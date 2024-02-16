package com.example.acdms_profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FullImage extends AppCompatActivity {
    private ImageView fullImageView;
    Button deleteIcon;
    final private CollectionReference databaseReference = FirebaseFirestore.getInstance().collection("Album");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        fullImageView = findViewById(R.id.fullImageView);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(this).load(imageUrl).into(fullImageView);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(imageUrl); // Show confirmation dialog before deleting
            }
        });
    }

    private void showDeleteConfirmationDialog(String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImage(imageUrl);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage(String imageUrl) {
        databaseReference.document(imageUrl).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Pass the deleted image identifier back to the Tab_album fragment
                        Intent intent = new Intent();
                        intent.putExtra("deletedImageUrl", imageUrl);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FullImage.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
