package com.example.bettrhelp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        FirebaseAuth auth = FirebaseAuth.getInstance();  // 局部变量
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        TextView textSignInClick = findViewById(R.id.textSignInClick);

        buttonSignIn.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty()) {
                editTextEmail.setError("Email cannot be empty");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Please enter valid email");
            } else if (password.isEmpty()) {
                editTextPassword.setError("Password cannot be empty");
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            Toast.makeText(SignInActivity.this, "Sign In Successful", Toast.LENGTH_LONG).show();
                            // 这里不需要user变量，除非你要用它
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(SignInActivity.this, "Sign In Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
            }
        });

        textSignInClick.setOnClickListener(view ->
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class))
        );

    }
}
