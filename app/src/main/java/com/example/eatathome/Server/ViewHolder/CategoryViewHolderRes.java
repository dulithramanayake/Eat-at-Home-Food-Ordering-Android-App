package com.example.eatathome.Server.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.Interface.ItemClickListener;
import com.example.eatathome.R;
import com.example.eatathome.Server.Constant.ConstantRes;

public class CategoryViewHolderRes extends RecyclerView.ViewHolder implements
        View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    public CategoryViewHolderRes(View itemView) {
        super(itemView);

        txtMenuName =  itemView.findViewById(R.id.menu_name);
        imageView =  itemView.findViewById(R.id.menu_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {

        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select the action");
        menu.add(0, 0, getAdapterPosition(), ConstantRes.UPDATE);
        menu.add(0, 1, getAdapterPosition(), ConstantRes.DELETE);

    }
}