package com.example.eatathome.Server.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eatathome.R;
import com.example.eatathome.Server.Constant.ConstantRes;
import com.example.eatathome.Server.Models.RiderRes;
import com.example.eatathome.Server.ViewHolder.RiderViewHolderRes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class RiderManagementActivityRes extends AppCompatActivity {

    FloatingActionButton fabAdd;
    FirebaseDatabase database;
    DatabaseReference shippers;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    String restId;

    FirebaseRecyclerAdapter<RiderRes, RiderViewHolderRes> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);

        restId = ConstantRes.currentUser.getRestaurantId().trim();

        //Init View
        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateShipperLayout();
            }
        });

        recyclerView = findViewById(R.id.recycler_shippers);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Database
        database = FirebaseDatabase.getInstance();
        shippers = database.getReference(ConstantRes.SHIPPER_TABLE);

        //load all shipper
        loadAllShipper(restId);
    }


    private void loadAllShipper(String restId) {

        Query loadAllShipper = shippers.orderByChild("restaurantId").equalTo(restId);

        FirebaseRecyclerOptions<RiderRes> allShipper = new FirebaseRecyclerOptions.Builder<RiderRes>()
                .setQuery(loadAllShipper, RiderRes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<RiderRes, RiderViewHolderRes>(allShipper) {
            @Override
            protected void onBindViewHolder(@NonNull RiderViewHolderRes holder, final int position, @NonNull final RiderRes model) {
                holder.shipper_phone.setText(model.getPhone());
                holder.shipper_name.setText(model.getName());
                holder.shipper_password.setText(model.getPassword());

                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(adapter.getRef(position).getKey(), model);
                    }
                });

                holder.btn_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteAccountDialog(adapter.getRef(position).getKey());
                    }
                });
            }

            @NonNull
            @Override
            public RiderViewHolderRes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipper_layout, parent, false);
                return new RiderViewHolderRes(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void showDeleteAccountDialog(final String key) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RiderManagementActivityRes.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Confirm Delete?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_delete_layout, null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                shippers.child(key).removeValue();
                Toast.makeText(RiderManagementActivityRes.this, "Account Delete Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
        adapter.notifyDataSetChanged();
    }


    private void showEditDialog(String key, RiderRes model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RiderManagementActivityRes.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("UPDATE SHIPPER ACCOUNT");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_shipper = inflater.inflate(R.layout.create_shipper_layout, null);

        final TextInputEditText shipper_phone = layout_shipper.findViewById(R.id.et_create_shipper_phone);
        final TextInputEditText shipper_name = layout_shipper.findViewById(R.id.et_create_shipper_name);
        final TextInputEditText shipper_password = layout_shipper.findViewById(R.id.et_create_shipper_password);
        shipper_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        shipper_password.setTransformationMethod(new PasswordTransformationMethod());

        //set data
        shipper_name.setText(model.getName());
        shipper_phone.setText(model.getPhone());
        shipper_phone.setEnabled(false);
        shipper_password.setText(model.getPassword());

        alertDialog.setView(layout_shipper);
        alertDialog.setIcon(R.drawable.ic_create_black_24dp);


        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //create account

                if (TextUtils.isEmpty(shipper_phone.getText())) {
                    Toast.makeText(RiderManagementActivityRes.this, "Phone Number is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(shipper_name.getText())) {
                    Toast.makeText(RiderManagementActivityRes.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(shipper_password.getText())) {
                    Toast.makeText(RiderManagementActivityRes.this, "Password is Empty!", Toast.LENGTH_SHORT).show();
                } else if (shipper_phone.getText().length() < 10) {
                    Toast.makeText(RiderManagementActivityRes.this, "Phone Number cannot less than 10 digts!", Toast.LENGTH_SHORT).show();
                } else if (shipper_phone.getText().length() > 11) {
                    Toast.makeText(RiderManagementActivityRes.this, "Phone Number cannot exceed 11 digits!", Toast.LENGTH_SHORT).show();
                } else {

                    Map<String, Object> update = new HashMap<>();
                    update.put("name", shipper_name.getText().toString());
                    update.put("phone", shipper_phone.getText().toString());
                    update.put("password", shipper_password.getText().toString());

                    shippers.child(shipper_phone.getText().toString())
                            .updateChildren(update)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RiderManagementActivityRes.this, "Shipper Updated Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RiderManagementActivityRes.this, "Failed to Update Account!", Toast.LENGTH_SHORT).show();
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


    private void showCreateShipperLayout() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RiderManagementActivityRes.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("CREATE SHIPPER ACCOUNT");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_shipper = inflater.inflate(R.layout.create_shipper_layout, null);

        final TextInputEditText shipper_phone = layout_shipper.findViewById(R.id.et_create_shipper_phone);
        final TextInputEditText shipper_name = layout_shipper.findViewById(R.id.et_create_shipper_name);
        final TextInputEditText shipper_password = layout_shipper.findViewById(R.id.et_create_shipper_password);
        shipper_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        shipper_password.setTransformationMethod(new PasswordTransformationMethod());

        alertDialog.setView(layout_shipper);
        alertDialog.setIcon(R.drawable.ic_create_black_24dp);


        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //create account

                if (TextUtils.isEmpty(shipper_phone.getText())) {
                    Toast.makeText(RiderManagementActivityRes.this, "Phone Number is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(shipper_name.getText())) {
                    Toast.makeText(RiderManagementActivityRes.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(shipper_password.getText())) {
                    Toast.makeText(RiderManagementActivityRes.this, "Password is Empty!", Toast.LENGTH_SHORT).show();
                } else if (shipper_phone.getText().length() < 10) {
                    Toast.makeText(RiderManagementActivityRes.this, "Phone Number cannot less than 10 digts!", Toast.LENGTH_SHORT).show();
                } else if (shipper_phone.getText().length() > 11) {
                    Toast.makeText(RiderManagementActivityRes.this, "Phone Number cannot exceed 11 digits!", Toast.LENGTH_SHORT).show();
                } else {
                    RiderRes shipper = new RiderRes();
                    shipper.setName(shipper_name.getText().toString());
                    shipper.setPassword(shipper_password.getText().toString());
                    shipper.setPhone(shipper_phone.getText().toString());
                    shipper.setIsadmin("false");
                    shipper.setIsstaff("true");
                    shipper.setRestaurantId(restId);

                    shippers.child(shipper_phone.getText().toString())
                            .setValue(shipper)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RiderManagementActivityRes.this, "Shipper Created Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RiderManagementActivityRes.this, "Failed to Create Account!", Toast.LENGTH_SHORT).show();
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