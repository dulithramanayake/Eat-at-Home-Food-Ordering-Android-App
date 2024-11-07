package com.example.eatathome.Administrator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;
import com.example.eatathome.Administrator.ConstantAdmin;
import com.example.eatathome.Administrator.Model.UserAdministrator;
import com.example.eatathome.R;
import com.example.eatathome.Rider.Activities.SignInAsRiderActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivityAppAdmin extends AppCompatActivity {

    TextInputEditText edtPhone, edtPassword;
    MaterialButton btnSignInAsAdmin;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_app_admin);

        // Initialize UI elements
        edtPhone = findViewById(R.id.et_phone_number);
        edtPassword = findViewById(R.id.et_password);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPassword.setTransformationMethod(new PasswordTransformationMethod());

        btnSignInAsAdmin = findViewById(R.id.btn_sign_in);

        // Initialize Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Administrator");

        btnSignInAsAdmin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String phone = edtPhone.getText().toString();
                String password = edtPassword.getText().toString();

                if (phone.isEmpty() || password.isEmpty()) {
                    // Check for empty fields
                    Toast.makeText(SignInActivityAppAdmin.this, "Please fill your phone number and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Call the sign-in function
                    signInAdmin(phone, password);
                }
            }
        });
    }

    private void signInAdmin(String phone, String password) {


        final ProgressDialog mDialog = new ProgressDialog(SignInActivityAppAdmin.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(localPhone).exists()) {

                    mDialog.dismiss();
                    UserAdministrator user = dataSnapshot.child(localPhone).getValue(UserAdministrator.class);
                    user.setPhone(localPhone);

                    if (user.getPassword().equals(localPassword)) {

                        // Successful login
                        Intent login = new Intent(SignInActivityAppAdmin.this, AddRestaurantsActivity.class);
                        ConstantAdmin.currentUser = user;
                        startActivity(login);
                        finish();
                    } else
                        // Password is incorrect
                        Toast.makeText(SignInActivityAppAdmin.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                } else {
                    // User does not exist in the database
                    mDialog.dismiss();
                    Toast.makeText(SignInActivityAppAdmin.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors

            }
        });
    }

    @Override
    public void onBackPressed() {
        // Your custom logic here
        super.onBackPressed(); // This line is important if you want to keep the default back button behavior
    }


}