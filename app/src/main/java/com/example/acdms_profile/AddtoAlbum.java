package com.example.acdms_profile;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddtoAlbum extends AppCompatActivity {

    private FloatingActionButton uploadButton;
    private ImageView uploadImage;
    EditText uploadCaption;
    ProgressBar progressBar;
    private Uri imageUri;
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_album);
        courseId = getIntent().getStringExtra("courseId");

        uploadButton = findViewById(R.id.uploadButton);
        uploadCaption = findViewById(R.id.enter_Caption);
        uploadImage = findViewById(R.id.uploadImage);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            uploadImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(AddtoAlbum.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null){
                    uploadToFirebase(imageUri);
                } else  {
                    Toast.makeText(AddtoAlbum.this, "Please select image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Outside onCreate
    private void uploadToFirebase(Uri uri) {
        String caption = uploadCaption.getText().toString();
        final StorageReference imageReference = storageReference.child("Album" + System.currentTimeMillis() + "." + getFileExtension(uri));
        final CollectionReference forAlbum = Utility.getCollectionReferenceForAlbum(courseId); //test lang

        imageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    imageReference.getDownloadUrl().addOnSuccessListener(uriResult -> {
                        DataClass dataClass = new DataClass(uriResult.toString(), caption);

                        forAlbum.add(dataClass)
                                .addOnSuccessListener(CollectionReference -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(AddtoAlbum.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    //Intent intent = new Intent(AddtoAlbum.this, CourseDetails.class);
                                    //startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(AddtoAlbum.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                });
                    });
                }).addOnProgressListener(taskSnapshot -> progressBar.setVisibility(View.VISIBLE))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddtoAlbum.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}