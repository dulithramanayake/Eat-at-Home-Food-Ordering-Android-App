package com.example.eatathome.Administrator.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.Interface.ItemClickListener;
import com.example.eatathome.R;

public class AddRestaurantsAdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView admin_name, admin_phone, admin_password, admin_id;
    public Button btn_edit, btn_remove;
    private ItemClickListener itemClickListener;

    public AddRestaurantsAdminViewHolder(View itemView) {
        super(itemView);

        admin_name = itemView.findViewById(R.id.admin_name);
        admin_phone = itemView.findViewById(R.id.admin_phone);
        admin_password = itemView.findViewById(R.id.admin_password);
        admin_id = itemView.findViewById(R.id.admin_id);

        btn_edit = itemView.findViewById(R.id.btnEditAdmin);
        btn_remove = itemView.findViewById(R.id.btnDeleteAdmin);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
