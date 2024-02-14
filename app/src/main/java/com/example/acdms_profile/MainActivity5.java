package com.example.acdms_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity5 extends AppCompatActivity {

    private EditText yearEditText;
    private TextView editTextView;
    private EditText schlnameEditText;
    private TextView editsecondTextView;
    private FirebaseHelper databaseHelper;
    private Uri selectedImageUri;
    private ProgressBar progressBar;
    private ImageView profileImageView;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        yearEditText = findViewById(R.id.yearlvl);
        editTextView = findViewById(R.id.yrlvledit);
        schlnameEditText = findViewById(R.id.boxschoolname);
        editsecondTextView = findViewById(R.id.schoolnameedit);
        progressBar = findViewById(R.id.progressBar);
        profileImageView = findViewById(R.id.profile);

        // Find the TextView
        TextView changePhotoTextView = findViewById(R.id.change_photo);

        // Create a SpannableString to apply the underline
        SpannableString content = new SpannableString(changePhotoTextView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the modified SpannableString to the TextView
        changePhotoTextView.setText(content);

        // Set OnClickListener for the modified TextView
        changePhotoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });


        // Initialize DatabaseHelper
        databaseHelper = new FirebaseHelper();

        // Fetch user data and set EditText values
        fetchAndSetUserData();

        // Set OnClickListener for the TextView
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between Edit and Save
                if (editTextView.getText().toString().equals("Edit")) {
                    editTextView.setText("Save");
                    yearEditText.setFocusableInTouchMode(true); // Make EditText editable
                } else {
                    editTextView.setText("Edit");
                    yearEditText.setFocusable(false); // Make EditText not editable

                    // Save changes to the database here
                    saveChangesToDatabase(yearEditText.getText().toString());
                }
            }
        });

        // Set OnClickListener for the TextView
        editsecondTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between Edit and Save
                if (editsecondTextView.getText().toString().equals("Edit")) {
                    editsecondTextView.setText("Save");
                    schlnameEditText.setFocusableInTouchMode(true); // Make EditText editable
                } else {
                    editsecondTextView.setText("Edit");
                    schlnameEditText.setFocusable(false); // Make EditText not editable

                    // Save changes to the database here
                    saveChangesToDatabase2(schlnameEditText.getText().toString());
                }
            }
        });

        // Set OnClickListener for the "SAVE" button
        findViewById(R.id.savebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save profile picture changes
                if (selectedImageUri != null) {
                    saveProfilePictureChanges(selectedImageUri);
                } else {
                    Toast.makeText(MainActivity5.this, "Please select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Set user profile image
        setProfileImage();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Display the selected image in the ImageView immediately
            setCircularImage(selectedImageUri);
        }
    }


    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("Profile Picture") // Use the same path as in the loadImageFromUrl method
                    .child(currentUser.getUid() + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Now get the download URL and update the Realtime Database
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            updateProfileImageUrl(uri.toString());
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Handle failed upload
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Method to save changes to the profile picture
    private void saveProfilePictureChanges(Uri selectedImageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered User")
                    .child(currentUser.getUid());

            // Generate a unique filename for the new profile picture
            String newProfilePictureFilename = currentUser.getUid() + "_" + System.currentTimeMillis() + ".jpg";

            // Storage reference for the new profile picture
            StorageReference newProfilePictureRef = FirebaseStorage.getInstance().getReference()
                    .child("Profile Picture")
                    .child(newProfilePictureFilename);

            // Upload the new profile picture to Firebase Storage
            newProfilePictureRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Now get the download URL and update the Realtime Database
                        newProfilePictureRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update the profile picture URL in the database
                            userReference.child("Profile Photo URL").setValue(uri.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        // Profile picture URL updated successfully
                                        runOnUiThread(() -> {
                                            Toast.makeText(MainActivity5.this, "Changed Successfully", Toast.LENGTH_SHORT).show();
                                        });
                                        // Load the updated profile picture
                                        setCircularImage(uri);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failed database update
                                        runOnUiThread(() -> {
                                            Toast.makeText(MainActivity5.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        });
                                    })
                                    .addOnCompleteListener(task -> {
                                        progressBar.setVisibility(View.GONE);
                                    });
                        });
                    })

                    .addOnFailureListener(exception -> {
                        // Handle failed upload
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity5.this, "Failed to upload new profile picture", Toast.LENGTH_SHORT).show();
                        });
                        progressBar.setVisibility(View.GONE);
                    });
        }
    }

    private void updateProfileImageUrl(String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered User")
                    .child(currentUser.getUid());

            userReference.child("Profile Photo URL").setValue(imageUrl)
                    .addOnFailureListener(e -> {
                        // Handle failed database update
                        Toast.makeText(this, "Failed to update profile photo", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateProfileImage(Uri imageUri) {
        uploadImageToFirebaseStorage(imageUri);

        // Update the UI with the new profile image
        setCircularImage(imageUri);
    }



    // Method to underline a TextView
    private void underlineText(TextView textView) {
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(content);
    }

    // Method to save changes to the database
    private void saveChangesToDatabase(String newValue) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered User")
                    .child(currentUser.getUid());

            userReference.child("YearLevel").setValue(newValue);
        }
    }



    // Method to save changes to the database
    private void saveChangesToDatabase2(String newValue) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered User")
                    .child(currentUser.getUid());

            userReference.child("School").setValue(newValue);
        }
    }


    // Fetch user data and set EditText values
    private void fetchAndSetUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered User")
                    .child(currentUser.getUid());

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String firstname = dataSnapshot.child("Firstname").getValue(String.class);
                        String lastname = dataSnapshot.child("Lastname").getValue(String.class);
                        String userBday = dataSnapshot.child("Birthday").getValue(String.class);
                        String userAge = dataSnapshot.child("Age").getValue(String.class);
                        String userYearLevel = dataSnapshot.child("YearLevel").getValue(String.class);
                        String userSchoolName = dataSnapshot.child("School").getValue(String.class);

                        // Set values to respective EditText fields
                        EditText boxName = findViewById(R.id.boxname);
                        EditText boxBday = findViewById(R.id.boxbday);
                        EditText boxAge = findViewById(R.id.boxage);
                        EditText boxYearLevel = findViewById(R.id.yearlvl);
                        EditText boxSchoolName = findViewById(R.id.boxschoolname);

                        boxName.setText(firstname + " " + lastname);
                        boxBday.setText(userBday);
                        boxAge.setText(userAge);
                        boxYearLevel.setText(userYearLevel);
                        boxSchoolName.setText(userSchoolName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors if needed
                }
            });
        }
    }


    // Set user profile image
    private void setProfileImage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered User")
                    .child(currentUser.getUid());

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String profileImageUriString = dataSnapshot.child("Profile Photo URL").getValue(String.class);

                        // Set profile image using Glide or other libraries
                        // For simplicity, using a separate method to load image from URL
                        loadImageFromUrl(profileImageUriString);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors if needed
                }
            });
        }
    }

    // Load image from URL and set it to the profile ImageView
    private void loadImageFromUrl(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_profile_image) // Placeholder image while loading
                .error(R.drawable.default_profile_image) // Image to display in case of error
                .circleCrop() // Crop the image into a circular shape
                .into(profileImageView);
    }


    // Method to set the image in circular shape
    private void setCircularImage(Uri imageUri) {
        if (imageUri != null) {
            try {
                // Convert the image URI to a Bitmap
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

                // Resize the bitmap to match the dimensions of the profile image view
                int desiredWidth = profileImageView.getWidth(); // Get the width of the profile image view
                int desiredHeight = profileImageView.getHeight(); // Get the height of the profile image view
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true);

                // Create a circular bitmap using the resized bitmap
                Bitmap circularBitmap = getRoundedBitmap(resizedBitmap);

                // Set the circular bitmap to the ImageView
                profileImageView.setImageBitmap(circularBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Selected image URI is null", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Create a shader to paint with a circular pattern
        Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        // Draw a circle on the canvas with the bitmap shader
        canvas.drawCircle(width / 2f, height / 2f, Math.min(width, height) / 2f, paint);

        return output;
    }

    public void buttonCLick(View view) {
        Intent intent = new Intent(this, MainActivity4.class);
        startActivity(intent);
    }
}
