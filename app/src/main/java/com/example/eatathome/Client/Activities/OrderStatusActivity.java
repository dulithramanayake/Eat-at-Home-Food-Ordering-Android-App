package com.example.eatathome.Client.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.Client.Model.Request;
import com.example.eatathome.Client.ViewHolder.OrderViewHolder;
import com.example.eatathome.R;
import com.example.eatathome.Client.Constant.Constant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class OrderStatusActivity extends AppCompatActivity {


    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    private String userID;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);


        //init firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Load menu
        recyclerView = findViewById(R.id.list_order_status);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);


        if (getIntent().getExtras() != null)
            loadOrder(Constant.currentUser.getPhone());
        else {
            if (getIntent().getStringExtra(Constant.PHONE_TEXT) == null)
                loadOrder(Constant.currentUser.getPhone());
            else
                loadOrder(getIntent().getStringExtra(Constant.PHONE_TEXT));

        }
    }

    private void loadOrder(String phone) {
        Query getOrderByUser = requests.orderByChild("phone").equalTo(phone);
        FirebaseRecyclerOptions<Request> orderOptions = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrderByUser, Request.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(orderOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Constant.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderDate.setText(Constant.getDate(Long.parseLong(Objects.requireNonNull(adapter.getRef(position).getKey()))));
                viewHolder.txtOrderName.setText(model.getName());
                viewHolder.txtOrderPrice.setText(model.getTotal());
                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Constant.currentKey = adapter.getRef(position).getKey();
                        if (adapter.getItem(position).getStatus().equals("2"))
                            startActivity(new Intent(OrderStatusActivity.this, TrackingOrderActivity.class));
                        else
                            Toast.makeText(OrderStatusActivity.this, "You cannot track this Order!", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.btnDeleteOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.getItem(position).getStatus().equals("0"))
                            deleteOrder(adapter.getRef(position).getKey());
                        else
                            Toast.makeText(OrderStatusActivity.this, "You cannot delete this Order!", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.btnConfirmShip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.getItem(position).getStatus().equals("3"))
                            ConfirmReceiveOrder(adapter.getRef(position).getKey());
                        else
                            Toast.makeText(OrderStatusActivity.this, "You cannot confirm receive this Order!", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    private void ConfirmReceiveOrder(String key) {

        showConfirmReceiveOrder(key);
    }


    private void deleteOrder(final String key) {

        showConfirmDeleteDialog(key);
    }

    private void showConfirmDeleteDialog(final String key) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatusActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Confirm Delete?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requests.child(key).removeValue();
                Toast.makeText(OrderStatusActivity.this,
                        "Order" + " " +
                                key +
                                " " + "has been deleted", Toast.LENGTH_SHORT).show();

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

    private void showConfirmReceiveOrder(final String key) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatusActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Confirm Receive?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requests.child(key).removeValue();
                Toast.makeText(OrderStatusActivity.this, "Order" + " " +
                        key +
                        " " + "has been confirm received", Toast.LENGTH_SHORT).show();

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