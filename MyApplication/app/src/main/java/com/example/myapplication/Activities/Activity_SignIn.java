package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_SignIn extends AppCompatActivity {

    private static final String TAG = "Activity_SignIn";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private Button goBackButton;
    private Button signUpButton;

    private String userName;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        findViews();
        allButtonListeners();
    }

    private void allButtonListeners() {
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_SignIn.this, Activity_SignUp.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
    }

    private void findViews() {
        emailEditText = findViewById(R.id.main_BOX_Email);
        passwordEditText = findViewById(R.id.main_BOX_password);
        signInButton = findViewById(R.id.main_BTN_SignIn);
        signUpButton = findViewById(R.id.main_BTN_signup);
        goBackButton = findViewById(R.id.signup_BTN_goBack);
    }

    private void signInUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserType(user.getUid());
                            userName = user.getDisplayName();
                        }
                    } else {
                        Toast.makeText(Activity_SignIn.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Sign-in failed: ", task.getException());
                    }
                });
    }

    private void checkUserType(String userId) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("userType").getValue(String.class);
                    if ("barber".equals(userType)) {
                        Intent intent = new Intent(Activity_SignIn.this, Activity_BarberProfile.class);
                      //  intent.putExtra("clientId",userName); // Sends the client's name to the next activity --> Activity_User_Appointments
                        startActivity(intent);
                        finish();
                    } else if ("client".equals(userType)) {
                        Intent intent = new Intent(Activity_SignIn.this, Activity_UserProfile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Activity_SignIn.this, "User type is unknown.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Activity_SignIn.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Activity_SignIn.this, "Database error.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: ", databaseError.toException());
            }
        });
    }
}
