package com.example.eatathome.Client.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.eatathome.Administrator.Activities.AddRestaurantsAdminActivity;
import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.Client.Model.User;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;
import com.example.eatathome.Server.Activities.RestaurantMainActivity;
import com.example.eatathome.databinding.ActivitySignInBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    public ActivitySignInBinding activitySignInBinding;
    String phoneNumber, password;

    FirebaseDatabase database;
    DatabaseReference table_users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        activitySignInBinding.btnSignIn.setOnClickListener(this);
        activitySignInBinding.txtSignUp.setOnClickListener(this);
        activitySignInBinding.chkRemember.setOnClickListener(this);
        activitySignInBinding.tvForgotPassword.setOnClickListener(this);

        //init paper
        Paper.init(this);

        //check remember
        String user = Paper.book().read(Constant.USER_KEY);
        String password = Paper.book().read(Constant.PASSWORD_KEY);
        if (user != null && password != null) {
            if (!user.isEmpty() && !password.isEmpty())
                login(user, password);

        }

    }

    private void login(final String user, final String password) {

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("User");

        if (Constant.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();

            table_users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //check if user not exit in database
                    if (dataSnapshot.child(user).exists()) {
                        //Get User Information
                        mDialog.dismiss();
                        User clientUsers = dataSnapshot.child(user).getValue(User.class);
                        clientUsers.setPhone(user);
                        if (clientUsers.getPassword().equals(password)) {
                            Toast.makeText(SignInActivity.this, "Sign In Successfully !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, RestaurantListActivity.class);
                            Constant.currentUser = clientUsers;
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(SignInActivity.this, "Wrong Password!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "User not exists ", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == activitySignInBinding.btnSignIn) {
            buttonSignIn();

        } else if (view == activitySignInBinding.txtSignUp) {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        } else if (view == activitySignInBinding.tvForgotPassword) {
            showForgotPasswordDialog();
        }

    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security);

        final TextInputEditText edtPhone = forgot_view.findViewById(R.id.et_phone_number);
        final TextInputEditText edtSecureCode = forgot_view.findViewById(R.id.et_secure_code);

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("User");

        final String phone = edtPhone.getText().toString();
        final String secureCode = edtSecureCode.getText().toString();

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                table_users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(phone).getValue(User.class);

                        if (user != null && user.getSecureCode() != null && user.getSecureCode().equals(secureCode)) {
                            Toast.makeText(SignInActivity.this, "Your Password " + user.getPassword(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignInActivity.this, "Wrong Secure Code", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    public void buttonSignIn() {
        if (!validatePhoneNumber() | !validatePassword()) {
            Toast.makeText(this, "Please enter correct info ", Toast.LENGTH_SHORT).show();
        } else {
            database = FirebaseDatabase.getInstance();
            table_users = database.getReference("User");
            if (Constant.isConnectedToInternet(getBaseContext())) {
                //save user & password
                if (activitySignInBinding.chkRemember.isChecked()) {
                    Paper.book().write(Constant.USER_KEY, phoneNumber);
                    Paper.book().write(Constant.PASSWORD_KEY, password);
                }
                final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();
                table_users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check if user not exit in database
                        if (dataSnapshot.child(activitySignInBinding.etPhoneNumber.getText().toString()).exists()) {
                            //Get User Information
                            mDialog.dismiss();
                            User clientUsers = dataSnapshot.child(activitySignInBinding.etPhoneNumber.getText().toString()).getValue(User.class);
                            clientUsers.setPhone(activitySignInBinding.etPhoneNumber.getText().toString());
                            if (clientUsers.getPassword().equals(activitySignInBinding.etPassword.getText().toString())) {
                                Toast.makeText(SignInActivity.this, "Sign In Successfully !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, RestaurantListActivity.class);
                                Constant.currentUser = clientUsers;
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "Wrong Password!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "User not exists ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }


    }

    private boolean validatePhoneNumber() {
        phoneNumber = activitySignInBinding.etPhoneNumber.getText().toString();
        if (phoneNumber.isEmpty()) {
            activitySignInBinding.etPhoneNumber.setError("Phone Number is required. Can't be empty.");
            return false;
        } else if (phoneNumber.length() < 10) {
            activitySignInBinding.etPhoneNumber.setError("Phone Number cannot less than 10 digits!");
            return false;
        } else if (phoneNumber.length() > 11) {
            activitySignInBinding.etPhoneNumber.setError("Phone Number cannot exceed 11 digits!");
            return false;
        } else {
            activitySignInBinding.etPhoneNumber.setError(null);
            return true;
        }

    }

    private boolean validatePassword() {

        password = activitySignInBinding.etPassword.getText().toString();

        if (password.isEmpty()) {
            activitySignInBinding.etPassword.setError("Password is required. Can't be empty.");
            return false;
        } else if (password.length() < 8) {
            activitySignInBinding.etPassword.setError("Password length short. Minimum 8 characters required.");
            return true;
        } else {
            activitySignInBinding.etPassword.setError(null);
            return true;
        }
    }


}