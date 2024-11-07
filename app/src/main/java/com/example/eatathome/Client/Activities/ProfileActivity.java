package com.example.eatathome.Client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.Client.Model.User;
import com.example.eatathome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {


    public TextView profile_name,  profile_phone, profile_address;
    Button btnUpdateUsername, btnUpdateHomeAddress,  btnSelect, btnUpload;
    CircularImageView profile_pic;
    RelativeLayout rootLayout;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    User newUser;
    FirebaseDatabase db;
    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseDatabase.getInstance();

        user = db.getReference("User").child(Constant.currentUser.getPhone());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        loadProfile();

        btnUpdateHomeAddress = findViewById(R.id.btn_updateAddress);
        btnUpdateUsername = findViewById(R.id.btn_updateUsername);
        profile_pic = (CircularImageView) findViewById(R.id.profile_picture);
        profile_pic.setBorderColor(getResources().getColor(R.color.colorAccent));
        profile_pic.setBorderWidth(2);

        btnUpdateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUsername = new Intent(ProfileActivity.this, UpdateUsernameActivity.class);
                startActivity(updateUsername);
            }
        });

        btnUpdateHomeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateAddress = new Intent(ProfileActivity.this, UpdateAddressActivity.class);
                startActivity(updateAddress);
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeProfileDialog();
            }
        });
    }


    private void showChangeProfileDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Change Profile Picture");
        LayoutInflater inflater = this.getLayoutInflater();
        View change_profile = inflater.inflate(R.layout.change_profile_dialog,null);
        btnSelect = change_profile.findViewById(R.id.btn_select);
        btnUpload = change_profile.findViewById(R.id.btn_upload);
        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //let users select image from gallery and save URL of this image
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //upload image
                uploadImage();
            }
        });
        alertDialog.setView(change_profile);
        alertDialog.setIcon(R.drawable.ic_baseline_person_24);
        //set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //change profile
                user.child("images").setValue(newUser.getImage());
                Picasso.get().load(saveUri).into(profile_pic);
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();

    }


    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Constant.PICK_IMAGE_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constant.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null
                && data.getData()!= null){

            saveUri = data.getData();
            btnSelect.setText("Image Selected!");
        }
    }

    private void uploadImage() {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();
            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //set value for newCategory if image upload and we can get download link
                            newUser = new User();
                            newUser.setImage(uri.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() /taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploading " + progress +" % ");
                        }
                    });
        }
    }

    public void loadProfile(){

        ValueEventListener imageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String images = dataSnapshot.child("images").getValue(String.class);

                Picasso.get().load(images).into(profile_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        profile_name =  findViewById(R.id.profile_name);
        profile_phone =  findViewById(R.id.profile_phone);
        profile_address = findViewById(R.id.profile_address);
        profile_name.setText(Constant.currentUser.getName());
        profile_address.setText(Constant.currentUser.getHomeAddress());
        profile_phone.setText(Constant.currentUser.getPhone());
        user.addValueEventListener(imageListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }
}