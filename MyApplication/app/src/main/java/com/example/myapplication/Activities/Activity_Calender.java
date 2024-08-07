package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class Activity_Calender extends AppCompatActivity{

    private Button backButton;
    private Button timeSlot1;
    private Button timeSlot2;
    private Button timeSlot3;
    private Button setAppointmentButton;

    private GridView calendarGrid;
    private TextView availableTimeSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        findViews();
        allButtonListeners();
        // מציאת כל הרכיבים לפי ה-ID שלהם




        // כאן תוכל להוסיף קוד נוסף למילוי לוח השנה ולזמני המשבצות לפי הצורך
    }

    private void allButtonListeners() {
        // שיוך מאזינים (ClickListeners) לכל הכפתורים
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // סגור את הפעילות הנוכחית וחזור לפעילות הקודמת
            }
        });

        timeSlot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // פעולה עבור בחירת משבצת זמן 1
            }
        });

        timeSlot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // פעולה עבור בחירת משבצת זמן 2
            }
        });

        timeSlot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // פעולה עבור בחירת משבצת זמן 3
            }
        });

        setAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // פעולה עבור קביעת תור
            }
        });
    }

    private void findViews() {
        backButton = findViewById(R.id.calender_BTN_backButton);
        timeSlot1 = findViewById(R.id.timeSlot1);
        timeSlot2 = findViewById(R.id.timeSlot2);
        timeSlot3 = findViewById(R.id.timeSlot3);
        setAppointmentButton = findViewById(R.id.setAppointmentButton);
        calendarGrid = findViewById(R.id.calendarGrid);
        availableTimeSlots = findViewById(R.id.availableTimeSlots);
    }

}
