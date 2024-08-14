package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.Helpers.Singing_Helper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Activity_SignUp extends AppCompatActivity {

    private Button submitButton;
    private Button goBackButton;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;

    private RadioGroup userTypeRadioGroup;

    private FirebaseAuth mAuth;

    private Singing_Helper singingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        singingHelper = new Singing_Helper();

        findViews();
        allButtonListeners();
    }

    private void allButtonListeners() {
        goBackButton.setOnClickListener(v -> finish());

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String userType = ((RadioButton) findViewById(userTypeRadioGroup.getCheckedRadioButtonId())).getText().toString().toLowerCase();

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneNumber.isEmpty()) {
                registerUser(name, email, password, phoneNumber, userType);
            } else {
                GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Some Info Is Missing");
            }
        });
    }

    private void registerUser(String name, String email, String password, String phoneNumber, String userType) {
        if (validateUserInput()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            singingHelper.saveUserData(Activity_SignUp.this, user, name, email, phoneNumber, password, userType);
                        } else {
                            GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Sign Up Failed. Enter valid details and try again");
                        }
                    });
        } else {
            GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Sign Up Failed. Enter valid details and try again");
        }
    }

    private boolean validateUserInput() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        return checkFullName(name) && checkEmail(email) && checkPassword(password) && checkPhone(phoneNumber);
    }

    private boolean checkFullName(String fullName) {
        String regex = "^(?!.{51})[a-zA-Z-]+(?: [a-zA-Z]+(?: [a-zA-Z-]+)?)?$";
        return fullName.matches(regex) || GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Invalid Name, Try Again");
    }

    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex) || GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Invalid Email, Please Enter a Valid Email Address");
    }

    private boolean checkPassword(String password) {
        String regex = "^[a-zA-Z0-9]{6,20}$";
        return password.matches(regex) || GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Invalid Password. Password must contain at least 6 characters.");
    }

    private boolean checkPhone(String phoneNumber) {
        String regex = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
        return phoneNumber.matches(regex) || GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Invalid Phone Number. Please enter a valid phone number.");
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
