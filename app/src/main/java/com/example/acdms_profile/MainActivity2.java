package com.example.acdms_profile;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText boxusername, boxpassword;
    private TextView textView;
    private TextView showHidePasswordButton;
    private EditText boxPassword;

    private boolean isPasswordVisible = false;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        auth = FirebaseAuth.getInstance();
        boxusername = findViewById(R.id.boxusername);
        boxpassword = findViewById(R.id.boxpassword);
        progressBar = findViewById(R.id.progressBar);

        textView = findViewById(R.id.signupinstead);
        showHidePasswordButton = findViewById(R.id.showHidePasswordButton);
        updateShowHideButtonText();

        showHidePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        boxPassword = findViewById(R.id.boxpassword);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(intent);

                Toast.makeText(MainActivity2.this, "Sign Up First", Toast.LENGTH_SHORT).show();
            }
        });

        // Underline for "Don’t have an account? Sign Up"
        String text = "Don’t have an account? Sign Up";
        SpannableString ss = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ss.setSpan(underlineSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);

        // Add a TextWatcher to toggle password visibility
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
        if (isPasswordVisible) {
            boxPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            boxPassword.setTypeface(Typeface.DEFAULT);
        } else {
            boxPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            boxPassword.setTypeface(Typeface.DEFAULT);
        }
        boxPassword.setSelection(cursorPosition);
    }

    // Called when the "Login" button is clicked
    public void loginUser(View view) {
        String email = boxusername.getText().toString().trim();
        String password = boxpassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in the user with email and password
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(MainActivity2.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Open the main activity
                            Intent intent = new Intent(MainActivity2.this, MainActivity4.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Authentication", "signInWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(MainActivity2.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void buttonCLick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}