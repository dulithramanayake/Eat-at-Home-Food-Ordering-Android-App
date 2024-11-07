package com.example.eatathome.Server.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.R;
import com.example.eatathome.Server.Constant.ConstantRes;
import com.example.eatathome.Server.Constant.NumberOfFood;
import com.example.eatathome.Server.Models.RatingRes;
import com.example.eatathome.Server.ViewHolder.ShowCommentViewHolderRes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ViewCommentActivityRes extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratingDb;

    String foodId = "";

    FirebaseRecyclerAdapter<RatingRes, ShowCommentViewHolderRes> adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comment);

        //Init SwipeRefreshLayout view
        swipeRefreshLayout =  findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConstantRes.isConnectedToInternet(getBaseContext()))
                    loadComment(foodId);
                else {
                    Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default, load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (ConstantRes.isConnectedToInternet(getBaseContext()))
                    loadComment(foodId);
                else {
                    Toast.makeText(getBaseContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Firebase
        database = FirebaseDatabase.getInstance();
        ratingDb = database.getReference("Restaurants").child(Constant.restaurantSelected).child("Ratings");
        recyclerView =  findViewById(R.id.recycler_comment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<RatingRes> options = new FirebaseRecyclerOptions.Builder<RatingRes>()
                .setQuery(ratingDb, RatingRes.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<RatingRes, ShowCommentViewHolderRes>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShowCommentViewHolderRes holder, final int position, @NonNull RatingRes model) {
                holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                holder.txtComment.setText(model.getComment());
                holder.txtUserPhone.setText(model.getUserPhone());
                holder.txtFoodName.setText(NumberOfFood.convertIdToName(model.getFoodId()));
                Picasso.get().load(model.getImage()).resize(70,70)
                        .centerCrop().into(holder.commentImage);
                holder.btnDeleteComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfirmDeleteDialog(adapter.getRef(position).getKey());
                    }
                });
            }
            @NonNull
            @Override
            public ShowCommentViewHolderRes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_comment_layout_res, parent, false);
                return new ShowCommentViewHolderRes(view);
            }
        };

        loadComment(foodId);
    }

    private void loadComment(String foodId) {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void ConfirmDeleteDialog(final String key) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewCommentActivityRes.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Confirm Delete?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_delete_layout, null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ratingDb.child(key).removeValue();
                Toast.makeText(ViewCommentActivityRes.this, "Comment Delete Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}