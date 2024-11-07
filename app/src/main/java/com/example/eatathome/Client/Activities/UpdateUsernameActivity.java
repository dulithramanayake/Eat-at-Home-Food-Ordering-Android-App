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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateUsernameActivity extends AppCompatActivity {

    MaterialButton confirm;
    TextInputEditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_username);


        username = findViewById(R.id.et_edtUsername);
        confirm = findViewById(R.id.btnConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }


    private void showConfirmDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateUsernameActivity.this);
        alertDialog.setTitle("Confirm Update Username?");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_signout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_signout);
        alertDialog.setIcon(R.drawable.ic_baseline_priority_high_24);

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (TextUtils.isEmpty(username.getText())) {
                    Toast.makeText(UpdateUsernameActivity.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                } else {

                    Constant.currentUser.setName(username.getText().toString());

                    FirebaseDatabase.getInstance().getReference("User")
                            .child(Constant.currentUser.getPhone())
                            .setValue(Constant.currentUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                        Toast.makeText(UpdateUsernameActivity.this, "Username was updated!", Toast.LENGTH_SHORT).show();
                                    finish();

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