package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_BarberProfile extends AppCompatActivity {

    private static final String TAG = "Activity_BarberProfile";

    private Button catalog;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneTextView;
    private Button existingAppointmentsButton;
    private Button signOutButton;

    private DatabaseReference usersRef;

    private String currentUserId;
    private String userName;
    private String userEmail;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_profile);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        findViews();
        setUserInformation();
        allButtonListeners();
    }

    private void setUserInformation() {
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("name").getValue(String.class);
                    userEmail = snapshot.child("email").getValue(String.class);
                    userPhone = snapshot.child("phoneNumber").getValue(String.class);

                    userNameTextView.setText(userName != null ? userName : "");
                    userEmailTextView.setText(userEmail != null ? userEmail : "");
                    userPhoneTextView.setText(userPhone != null ? userPhone : "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read user data.", error.toException());
            }
        });
    }

    private void signOut() {
        Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();

        // Sign out the user
        FirebaseAuth.getInstance().signOut();

        // Navigate to the Activity_SignIn and finish the current activity
        Intent intent = new Intent(Activity_BarberProfile.this, Activity_SignIn.class);
        startActivity(intent);
        finish();
    }

    private void allButtonListeners() {
        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_BarberProfile.this, Activity_Hairstyle.class);
                startActivity(intent);
            }
        });

        existingAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_BarberProfile.this, Activity_Barber.class);
                intent.putExtra("clientId", userName); // Sends the client's name to the next activity --> Activity_User_Appointments
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void findViews() {
        catalog = findViewById(R.id.barberProfile_BTN_catalog);
        userNameTextView = findViewById(R.id.barber_name);
        userEmailTextView = findViewById(R.id.barber_email);
        userPhoneTextView = findViewById(R.id.barber_phone);
        existingAppointmentsButton = findViewById(R.id.barberProfile_BTM_bookNewAppointment);
        signOutButton = findViewById(R.id.barberProfile_BTN_signOut);
    }
}
