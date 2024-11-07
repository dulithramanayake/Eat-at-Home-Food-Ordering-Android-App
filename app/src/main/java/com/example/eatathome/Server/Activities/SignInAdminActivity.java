package com.example.eatathome.Server.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eatathome.R;
import com.example.eatathome.Server.Constant.ConstantRes;
import com.example.eatathome.Server.Models.UserRes;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInAdminActivity extends AppCompatActivity {

    TextInputEditText edtPhone, edtPassword;
    TextView addRestaurant;
    MaterialButton btnSignInAsAdmin;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_admin);

        edtPhone =  findViewById(R.id.et_phone_number);
        edtPassword =  findViewById(R.id.et_password);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPassword.setTransformationMethod(new PasswordTransformationMethod());

        btnSignInAsAdmin =  findViewById(R.id.btn_sign_in);

        //Init firebase

        addRestaurant = findViewById(R.id.tv_add_restaurant);
        addRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInAdminActivity.this, AboutActivityRes.class);
                startActivity(intent);

            }
        });

        db = FirebaseDatabase.getInstance();
        users = db.getReference("UserRestaurantAdmin");

        btnSignInAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(edtPhone.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    private void signInUser(String phone, String password) {

        final ProgressDialog mDialog = new ProgressDialog(SignInAdminActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(localPhone).exists()) {

                    mDialog.dismiss();
                    UserRes user = dataSnapshot.child(localPhone).getValue(UserRes.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsadmin())) {

                        //If isAdmin = true
                        if (user.getPassword().equals(localPassword)) {

                            Intent login = new Intent(SignInAdminActivity.this, MainAdminActivity.class);
                            ConstantRes.currentUser = user;
                            startActivity(login);
                            finish();
                        } else
                            Toast.makeText(SignInAdminActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SignInAdminActivity.this, "Please login with Staff account", Toast.LENGTH_SHORT).show();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignInAdminActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}