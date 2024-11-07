package com.example.eatathome.Client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateAddressActivity extends AppCompatActivity {

    MaterialButton confirm;
    TextInputEditText address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_address);

        address = findViewById(R.id.et_edtHomeAddress);
        confirm = findViewById(R.id.btnConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }


    private void showConfirmDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateAddressActivity.this);
        alertDialog.setTitle("Confirm Update Address?");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_signout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_signout);
        alertDialog.setIcon(R.drawable.ic_baseline_priority_high_24);
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(address.getText())) {
                    Toast.makeText(UpdateAddressActivity.this, "Home Address is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    Constant.currentUser.setHomeAddress(address.getText().toString());
                    FirebaseDatabase.getInstance().getReference("User")
                            .child(Constant.currentUser.getPhone())
                            .setValue(Constant.currentUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(UpdateAddressActivity.this, "Update Address Successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateAddressActivity.this, "Home Address Cannot Update!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}