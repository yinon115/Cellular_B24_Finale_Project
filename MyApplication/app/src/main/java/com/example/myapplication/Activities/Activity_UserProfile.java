package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Activity_UserProfile extends AppCompatActivity {

    private static final String TAG = "Activity_UserProfile";

    private Button catalog;

    private TextView userProfile_dateText;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneTextView;
    private Button bookNewAppointmentButton;
    private Button existingAppointmentsButton;
    private Button signOutButton;

    private Appointment newAppointment;
    private DatabaseReference usersRef;
    private DatabaseReference appointmentsRef;

    private String currentUserId;
    private String userName;
    private String userEmail;
    private String userPhone;

    private int app_year;
    private int app_month;
    private int app_day;
    private int app_hour;
    private int app_minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

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
        Intent intent = new Intent(Activity_UserProfile.this, Activity_SignIn.class);
        startActivity(intent);
        finish();
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
                openDatePicker(); // Open date picker dialog
            }
        });

        existingAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_UserProfile.this, Activity_User_Appointments.class);
                intent.putExtra("clientId",userName); // Sends the client's name to the next activity --> Activity_User_Appointments
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                Intent intent = new Intent(Activity_UserProfile.this, Activity_Main.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        catalog = findViewById(R.id.userProfile_BTN_catalog);
        userProfile_dateText = findViewById(R.id.userProfile_TXT_date_text);
        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        userPhoneTextView = findViewById(R.id.user_phone);
        bookNewAppointmentButton = findViewById(R.id.userProfile_BTM_bookNewAppointment);
        existingAppointmentsButton = findViewById(R.id.userProfile_BTN_existingAppointments);
        signOutButton = findViewById(R.id.userProfile_BTN_signOut);
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                    Toast.makeText(Activity_UserProfile.this, "Appointments cannot be set on Friday or Saturday. Please choose another day.", Toast.LENGTH_LONG).show();
                    openDatePicker(); // Reopen date picker
                } else {
                    app_year = year;
                    app_month = month;
                    app_day = dayOfMonth;
                    openTimePicker(); // Open time picker dialog after the date has been chosen
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (hour < 9 || hour >= 18) {
                    Toast.makeText(Activity_UserProfile.this, "Please select a time between 9 AM and 6 PM.", Toast.LENGTH_LONG).show();
                    openTimePicker(); // Reopen time picker
                } else {
                    // Valid time selected
                    app_hour = hour;
                    app_minutes = minute;
                    newAppointment = new Appointment(userName, app_year, (app_month + 1), app_day, app_hour, app_minutes);

                    // Checking for overlaping before setting new appointment
                    checkAppointmentOverlap(newAppointment, new OnCheckAppointmentListener() {
                        @Override
                        public void onCheckCompleted(boolean isAvailable) {
                            if (isAvailable) {
                                // Saving the new appointment to Firebase
                                saveAppointmentToFirebase(newAppointment);
                                userProfile_dateText.setText("An appointment was set on: " + (app_month + 1) + "/" + app_day + "/" + app_year);
                                userProfile_dateText.append(" " + String.format("%02d:%02d", app_hour, app_minutes));

                            } else {
                                Toast.makeText(Activity_UserProfile.this, "The selected time slot is not available. Please choose another time.", Toast.LENGTH_LONG).show();
                                userProfile_dateText.setText("Appointment wasn't set\n     Please try again");
                            }
                        }
                    });
                }
            }
        }, 9, 0, false);
        timePickerDialog.show();
    }


    private void saveAppointmentToFirebase(Appointment appointment) {
        String appointmentId = appointmentsRef.push().getKey();
        if (appointmentId != null) {
            appointmentsRef.child(appointmentId).setValue(appointment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Activity_UserProfile.this, "Appointment saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Activity_UserProfile.this, "Failed to save appointment", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void checkAppointmentOverlap(final Appointment newAppointment, final OnCheckAppointmentListener listener) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean overlap = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment existingAppointment = snapshot.getValue(Appointment.class);
                    if (existingAppointment != null && isOverlapping(existingAppointment, newAppointment)) {
                        overlap = true;
                        break;
                    }
                }
                listener.onCheckCompleted(!overlap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "checkAppointmentOverlap:onCancelled", databaseError.toException());
            }
        });
    }

    private boolean isOverlapping(Appointment existingAppointment, Appointment newAppointment) {
        Calendar existingCal = Calendar.getInstance();
        existingCal.set(existingAppointment.year, existingAppointment.month, existingAppointment.day, existingAppointment.hour, existingAppointment.minute);

        Calendar newCal = Calendar.getInstance();
        newCal.set(newAppointment.year, newAppointment.month, newAppointment.day, newAppointment.hour, newAppointment.minute);

        long existingTime = existingCal.getTimeInMillis();
        long newTime = newCal.getTimeInMillis();
        long diff = Math.abs(existingTime - newTime);

        return diff < 15 * 60 * 1000; // A difference of less than 15 minutes between each appointment
    }

    interface OnCheckAppointmentListener {
        void onCheckCompleted(boolean isAvailable);
    }


}
