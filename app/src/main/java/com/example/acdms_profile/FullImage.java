package com.example.acdms_profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FullImage extends AppCompatActivity {
    private ImageView fullImageView;
    private Uri imageUri;

    Button deleteIcon;
    final private CollectionReference databaseReference = FirebaseFirestore.getInstance().collection("Album");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        fullImageView = findViewById(R.id.fullImageView);
        deleteIcon = findViewById(R.id.deletepngbutton);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(this).load(imageUrl).into(fullImageView);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    deleteFromFirebase(imageUri.toString());
                    // Clear the image view and any other related data after deletion if necessary
                    fullImageView.setImageResource(android.R.color.transparent); // Clear the image
                    imageUri = null; // Clear the image URI
                } else {
                    Toast.makeText(FullImage.this, "No image to delete", Toast.LENGTH_SHORT).show();
                }
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

    private void deleteFromFirebase(String imageUrl) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        imageRef.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            Toast.makeText(FullImage.this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // Uh-oh, an error occurred!
            Toast.makeText(FullImage.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
        });
    }

}
