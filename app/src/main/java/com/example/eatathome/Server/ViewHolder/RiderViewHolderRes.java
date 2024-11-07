package com.example.eatathome.Server.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.Interface.ItemClickListener;
import com.example.eatathome.R;

public class RiderViewHolderRes extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView shipper_name, shipper_phone, shipper_password;
    public Button btn_edit, btn_remove;
    private ItemClickListener itemClickListener;

    public RiderViewHolderRes(View itemView) {
        super(itemView);

        shipper_name = itemView.findViewById(R.id.shipper_name);
        shipper_phone = itemView.findViewById(R.id.shipper_phone);
        shipper_password = itemView.findViewById(R.id.shipper_password);

        btn_edit = itemView.findViewById(R.id.btnEditShipper);
        btn_remove = itemView.findViewById(R.id.btnDeleteShipper);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
