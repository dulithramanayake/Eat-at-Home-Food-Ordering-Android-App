package com.example.eatathome.Rider.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.R;

public class OrderViewHolderRider extends RecyclerView.ViewHolder{

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtOrderDate, txtOrderName, txtOrderPrice;
    public Button btnShipping;

    public OrderViewHolderRider(View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderDate = itemView.findViewById(R.id.order_date);
        txtOrderName = itemView.findViewById(R.id.order_name);
        txtOrderPrice = itemView.findViewById(R.id.order_price);

        btnShipping = itemView.findViewById(R.id.btnShipping);

    }
}
