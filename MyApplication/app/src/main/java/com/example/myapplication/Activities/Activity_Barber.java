package com.example.myapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Barber extends AppCompatActivity {

    private Button backButton;
    private TextView barberName;
    private LinearLayout appointmentListLayout;
    private DatabaseReference appointmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber);

        backButton = findViewById(R.id.barber_BTN_backButton);
        barberName = findViewById(R.id.barberName);
        appointmentListLayout = findViewById(R.id.appointmentList);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        appointmentsRef = database.getReference("appointments");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity
            }
        });

        loadAppointments();
    }

    private void loadAppointments() {
        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appointmentListLayout.removeAllViews(); // Clear existing appointments
                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        String appointmentId = appointmentSnapshot.getKey();
                        String appointmentTime = appointment.formatDateTime();//appointmentSnapshot.child("time").getValue(String.class);
                        String customerName = appointment.getClientName();//appointmentSnapshot.child("customerName").getValue(String.class);

                        addAppointmentToView(appointmentId, appointmentTime, customerName);

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(Activity_Barber.this, "Failed to load appointments.", Toast.LENGTH_SHORT).show();
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
}
