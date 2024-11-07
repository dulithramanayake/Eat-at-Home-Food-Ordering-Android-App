package com.example.eatathome.Server.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.R;

public class UserViewHolderRes extends RecyclerView.ViewHolder {

    public TextView staffName, staffPassword, staffRole;
    public Button btnDeleteAccount, btnEditAccount;

    public UserViewHolderRes(View itemView) {
        super(itemView);

        staffName = itemView.findViewById(R.id.staff_name);
        staffPassword = itemView.findViewById(R.id.staff_password);

        staffRole = itemView.findViewById(R.id.staff_role);
        btnEditAccount = itemView.findViewById(R.id.btnEditStaff);
        btnDeleteAccount = itemView.findViewById(R.id.btnDeleteStaff);
    }
}
