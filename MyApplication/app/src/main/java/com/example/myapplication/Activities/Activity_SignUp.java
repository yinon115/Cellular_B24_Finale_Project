package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_SignUp extends AppCompatActivity {

    private static final String TAG = "Activity_SignUp";

    private Button submitButton;
    private Button goBackButton;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;
    private RadioGroup userTypeRadioGroup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        findViews();
        allButtonListeners();
    }

    private void allButtonListeners() {
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String phoneNumber = phoneNumberEditText.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneNumber.isEmpty()) {
                    registerUser(name, email, password, phoneNumber);
                } else {
                    Toast.makeText(Activity_SignUp.this, "Some Info Is Missing", Toast.LENGTH_SHORT).show();
                    if (name.isEmpty()) {
                        nameEditText.setError("Name is required");
                    }
                    if (email.isEmpty()) {
                        emailEditText.setError("Email is required");
                    }
                    if (password.isEmpty()) {
                        passwordEditText.setError("Password is required");
                    }
                    if (phoneNumber.isEmpty()) {
                        phoneNumberEditText.setError("Phone number is required");
                    }
                }
            }
        });
    }

    private void registerUser(String name, String email, String password, String phoneNumber) {
        if (validateUserInput()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserData(user);
                            Toast.makeText(Activity_SignUp.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Activity_SignUp.this, Activity_Main.class);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "Sign-up failed: ", task.getException());
                            Toast.makeText(Activity_SignUp.this, "Sign Up Failed. Enter valid details and try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(Activity_SignUp.this, "Sign Up Failed. Enter valid details and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateUserInput() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        return checkFullName(name) && checkEmail(email) && checkPassword(password) && checkPhone(phoneNumber);
    }

    private void saveUserData(FirebaseUser user) {
        String userId = user.getUid();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String userType = ((RadioButton) findViewById(userTypeRadioGroup.getCheckedRadioButtonId())).getText().toString().toLowerCase();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("email").setValue(email);
        userRef.child("name").setValue(name);
        userRef.child("phoneNumber").setValue(phoneNumber);
        userRef.child("password").setValue(password);
        userRef.child("userType").setValue(userType);

        Log.d(TAG, "User data saved: Name - " + name + ", Email - " + email + ", Phone - " + phoneNumber + ", Password - " + password + ", UserType - " + userType);
    }

    public boolean checkFullName(String fullName) {
        String regex = "^(?!.{51})[a-zA-Z-]+(?: [a-zA-Z]+(?: [a-zA-Z-]+)?)?$";
        return fullName.matches(regex) || showToast("Invalid Name, Try Again");
    }

    public boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(regex).matcher(email).matches() || showToast("Invalid Email, Please Enter a Valid Email Address");
    }

    public boolean checkPassword(String password) {
        String regex = "^[a-zA-Z0-9]{6,20}$";
        return password.matches(regex) || showToast("Invalid Password. Password must contain at least 6 characters.");
    }

    public boolean checkPhone(String phoneNumber) {
        String regex = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
        return phoneNumber.matches(regex) || showToast("Invalid Phone Number. Please enter a valid phone number.");
    }

    private boolean showToast(String message) {
        Toast.makeText(Activity_SignUp.this, message, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void findViews() {
        goBackButton = findViewById(R.id.signup_BTN_goBack);
        nameEditText = findViewById(R.id.signup_BOX_name);
        emailEditText = findViewById(R.id.signup_BOX_email);
        passwordEditText = findViewById(R.id.signup_BOX_password);
        phoneNumberEditText = findViewById(R.id.signup_BOX_phoneNumber);
        submitButton = findViewById(R.id.signup_BTN_submit);
        userTypeRadioGroup = findViewById(R.id.signup_radioGroup_userType);
    }
}
