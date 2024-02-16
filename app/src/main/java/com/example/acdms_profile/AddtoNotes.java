package com.example.acdms_profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.Timestamp;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class AddtoNotes extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1001;
    private static final int REQUEST_CODE_PICK_IMAGE = 1002;

    EditText titleEditText, contentEditText;
    Button saveNoteBtn;
    TextView pageTitleText;
    ImageView micImageView, cameraImageView, deleteNoteBtn;

    TextRecognizer textRecognizer;
    Uri imageUri;

    String title, content, docId, courseId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_notes);

        titleEditText = findViewById(R.id.notes_title);
        contentEditText = findViewById(R.id.notes_content);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleText = findViewById(R.id.page_title);
        deleteNoteBtn = findViewById(R.id.deleteNoteBtn);
        micImageView = findViewById(R.id.mic);
        cameraImageView = findViewById(R.id.camera);

        // Receive data when a note is clicked
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");
        courseId = getIntent().getStringExtra("courseId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if (isEditMode) {
            pageTitleText.setText("NOTES");
            deleteNoteBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener(v -> saveNote());
        deleteNoteBtn.setOnClickListener(v -> deleteNoteFromFirebase());

        micImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        // Initialize TextRecognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }


    private void saveNote() {
        String noteTitle = titleEditText.getText().toString().trim();
        String noteContent = contentEditText.getText().toString();

        if (noteTitle == null || noteTitle.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }

        DataClass note = new DataClass();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());


        if (docId == null || docId.isEmpty()) {
            // Generate a new docId for new notes
            docId = Utility.getCollectionReferenceForNotes(courseId).document().getId();
        }

        note.setDocId(docId);

        saveNoteToFirebase(note);
    }

    private void saveNoteToFirebase(DataClass note) {
        DocumentReference documentReference;

        if (isEditMode) {
            // Update note
            documentReference = Utility.getCollectionReferenceForNotes(courseId).document(docId);
        } else {
            documentReference = Utility.getCollectionReferenceForNotes(courseId).document();
            docId = documentReference.getId(); // Set the docId for new notes
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddtoNotes.this, "Note Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddtoNotes.this, "Failed Adding Note", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        ImagePicker.with(this)
                .crop()	     //Crop image(Optional), Check Customization for more option
                .compress(1024)	 //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080) //Final image resolution will be less than 1080 x 1080(Optional)
                .start(REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    contentEditText.setText(result.get(0));
                }
            }
        } else if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            recognizeTextFromImage();
        }
    }

    private void recognizeTextFromImage() {
        if (imageUri != null) {
            try {
                InputImage inputImage = InputImage.fromFilePath(AddtoNotes.this, imageUri);
                Task<Text> result = textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        String recognizedText = text.getText();
                        recognizedText = recognizedText.replace("\n", " ");
                        contentEditText.setText(recognizedText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddtoNotes.this, "Text recognition failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                Toast.makeText(AddtoNotes.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    void deleteNoteFromFirebase() {
        DocumentReference documentReference;

        documentReference = Utility.getCollectionReferenceForNotes(courseId).document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddtoNotes.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddtoNotes.this, "Failed Deleting Note", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
