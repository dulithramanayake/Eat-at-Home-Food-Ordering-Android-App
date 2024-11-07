package com.example.eatathome.Client.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.Interface.ItemClickListener;
import com.example.eatathome.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener{

    public TextView restaurantName, restaurantLocation;
    public ImageView restaurantImage;

    private ItemClickListener itemClickListener;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);

        restaurantName = itemView.findViewById(R.id.restaurant_name);
        restaurantImage = itemView.findViewById(R.id.restaurant_image);
        restaurantLocation = itemView.findViewById(R.id.restaurant_location);
        itemView.setOnClickListener(this);

    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

}