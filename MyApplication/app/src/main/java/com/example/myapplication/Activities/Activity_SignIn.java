package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.Singing_Helper;
import com.example.myapplication.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class Activity_SignIn extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    private Button signInButton;
    private Button goBackButton;
    private Button signUpButton;

    private Singing_Helper singingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        singingHelper = new Singing_Helper();
        findViews();
        allButtonListeners();
    }

    private void allButtonListeners() {
        goBackButton.setOnClickListener(v -> finish());

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(Activity_SignIn.this, Activity_SignUp.class));
        });

        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            singingHelper.signInUser(Activity_SignIn.this, email, password);
        });
    }

    private void findViews() {
        emailEditText = findViewById(R.id.main_BOX_Email);
        passwordEditText = findViewById(R.id.main_BOX_password);
        signInButton = findViewById(R.id.main_BTN_SignIn);
        signUpButton = findViewById(R.id.main_BTN_signup);
        goBackButton = findViewById(R.id.signup_BTN_goBack);
    }
}
