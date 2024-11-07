package com.example.eatathome.Server.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.R;

public class ShowCommentViewHolderRes extends RecyclerView.ViewHolder {

    public TextView txtUserPhone, txtComment, txtFoodName;
    public RatingBar ratingBar;
    public ImageView commentImage;
    public Button btnDeleteComment;

    public ShowCommentViewHolderRes(View itemView) {
        super(itemView);
        txtComment = itemView.findViewById(R.id.comment);
        txtUserPhone = itemView.findViewById(R.id.comment_user_phone);
        txtFoodName = itemView.findViewById(R.id.comment_item_name);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        commentImage = itemView.findViewById(R.id.comment_image);
        btnDeleteComment = itemView.findViewById(R.id.btnDeleteComment);
    }
}
