package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_UserProfile extends AppCompatActivity {

    private static final String TAG = "Activity_UserProfile";

    private Button catalog;
    private Button rateBarberButton;
    private TextView userProfile_dateText;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneTextView;
    private Button bookNewAppointmentButton;
    private Button existingAppointmentsButton;
    private Button signOutButton;

    private List<Activity_Barber> barberList;
    private DatabaseReference usersRef;
    private String selectedBarberId;
    private String selectedBarberName;

    private Appointment newAppointment;
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

    private Appointment_Helper appointmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        barberList = new ArrayList<>();

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        findViews();
        setUserInformation();
        allButtonListeners();

        appointmentHelper = new Appointment_Helper(this, userName, message -> userProfile_dateText.setText(message));
    }

    private void loadBarbersForRating() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barberList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userType = snapshot.child("userType").getValue(String.class);
                    if ("barber".equals(userType)) {
                        String barberId = snapshot.getKey();
                        String name = snapshot.child("name").getValue(String.class);
                        if (barberId != null && name != null) {
                            barberList.add(new Activity_Barber(barberId, name));
                        }
                    }
                }
                showBarberSelectionDialogForRating();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadBarbers:onCancelled", databaseError.toException());
            }
        });
    }

    private void showBarberSelectionDialogForRating() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_barber);

        ListView listViewBarbers = dialog.findViewById(R.id.list_view_barbers);
        ArrayAdapter<Activity_Barber> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, barberList);
        listViewBarbers.setAdapter(adapter);

        listViewBarbers.setOnItemClickListener((parent, view, position, id) -> {
            Activity_Barber selectedBarber = barberList.get(position);
            selectedBarberId = selectedBarber.getBarberId();
            selectedBarberName = selectedBarber.getBarberName();
            Toast.makeText(Activity_UserProfile.this, "Selected: " + selectedBarber.getBarberName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            showRatingDialog();
        });

        dialog.show();
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rate_barber);

        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        Button submitRatingButton = dialog.findViewById(R.id.button_submit_rating);

        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating();
                Log.d(TAG, "Rating: " + rating);
                saveRating(rating);
                dialog.dismiss();
            }
        });

        Button closeButton = dialog.findViewById(R.id.button_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveRating(float rating) {
        Log.d(TAG, "Saving rating for barber: " + selectedBarberName);
        usersRef.child(selectedBarberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Barber exists: " + selectedBarberName);
                    String userType = snapshot.child("userType").getValue(String.class);
                    if ("barber".equals(userType)) {
                        int numOfRatings = snapshot.child("numOfRatings").getValue(Integer.class) != null ? snapshot.child("numOfRatings").getValue(Integer.class) : 0;
                        float currentRating = snapshot.child("rating").getValue(Float.class) != null ? snapshot.child("rating").getValue(Float.class) : 0.0f;

                        float newRating = ((currentRating * numOfRatings) + rating) / (numOfRatings + 1);

                        snapshot.getRef().child("rating").setValue(newRating);
                        snapshot.getRef().child("numOfRatings").setValue(numOfRatings + 1);

                        Toast.makeText(Activity_UserProfile.this, "Rating saved successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Activity_UserProfile.this, "Cannot rate non-barber user.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Barber not found: " + selectedBarberName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "saveRating:onCancelled", error.toException());
            }
        });
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

        FirebaseAuth.getInstance().signOut();

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
                signOut();
                Intent intent = new Intent(Activity_UserProfile.this, Activity_Main.class);
                startActivity(intent);
            }
        });

        rateBarberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBarbersForRating();
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
