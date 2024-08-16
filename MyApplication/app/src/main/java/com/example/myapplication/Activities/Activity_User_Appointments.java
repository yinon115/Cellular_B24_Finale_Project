package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.Appointment;
import com.example.myapplication.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_User_Appointments extends AppCompatActivity {

    private Button goBackButton;
    private TextView upcomingAppointments;

    private ListView listViewAppointments;
    private ArrayAdapter<String> adapter;
    private List<String> appointmentList;

    private String currentUserId;
    private String clientName;

    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        findViews();
        allButtonListeners();

        clientName = getIntent().getStringExtra("clientId");// Receives the client's name
                                                        // from the previous activity --> Activity_UserProfile

        //  Initializing Firebase DB
        database = FirebaseDatabase.getInstance().getReference("appointments");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Loading appointments from Firebase
        loadAppointments();
    }

    private void allButtonListeners() {
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findViews() {
        goBackButton = findViewById(R.id.myAppointments_BTN_goBack);
        listViewAppointments = findViewById(R.id.list_view_appointments);
        upcomingAppointments = findViewById(R.id.myAppointments_LBL_upcomingApp);

        // ListView definition
        appointmentList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentList);
        listViewAppointments.setAdapter(adapter);
    }

    private void loadAppointments() {

        database.orderByChild("clientName").equalTo(clientName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        appointmentList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Appointment appointment = snapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                String appointmentInfo = "Client: " + appointment.getClientName() + "\nDate: " +
                                                appointment.formatDateTime() +"\nBarber: " + appointment.getBarberName();
                                appointmentList.add(appointmentInfo);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadAppointments:onCancelled", databaseError.toException());
                    }
                });
    }
}