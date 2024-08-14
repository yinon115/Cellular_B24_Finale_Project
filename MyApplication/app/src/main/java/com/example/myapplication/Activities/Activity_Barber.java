package com.example.myapplication.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.Appointment;
import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Barber extends AppCompatActivity {

    private static final String TAG = "Activity_Barber";

    private String barberId;
    private String barberName;

    private Button backButton;

    private TextView barberNameView;

    private LinearLayout appointmentListLayout;

    private DatabaseReference appointmentsRef;

    public Activity_Barber() {
        // Default constructor
    }

    public Activity_Barber(String barberId, String name) {
        this.barberId = barberId;
        this.barberName = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        appointmentsRef = database.getReference("appointments");

        findViews();
        getBarberInformation();
        allButtonListeners();

        loadAppointments();
    }

    private void getBarberInformation() {     // Receiving the Firebase DB
        Intent intent = getIntent();
        barberId = intent.getStringExtra("barberId");
        barberName = intent.getStringExtra("barberName");
        barberNameView.setText(barberName);
    }

    private void allButtonListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findViews() {
        backButton = findViewById(R.id.barber_BTN_backButton);
        barberNameView = findViewById(R.id.barberName);
        appointmentListLayout = findViewById(R.id.appointmentList);
    }

    private void loadAppointments() {
        appointmentsRef.orderByChild("barberId").equalTo(barberId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appointmentListLayout.removeAllViews(); // Clear existing appointments
                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        String appointmentId = appointmentSnapshot.getKey();
                        String appointmentTime = appointment.formatDateTime();
                        String customerName = appointment.getClientName();
                        Log.d(TAG, "Loaded appointment for client: " + customerName);

                        addAppointmentToView(appointmentId, appointmentTime, customerName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                GeneralFun_Helper.activities_showToast(Activity_Barber.this, "Failed to load appointments.");
            }
        });
    }

    private void addAppointmentToView(final String appointmentId, String appointmentTime, String customerName) {
        View appointmentView = getLayoutInflater().inflate(R.layout.appointment_item, null);

        TextView appointmentTimeText = appointmentView.findViewById(R.id.appointmentTime);
        TextView customerNameText = appointmentView.findViewById(R.id.customerName);
        Button cancelButton = appointmentView.findViewById(R.id.cancelButton);

        appointmentTimeText.setText(appointmentTime);
        customerNameText.setText(customerName);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAppointment(appointmentId);
            }
        });

        appointmentListLayout.addView(appointmentView);
    }

    private void cancelAppointment(String appointmentId) {
        appointmentsRef.child(appointmentId).removeValue();
        loadAppointments(); // Refresh the list after cancellation
    }

    public String getBarberName() {
        return barberName;
    }

    public String getBarberId() {
        return barberId;
    }

    @Override
    public String toString() {
        return "Barber's Name: " + barberName;
    }
}
