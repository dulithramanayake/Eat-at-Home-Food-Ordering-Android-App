package com.example.eatathome.Rider.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import com.example.eatathome.R;
import com.example.eatathome.Rider.Constant.ConstantRider;
import com.example.eatathome.Rider.Model.UserRider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInAsRiderActivity extends AppCompatActivity {

    MaterialButton btnSignIn;
    TextInputEditText etPhone, etPassword;

    FirebaseDatabase database;
    DatabaseReference rider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_as_rider);

        etPhone = findViewById(R.id.et_rider_phone_number);
        etPassword = findViewById(R.id.et_rider_password);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        //Init firebase
        database = FirebaseDatabase.getInstance();
        rider = database.getReference(ConstantRider.SHIPPER_TABLE);

        btnSignIn = findViewById(R.id.btn_rider_sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String phone = etPhone.getText().toString();
                String password = etPassword.getText().toString();

                if (phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInAsRiderActivity.this, "Please fill your phone number and password", Toast.LENGTH_SHORT).show();
                } else {
                    login(phone, password);
                }
            }
        });

    }


    private void login(String phone, final String password) {

        final ProgressDialog mDialog = new ProgressDialog(SignInAsRiderActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        rider.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mDialog.dismiss();
                    UserRider rider = dataSnapshot.getValue(UserRider.class);
                    if (rider.getPassword().equals(password)) {
                        //Login success
                        startActivity(new Intent(SignInAsRiderActivity.this, HomeActivityRider.class));
                        ConstantRider.currentRider = rider;
                        finish();
                    } else if (rider.getPhone() == null) {
                        Toast.makeText(SignInAsRiderActivity.this, "Your Phone Number is Empty!", Toast.LENGTH_SHORT).show();
                    } else if (rider.getPassword() == null) {
                        Toast.makeText(SignInAsRiderActivity.this, "Your Password is Empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignInAsRiderActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SignInAsRiderActivity.this, "User not exists in Database!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        // Your custom logic here
        super.onBackPressed(); // This line is important if you want to keep the default back button behavior
    }
}