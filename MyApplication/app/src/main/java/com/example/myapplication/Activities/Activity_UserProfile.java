package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.Appointment_Helper;
import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.Helpers.Rating_Helper;
import com.example.myapplication.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_UserProfile extends AppCompatActivity {

    private static final String TAG = "Activity_UserProfile";

    private String currentUserId;
    private String userName;
    private String userEmail;
    private String userPhone;

    private Button catalog;
    private Button rateBarberButton;
    private Button bookNewAppointmentButton;
    private Button existingAppointmentsButton;
    private Button signOutButton;

    private TextView userProfile_dateText;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneTextView;

    private DatabaseReference usersRef;

    private Appointment_Helper appointmentHelper;

    private Rating_Helper ratingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

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

                    appointmentHelper = new Appointment_Helper(Activity_UserProfile.this, userName, message -> userProfile_dateText.setText(message));
                    ratingHelper = new Rating_Helper(Activity_UserProfile.this, userName);
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
                Intent intent = new Intent(Activity_UserProfile.this, Activity_Hairstyle.class);
                startActivity(intent);
            }
        });

        bookNewAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentHelper.loadBarbers();
            }
        });

        existingAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_UserProfile.this, Activity_User_Appointments.class);
                intent.putExtra("clientId", userName);
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFun_Helper.signOut(Activity_UserProfile.this);

            }
        });

        rateBarberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingHelper.loadBarbersForRating();
            }
        });
    }

    private void findViews() {
        catalog = findViewById(R.id.userProfile_BTN_catalog);
        rateBarberButton = findViewById(R.id.userProfile_BTN_rateBarber);
        userProfile_dateText = findViewById(R.id.userProfile_TXT_date_text);
        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        userPhoneTextView = findViewById(R.id.user_phone);
        bookNewAppointmentButton = findViewById(R.id.userProfile_BTM_bookNewAppointment);
        existingAppointmentsButton = findViewById(R.id.userProfile_BTN_existingAppointments);
        signOutButton = findViewById(R.id.userProfile_BTN_signOut);
    }
}
