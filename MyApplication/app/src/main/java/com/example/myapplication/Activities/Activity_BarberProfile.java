package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_BarberProfile extends AppCompatActivity {

    private static final String TAG = "Activity_BarberProfile";

    private String currentUserId;
    private String barberName;
    private String barberEmail;
    private String barberPhone;
    private String barberRating;

    private Button catalog;
    private Button existingAppointmentsButton;
    private Button signOutButton;

    private TextView barberNameTextView;
    private TextView barberEmailTextView;
    private TextView barberPhoneTextView;
    private TextView barberRatingTextView;

    private DatabaseReference usersRef;

    private Float ratingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_profile);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        findViews();
        setBarberInformation();
        allButtonListeners();
    }

    private void setBarberInformation() {
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    barberName = snapshot.child("name").getValue(String.class);
                    barberEmail = snapshot.child("email").getValue(String.class);
                    barberPhone = snapshot.child("phoneNumber").getValue(String.class);
                    ratingValue = snapshot.child("rating").getValue(Float.class);
                    barberRating = ratingValue != null ? String.valueOf(ratingValue) : "No Rating";

                    barberNameTextView.setText(barberName != null ? barberName : "");
                    barberEmailTextView.setText(barberEmail != null ? barberEmail : "");
                    barberPhoneTextView.setText(barberPhone != null ? barberPhone : "");
                    barberRatingTextView.setText(barberRating != null ? barberRating : "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read user data.", error.toException());
            }
        });
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
                intent.putExtra("barberId", currentUserId); // Sends the barber's ID to the next activity
                intent.putExtra("barberName", barberName); // Sends the barber's name to the next activity
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFun_Helper.signOut(Activity_BarberProfile.this);
            }
        });
    }

    private void findViews() {
        catalog = findViewById(R.id.barberProfile_BTN_catalog);
        barberNameTextView = findViewById(R.id.barber_name);
        barberEmailTextView = findViewById(R.id.barber_email);
        barberPhoneTextView = findViewById(R.id.barber_phone);
        barberRatingTextView = findViewById(R.id.barber_rating);
        existingAppointmentsButton = findViewById(R.id.barberProfile_BTM_bookNewAppointment);
        signOutButton = findViewById(R.id.barberProfile_BTN_signOut);
    }
}
