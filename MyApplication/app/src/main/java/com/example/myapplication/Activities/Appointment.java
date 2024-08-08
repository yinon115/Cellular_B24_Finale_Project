package com.example.myapplication.Activities;

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
        // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }
}