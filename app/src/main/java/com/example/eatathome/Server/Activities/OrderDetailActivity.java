package com.example.eatathome.Server.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.eatathome.R;
import com.example.eatathome.Server.Constant.ConstantRes;
import com.example.eatathome.Server.ViewHolder.OrderDetailAdapterRes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderDetailActivity extends AppCompatActivity {

    TextView order_id, order_phone, order_address, order_total, order_comment;
    String order_id_value = "";
    RecyclerView lstFoods;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference shippers;

    String restId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        database = FirebaseDatabase.getInstance();
        shippers = database.getReference(ConstantRes.SHIPPER_TABLE);
        restId = ConstantRes.currentUser.getRestaurantId().trim();
        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.order_phone);
        order_address = findViewById(R.id.order_address);
        order_total = findViewById(R.id.order_total);
        order_comment = findViewById(R.id.order_comment);
        lstFoods =  findViewById(R.id.lstFoods);
        lstFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);
        if (getIntent() != null) {
            order_id_value = getIntent().getStringExtra("OrderId");
            //set value
            order_id.setText(order_id_value);
            order_phone.setText(ConstantRes.currentRequest.getPhone());
            order_total.setText(ConstantRes.currentRequest.getTotal());
            order_address.setText(ConstantRes.currentRequest.getAddress());
            order_comment.setText(ConstantRes.currentRequest.getComment());
            Query loadAllFood = shippers.orderByChild("restaurantId").equalTo(restId);
            OrderDetailAdapterRes adapter = new OrderDetailAdapterRes(ConstantRes.currentRequest.getFoods());
            adapter.notifyDataSetChanged();
            lstFoods.setAdapter(adapter);
        }
    }

}