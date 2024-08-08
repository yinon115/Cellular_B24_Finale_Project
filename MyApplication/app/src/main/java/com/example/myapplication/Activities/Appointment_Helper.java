package com.example.myapplication.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.Activities.Appointment;
import com.example.myapplication.Activities.Activity_Barber;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Appointment_Helper {

    private static final String TAG = "Appointment_Helper";

    private Context context;
    private String clientName;
    private DatabaseReference usersRef;
    private DatabaseReference appointmentsRef;
    private List<Activity_Barber> barberList;
    private String selectedBarberId;
    private String selectedBarberName;

    private int app_year;
    private int app_month;
    private int app_day;
    private int app_hour;
    private int app_minutes;

    private OnAppointmentSetListener appointmentSetListener;

    public Appointment_Helper(Context context, String clientName, OnAppointmentSetListener listener) {
        this.context = context;
        this.clientName = clientName;
        this.appointmentSetListener = listener;
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");
        barberList = new ArrayList<>();
    }

    public void loadBarbers() {
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
                showBarberSelectionDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadBarbers:onCancelled", databaseError.toException());
            }
        });
    }

    private void showBarberSelectionDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_select_barber);

        ListView listViewBarbers = dialog.findViewById(R.id.list_view_barbers);
        ArrayAdapter<Activity_Barber> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, barberList);
        listViewBarbers.setAdapter(adapter);

        listViewBarbers.setOnItemClickListener((parent, view, position, id) -> {
            Activity_Barber selectedBarber = barberList.get(position);
            selectedBarberId = selectedBarber.getBarberId();
            selectedBarberName = selectedBarber.getBarberName();
            Toast.makeText(context, "Selected: " + selectedBarber.getBarberName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            openDatePicker();
        });

        dialog.show();
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                    Toast.makeText(context, "Appointments cannot be set on Friday or Saturday. Please choose another day.", Toast.LENGTH_LONG).show();
                    openDatePicker(); // Reopen date picker
                } else {
                    app_year = year;
                    app_month = month;
                    app_day = dayOfMonth;
                    openTimePicker(); // Open time picker dialog after the date has been chosen
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // הגדרת התאריך המינימלי לתאריך הנוכחי
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (hour < 9 || hour >= 18) {
                    Toast.makeText(context, "Please select a time between 9 AM and 6 PM.", Toast.LENGTH_LONG).show();
                    openTimePicker(); // Reopen time picker
                } else {
                    // Valid time selected
                    app_hour = hour;
                    app_minutes = minute;
                    Appointment newAppointment = new Appointment(clientName, app_year, (app_month + 1), app_day, app_hour, app_minutes, selectedBarberId, selectedBarberName);

                    // Checking for overlapping before setting new appointment
                    checkAppointmentOverlap(newAppointment, new OnCheckAppointmentListener() {
                        @Override
                        public void onCheckCompleted(boolean isAvailable) {
                            if (isAvailable) {
                                // Saving the new appointment to Firebase
                                saveAppointmentToFirebase(newAppointment);
                                appointmentSetListener.onAppointmentSet("An appointment was set on: " + (app_month + 1) + "/" + app_day + "/" + app_year + " " + String.format("%02d:%02d", app_hour, app_minutes));
                            } else {
                                Toast.makeText(context, "The selected time slot is not available. Please choose another time.", Toast.LENGTH_LONG).show();
                                appointmentSetListener.onAppointmentSet("Appointment wasn't set\n     Please try again");
                            }
                        }
                    });
                }
            }
        }, 9, 0, false);

        // הצגת ה- TimePickerDialog
        timePickerDialog.show();
    }

    private void saveAppointmentToFirebase(Appointment appointment) {
        String appointmentId = appointmentsRef.push().getKey();
        if (appointmentId != null) {
            appointmentsRef.child(appointmentId).setValue(appointment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Appointment saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to save appointment", Toast.LENGTH_SHORT).show();
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

    public interface OnAppointmentSetListener {
        void onAppointmentSet(String message);
    }
}
