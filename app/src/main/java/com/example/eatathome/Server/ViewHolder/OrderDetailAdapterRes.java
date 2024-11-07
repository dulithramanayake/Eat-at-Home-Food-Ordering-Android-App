package com.example.eatathome.Server.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.R;
import com.example.eatathome.Server.Models.OrderRes;
import com.example.eatathome.Server.Models.RestaurantRes;

import java.util.List;

class MyViewHolderRes extends RecyclerView.ViewHolder{

    public TextView name,quantity,price;

    public MyViewHolderRes(View itemView){
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
    }
}

public class OrderDetailAdapterRes extends RecyclerView.Adapter<MyViewHolderRes>{

    List<OrderRes> myOrders;

    public OrderDetailAdapterRes(List<OrderRes> myOrders) {
        this.myOrders = myOrders;

    }

    @NonNull
    @Override
    public MyViewHolderRes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);
        return new MyViewHolderRes(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderRes holder, int position) {
        OrderRes order = myOrders.get(position);
        holder.name.setText(String.format("Name : %s", order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s", order.getQuantity()));
        holder.price.setText(String.format("Price : %s", order.getPrice()));

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
