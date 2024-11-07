package com.example.eatathome.Server.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.R;
import com.example.eatathome.Server.Remote.APIServiceRes;
import com.example.eatathome.Server.Constant.ConstantRes;
import com.example.eatathome.Server.Models.MyResponseRes;
import com.example.eatathome.Server.Models.NotificationRes;
import com.example.eatathome.Server.Models.RequestRes;
import com.example.eatathome.Server.Models.SenderRes;
import com.example.eatathome.Server.Models.TokenRes;
import com.example.eatathome.Server.ViewHolder.OrderViewHolderRes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusActivityRes extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<RequestRes, OrderViewHolderRes> adapter;

    FirebaseDatabase db;
    DatabaseReference requests;
    DatabaseReference shippers;

    MaterialSpinner spinner, shipperSpinner;

    APIServiceRes mService;


    String restId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_res);

        //Firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        restId = ConstantRes.currentUser.getRestaurantId().trim();

        //Init service
        mService = ConstantRes.getFCMClient();

        //Init
        recyclerView = findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(restId);
    }

    private void loadOrders(String restId) {


        Query getOrderByAdmin = requests.orderByChild("restaurantId").equalTo(restId);

        FirebaseRecyclerOptions<RequestRes> options = new FirebaseRecyclerOptions.Builder<RequestRes>()
                .setQuery(getOrderByAdmin, RequestRes.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<RequestRes, OrderViewHolderRes>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolderRes viewHolder, final int position, @NonNull final RequestRes model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderStatus.setText(ConstantRes.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderDate.setText(ConstantRes.getDate(Long.parseLong(Objects.requireNonNull(adapter.getRef(position).getKey()))));
                viewHolder.txtOrderName.setText(model.getName());
                viewHolder.txtOrderPrice.setText(model.getTotal());

                //New event Button
                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }
                });

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfirmDeleteDialog(adapter.getRef(position).getKey());

                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent(OrderStatusActivityRes.this, OrderDetailActivity.class);
                        ConstantRes.currentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingOrder = new Intent(OrderStatusActivityRes.this, TrackingOrderActivityRes.class);
                        ConstantRes.currentRequest = model;
                        startActivity(trackingOrder);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolderRes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout_admin, parent, false);
                return new OrderViewHolderRes(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void showUpdateDialog(String key, final RequestRes item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatusActivityRes.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please Choose Status");
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);
        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed", "Preparing Orders", "Shipping", "Delivered");
        shipperSpinner = view.findViewById(R.id.shipperSpinner);
        //load all shipper to spinner
        shippers = db.getReference("Shippers");
        final Query loadAllShipper = shippers.orderByChild("restaurantId").equalTo(restId);
        final List<String> shipperList = new ArrayList<>();
        loadAllShipper.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot shipperSnapshot : dataSnapshot.getChildren())
                    shipperList.add(shipperSnapshot.getKey());
                shipperSpinner.setItems(shipperList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        alertDialog.setView(view);
        final String localKey = key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                if (item.getStatus().equals("2")) {
                    //copy item to table "OrdersNeedShip"
                    FirebaseDatabase.getInstance().getReference(ConstantRes.ORDER_NEED_SHIP_TABLE)
                            .child(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString())
                            .child(localKey)
                            .setValue(item);

                    requests.child(localKey).setValue(item);
                    adapter.notifyDataSetChanged(); //add to update item size

                    sendOrderStatusToUser(localKey, item);
                    sendOrderShipRequestToShipper(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString(), item);
                } else {
                    requests.child(localKey).setValue(item);
                    adapter.notifyDataSetChanged(); //add to update item size

                    sendOrderStatusToUser(localKey, item);
                }

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

    private void sendOrderShipRequestToShipper(String shipperPhone, RequestRes item) {

        DatabaseReference tokens = db.getReference("Tokens");

        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            TokenRes token = dataSnapshot.getValue(TokenRes.class);

                            //make raw payload
                            NotificationRes notification = new NotificationRes("EatatHome", "You have new order need ship");
                            SenderRes content = new SenderRes(token.getToken(), notification);

                            mService.sendNotification(content).enqueue(new Callback<MyResponseRes>() {
                                @Override
                                public void onResponse(Call<MyResponseRes> call, Response<MyResponseRes> response) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(OrderStatusActivityRes.this, "Sent to Shippers!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(OrderStatusActivityRes.this, ""
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponseRes> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void ConfirmDeleteDialog(final String key) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatusActivityRes.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Confirm Delete?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_delete_layout, null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requests.child(key).removeValue();
                Toast.makeText(OrderStatusActivityRes.this, "Order Deleted Successfully!", Toast.LENGTH_SHORT).show();
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

    private void sendOrderStatusToUser(final String key, final RequestRes item) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.child(item.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            TokenRes token = dataSnapshot.getValue(TokenRes.class);

                            //make raw payload
                            NotificationRes notification = new NotificationRes("EatatHome", "Your order " + key + " was updated");
                            SenderRes content = new SenderRes(token.getToken(), notification);

                            mService.sendNotification(content).enqueue(new Callback<MyResponseRes>() {
                                @Override
                                public void onResponse(Call<MyResponseRes> call, Response<MyResponseRes> response) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(OrderStatusActivityRes.this, "Order was updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(OrderStatusActivityRes.this, "Order was updated!"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }


                                @Override
                                public void onFailure(Call<MyResponseRes> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}