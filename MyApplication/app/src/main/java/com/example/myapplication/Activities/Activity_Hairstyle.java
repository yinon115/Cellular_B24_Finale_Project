package com.example.myapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.myapplication.R;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Hairstyle extends AppCompatActivity{

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairstyles);
        findViews();
        allButtonListeners();

    }

    private void allButtonListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // סגור את הפעילות הנוכחית וחזור לפעילות הקודמת
            }
        });
    }

    private void findViews() {
        backButton = findViewById(R.id.calender_BTN_backButton);
    }
}
