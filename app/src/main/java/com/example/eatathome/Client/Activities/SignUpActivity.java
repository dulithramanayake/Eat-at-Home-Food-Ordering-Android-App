package com.example.eatathome.Client.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.Client.Model.User;
import com.example.eatathome.R;
import com.example.eatathome.databinding.ActivitySignUpBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySignUpBinding activitySignUpBinding;
    String phoneNumber, name, password, secureCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        activitySignUpBinding.btnSignUp.setOnClickListener(this);
        activitySignUpBinding.txtSignIn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view == activitySignUpBinding.btnSignUp) {

            buttonSignUp();

        } else if (view == activitySignUpBinding.txtSignIn) {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        }

    }

    public void buttonSignUp() {
        if (!validatePhoneNumber() | !validateName() | !validatePassword() | !validateSecureCode()) {
            Toast.makeText(this, "Please enter correct info", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toast.makeText(this, "Password length must be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(secureCode)) {
            Toast.makeText(this, "Password and Secure Code do not match", Toast.LENGTH_SHORT).show();
        } else {
            final String enteredPhoneNumber = phoneNumber;
            final String enteredPassword = password;
            final String enteredSecureCode = secureCode;
            // Check if the user already exists
            final DatabaseReference table_users = FirebaseDatabase.getInstance().getReference("User");
            table_users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(enteredPhoneNumber).exists()) {
                        User user = dataSnapshot.child(enteredPhoneNumber).getValue(User.class);
                        if (user != null && enteredPassword.equals(user.getPassword()) && enteredSecureCode.equals(user.getSecureCode())) {
                            // Registration is valid
                            // Add your sign-up code here
                            // ...
                            Toast.makeText(SignUpActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Password and Secure Code do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        User clientUsers = new User(name, password, phoneNumber, secureCode, null, null);
                        table_users.child(phoneNumber).setValue(clientUsers);
                        Toast.makeText(SignUpActivity.this, "Sign Up Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }



    private boolean validatePhoneNumber() {
        phoneNumber = activitySignUpBinding.etPhoneNumber.getText().toString();
        if (phoneNumber.isEmpty()) {
            activitySignUpBinding.etPhoneNumber.setError("Phone Number is required. Can't be empty.");
            return false;
        } else if (phoneNumber.length() < 10) {
            activitySignUpBinding.etPhoneNumber.setError("Phone Number cannot less than 10 digits!");
            return false;
        } else if (phoneNumber.length() > 11) {
            activitySignUpBinding.etPhoneNumber.setError("Phone Number cannot exceed 11 digits!");
            return false;
        } else {
            activitySignUpBinding.etPhoneNumber.setError(null);
            return true;
        }

    }

    private boolean validateSecureCode() {

        secureCode = activitySignUpBinding.etSecureCode.getText().toString();

        if (secureCode.isEmpty()) {
            activitySignUpBinding.etSecureCode.setError("SecureCode is required. Can't be empty.");
            return false;
        } else if (secureCode.length() < 8) {
            activitySignUpBinding.etSecureCode.setError("SecureCode length short. Minimum 8 characters required.");
            return true;
        } else {
            activitySignUpBinding.etSecureCode.setError(null);
            return true;
        }
    }

    private boolean validateName() {

        name = activitySignUpBinding.etName.getText().toString();

        if (name.isEmpty()) {
            activitySignUpBinding.etName.setError("UserName is required. Can't be empty.");
            return false;
        } else {
            activitySignUpBinding.etName.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        password = activitySignUpBinding.etPassword.getText().toString();

        if (password.isEmpty()) {
            activitySignUpBinding.etPassword.setError("Password is required. Can't be empty.");
            return false;
        } else if (password.length() < 8) {
            activitySignUpBinding.etPassword.setError("Password length short. Minimum 8 characters required.");
            return true;
        } else {
            activitySignUpBinding.etPassword.setError(null);
            return true;
        }
    }


}