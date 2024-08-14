package com.example.myapplication.Helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.Activities.Activity_BarberProfile;
import com.example.myapplication.Activities.Activity_UserProfile;
import com.example.myapplication.Activities.Activity_SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Singing_Helper {

    private static final String TAG = "Singing_Helper";
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public Singing_Helper() {
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void signInUser(Context context, String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity_SignIn) context, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserType(context, user.getUid());
                        }
                    } else {
                        Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Sign-in failed: ", task.getException());
                    }
                });
    }

    public void checkUserType(Context context, String userId) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("userType").getValue(String.class);
                    if ("barber".equals(userType)) {
                        Intent intent = new Intent(context, Activity_BarberProfile.class);
                        context.startActivity(intent);
                    } else if ("client".equals(userType)) {
                        Intent intent = new Intent(context, Activity_UserProfile.class);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "User type is unknown.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Database error.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: ", databaseError.toException());
            }
        });
    }

    public void saveUserData(Context context, FirebaseUser user, String name, String email, String phoneNumber, String password, String userType) {
        String userId = user.getUid();
        DatabaseReference userRef = usersRef.child(userId);

        userRef.child("email").setValue(email);
        userRef.child("name").setValue(name);
        userRef.child("phoneNumber").setValue(phoneNumber);
        userRef.child("password").setValue(password);
        userRef.child("userType").setValue(userType);

        Log.d(TAG, "User data saved: Name - " + name + ", Email - " + email + ", Phone - " + phoneNumber + ", Password - " + password + ", UserType - " + userType);

        checkUserType(context, userId);  // Redirect to the appropriate profile after registration
    }
}
