package com.example.eatathome.Administrator.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.Administrator.ConstantAdmin;
import com.example.eatathome.Interface.ItemClickListener;
import com.example.eatathome.R;

public class AddRestaurantsViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtMenuName, txtLocation;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    public AddRestaurantsViewHolder(View itemView) {
        super(itemView);

        txtMenuName = itemView.findViewById(R.id.restaurant_name);
        txtLocation = itemView.findViewById(R.id.restaurant_location);
        imageView = itemView.findViewById(R.id.restaurant_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {

        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

//        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select the action");
        menu.add(0, 0, getAdapterPosition(), ConstantAdmin.UPDATE);
        menu.add(0, 1, getAdapterPosition(), ConstantAdmin.DELETE);

    }
}