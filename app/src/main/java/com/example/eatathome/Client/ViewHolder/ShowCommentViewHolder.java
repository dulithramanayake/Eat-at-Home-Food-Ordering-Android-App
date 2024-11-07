package com.example.eatathome.Client.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eatathome.R;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {

    public TextView txtUserPhone, txtComment, txtFoodName;
    public RatingBar ratingBar;
    public ImageView commentImage;

    public ShowCommentViewHolder(View itemView) {
        super(itemView);
        txtComment = itemView.findViewById(R.id.comment);
        txtFoodName = itemView.findViewById(R.id.comment_item_name);
        txtUserPhone = itemView.findViewById(R.id.comment_user_phone);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        commentImage = itemView.findViewById(R.id.comment_image);
    }
}
