package com.example.bettrhelp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editTextEmail, editTextPassword;
    private CheckBox checkBoxTermCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        TextView textSignUpClick = findViewById(R.id.textSignUpClick);
        checkBoxTermCondition = findViewById(R.id.checkBoxTermCondition);

        buttonSignUp.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            boolean valid = true;

            if (email.isEmpty()) {
                editTextEmail.setError("Email cannot be empty");
                valid = false;
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Password cannot be empty");
                valid = false;
            }
            if (!checkBoxTermCondition.isChecked()) {
                Toast.makeText(SignUpActivity.this, "Please accept the terms and conditions", Toast.LENGTH_LONG).show();
                valid = false;
            }

            if (valid) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        finish();
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(SignUpActivity.this, "Sign Up Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        textSignUpClick.setOnClickListener(view ->
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class))
        );
    }
}
