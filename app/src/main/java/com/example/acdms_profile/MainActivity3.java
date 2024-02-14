package com.example.acdms_profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity3 extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView showHidePasswordButton;
    private EditText boxPassword;
    private boolean isPasswordVisible = false;
    private EditText boxEmail;
    private ImageView profileImageView;
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextfname, editTextlname, editTextusername, editTextpassword,
              editTextschoolname, editTextyearlevel, editTextbirthday, editTextage;
    private ProgressBar progressBar;

    private Uri profileImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Initialize Firebase
        com.google.firebase.FirebaseApp.initializeApp(this);

        initializeViews();

        Button signupbutton = findViewById(R.id.signupbut);
        signupbutton.setOnClickListener(v -> handleSignUpButtonClick());

        TextView underlineTextView = findViewById(R.id.loginstead);
        setupUnderlineText(underlineTextView);

        TextView uploadPhotoTextView = findViewById(R.id.upload_Photo);
        setupUploadPhotoLink(uploadPhotoTextView);

        ImageView calendarIcon = findViewById(R.id.calendarIcon);
        calendarIcon.setOnClickListener(v -> showDatePickerDialog());

        EditText bdaybox = findViewById(R.id.boxBirthDate);
        bdaybox.setOnClickListener(v -> showDatePickerDialog());

        TextView logInsteadTextView = findViewById(R.id.loginstead);
        logInsteadTextView.setOnClickListener(v -> navigateToLogin());

        EditText boxBirthdDate = findViewById(R.id.boxBirthDate);
        boxBirthdDate.setInputType(InputType.TYPE_NULL);

        showHidePasswordButton = findViewById(R.id.showHidePasswordButton);
        updateShowHideButtonText();

        boxPassword = findViewById(R.id.boxpassword);
        setupPasswordVisibility();

        boxEmail = findViewById(R.id.boxusername);

        TextView textView = findViewById(R.id.loginstead);
        textView.setOnClickListener(v -> navigateToLogin());

        textView = findViewById(R.id.upload_Photo);
        profileImageView = findViewById(R.id.imageViewprofile);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        editTextbirthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updatePasswordVisibility();
            }
        });
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        editTextfname = findViewById(R.id.boxfirstname);
        editTextlname = findViewById(R.id.boxlastname);
        editTextusername = findViewById(R.id.boxusername);
        editTextpassword = findViewById(R.id.boxpassword);
        editTextschoolname = findViewById(R.id.boxschoolname);
        editTextyearlevel = findViewById(R.id.boxyearlevel);
        editTextbirthday = findViewById(R.id.boxBirthDate);
        editTextage = findViewById(R.id.boxage);
    }

    private void handleSignUpButtonClick() {
        String textfname = editTextfname.getText().toString();
        String textlname = editTextlname.getText().toString();
        String textusername = editTextusername.getText().toString();
        String textpassword = editTextpassword.getText().toString();
        String textschoolname = editTextschoolname.getText().toString();
        String textyearlevel = editTextyearlevel.getText().toString();
        String textbirthday = editTextbirthday.getText().toString();
        String textage = editTextage.getText().toString();

        if (TextUtils.isEmpty(textfname) || TextUtils.isEmpty(textlname) ||
                TextUtils.isEmpty(textusername) || TextUtils.isEmpty(textpassword) ||
                TextUtils.isEmpty(textschoolname) || TextUtils.isEmpty(textyearlevel)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(textusername).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            editTextusername.requestFocus();
        } else if (textpassword.length() < 8) {
            Toast.makeText(this, "Password should be at least 8 characters", Toast.LENGTH_SHORT).show();
            editTextpassword.requestFocus();
        } else {
            // Call registerUser with the selected image URI
            registerUser(textfname, textlname, textusername, textpassword, textschoolname,  textyearlevel, textbirthday, textage, profileImageUri);
            progressBar.setVisibility(View.VISIBLE);
        }

    }


    private void registerUser(String textfname, String textlname, String textusername, String textpassword,
                             String textschoolname,  String textyearlevel,  String textbirthday, String textage, Uri profileImageUri) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Create user profile
        auth.createUserWithEmailAndPassword(textusername, textpassword).addOnCompleteListener(MainActivity3.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            Toast.makeText(MainActivity3.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                            // Upload profile image to Firebase Storage
                            uploadProfileImage(firebaseUser.getUid(), profileImageUri);

                            // Enter user Data Info to the Firebase Realtime Database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textfname, textlname,   textschoolname, textyearlevel, textbirthday, textage);

                            // Extracting User reference from Database for Registered Users"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Send Verification email
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(MainActivity3.this, "Registered successfully. Please verify your email", Toast.LENGTH_SHORT).show();

                                        // Open user profile after successful registration
                                        Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish(); //to close Register  Activity
                                    } else {
                                        //Check if the failure is due to email already in use
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(MainActivity3.this, "Registration failed: Email is already in use", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(MainActivity3.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void setupUnderlineText(TextView underlineTextView) {
        String text = "Already have an account? Log In";
        SpannableString ss = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ss.setSpan(underlineSpan, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        underlineTextView.setText(ss);
        underlineTextView.setOnClickListener(v -> navigateToLogin());
    }

    private void setupUploadPhotoLink(TextView textView) {
        String text1 = "UPLOAD PROFILE";
        SpannableString ss1 = new SpannableString(text1);
        UnderlineSpan underlineSpan1 = new UnderlineSpan();
        ss1.setSpan(underlineSpan1, 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openImageChooser();
            }
        };

        ss1.setSpan(clickableSpan, 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#113946")), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(ss1);
    }

    private void showDatePickerDialog() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    private void uploadProfileImage(String userId, Uri imageUri) {
        if (imageUri != null) {
            // Access Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile Picture").child(userId);

            // Upload the image to Firebase Storage
            storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Get the download URL for the uploaded image
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save the download URL to the Firebase Realtime Database
                    saveImageURLToDatabase(userId, uri.toString());
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity3.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity3.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Selected image URI is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageURLToDatabase(String userId, String imageUrl) {
        // Save the image URL to the "profileImageUrl" node in the database
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
        referenceProfile.child(userId).child("Profile Photo URL").setValue(imageUrl);
    }



    private void setupPasswordVisibility() {
        boxPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updatePasswordVisibility();
            }
        });

        showHidePasswordButton.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        updatePasswordVisibility();
        updateShowHideButtonText();
    }

    private void updateShowHideButtonText() {
        String buttonText = isPasswordVisible ? "HIDE" : "SHOW";
        showHidePasswordButton.setText(buttonText);
    }

    private void updatePasswordVisibility() {
        int cursorPosition = boxPassword.getSelectionEnd();
        int inputType = isPasswordVisible ?
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

        boxPassword.setInputType(inputType);
        boxPassword.setTypeface(Typeface.DEFAULT);
        boxPassword.setSelection(cursorPosition);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, year);
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        int age = calculateAge(selectedDate);

        EditText boxAge = findViewById(R.id.boxage);
        boxAge.setText(String.valueOf(age));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String formattedDate = dateFormat.format(selectedDate.getTime());

        EditText boxbirthday = findViewById(R.id.boxBirthDate);
        boxbirthday.setText(formattedDate);
    }

    private int calculateAge(Calendar selectedDate) {
        Calendar currentDate = Calendar.getInstance();
        int age = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR);
        if (currentDate.get(Calendar.DAY_OF_YEAR) < selectedDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    //Upload Image
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            setCircularImage(selectedImageUri);

            // Save the selected image URI
            profileImageUri = selectedImageUri;

            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
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

    // Method to set the image in circular shape
    private void setCircularImage(Uri imageUri) {
        if (imageUri != null) {
            try {
                // Convert the image URI to a Bitmap
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Resize the bitmap to match the desired dimensions
                int desiredWidth = profileImageView.getWidth(); // Get the width of the profile image view
                int desiredHeight = profileImageView.getHeight(); // Get the height of the profile image view
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true);

                // Create a circular bitmap using the resized bitmap
                Bitmap circularBitmap = getRoundedBitmap(resizedBitmap);

                // Set the circular bitmap to the ImageView
                profileImageView.setImageBitmap(circularBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error handling image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selected image URI is null", Toast.LENGTH_SHORT).show();
        }
    }


    public void buttonCLick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }}