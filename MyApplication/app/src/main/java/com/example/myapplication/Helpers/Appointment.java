package com.example.myapplication.Helpers;

import android.util.Log;

public class Appointment {
    private static final String TAG = "Appointment";

    public String clientName;
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public String barberId;
    public String barberName;

    public Appointment() {
        // Default constructor
    }

    public Appointment(String clientName, int year, int month, int day, int hour, int minute, String barberId, String barberName) {
        this.clientName = clientName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.barberId = barberId;
        this.barberName = barberName;
    }

    public String formatDateTime() {
        return String.format("%04d-%02d-%02d %02d:%02d", year, month, day, hour, minute);
    }

    public String getClientName() {
        Log.d(TAG, "getClientName: " + clientName);
        return clientName;
    }

    public void setClientName(String clientName) {
        Log.d(TAG, "setClientName: " + clientName);
        this.clientName = clientName;
    }

    public String getBarberName() {
        return barberName;
    }

}