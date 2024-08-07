package com.example.myapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.myapplication.R;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Hairstyle extends AppCompatActivity{

//    private Button koreanPermButton;
//    private Button curlyMulletButton;
//    private Button shortMulletButton;
//    private Button centerPartMulletButton;
//    private Button spikyMulletButton;
//    private Button mediumMulletButton;
//    private Button wavyMulletButton;

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

//        // שיוך ה-ClickListener לכפתור Korean Perm
//        koreanPermButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // הפעלת פעילות חדשה או פעולה אחרת
//            }
//        });
//
//        // שיוך ה-ClickListener לכפתור Curly Mullet
//        curlyMulletButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // הפעלת פעילות חדשה או פעולה אחרת
//            }
//        });
//
//        // שיוך ה-ClickListener לכפתור Short Mullet
//        shortMulletButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // הפעלת פעילות חדשה או פעולה אחרת
//            }
//        });
//
//        // שיוך ה-ClickListener לכפתור Center Part Mullet
//        centerPartMulletButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // הפעלת פעילות חדשה או פעולה אחרת
//            }
//        });
//
//        // שיוך ה-ClickListener לכפתור Spiky Mullet
//        spikyMulletButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // הפעלת פעילות חדשה או פעולה אחרת
//            }
//        });
//
//        // שיוך ה-ClickListener לכפתור Medium Mullet
//        mediumMulletButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // הפעלת פעילות חדשה או פעולה אחרת
//            }
//        });
//
//        // שיוך ה-ClickListener לכפתור Wavy Mullet
//        wavyMulletButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // הפעלת פעילות חדשה או פעולה אחרת
//            }
//        });
    }


    private void findViews() {
        backButton = findViewById(R.id.calender_BTN_backButton);
        // מציאת כל הכפתורים ושיוך ה-ClickListener לכל כפתור
//        koreanPermButton = findViewById(R.id.korean_perm_button);
//        curlyMulletButton = findViewById(R.id.curly_mullet_button);
//        shortMulletButton = findViewById(R.id.short_mullet_button);
//        centerPartMulletButton = findViewById(R.id.center_part_mullet_button);
//        spikyMulletButton = findViewById(R.id.spiky_mullet_button);
//        mediumMulletButton = findViewById(R.id.medium_mullet_button);
//        wavyMulletButton = findViewById(R.id.wavy_mullet_button);
    }
}
