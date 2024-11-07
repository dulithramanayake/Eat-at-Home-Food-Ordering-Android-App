package com.example.eatathome.Client.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.Client.Database.Database;
import com.example.eatathome.Client.Model.Food;
import com.example.eatathome.Client.Model.Order;
import com.example.eatathome.Client.Model.Rating;
import com.example.eatathome.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetailsActivity extends AppCompatActivity implements RatingDialogListener {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart, btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;
    MaterialButton showComment;

    FirebaseDatabase database;
    DatabaseReference food;
    DatabaseReference ratingsTable;
    DatabaseReference foods;
    String foodId = "";
    Food currentFood;
    String RestaurantId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        numberButton = findViewById(R.id.number_button);
        btnRating = findViewById(R.id.btn_rating);
        showComment = findViewById(R.id.btn_show_comment);
        showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodDetailsActivity.this, ShowCommentActivity.class);
                intent.putExtra(Constant.FOOD_ID, foodId);
                startActivity(intent);
            }
        });

        ratingBar = findViewById(R.id.ratingBar);
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        // get intent here
        if (getIntent() != null)
            RestaurantId = getIntent().getStringExtra(Constant.RESTAURANT_ID);

        btnCart = findViewById(R.id.btn_cart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        Constant.currentUser.getPhone(),
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getImage(),
                        currentFood.getRestaurantId()

                ));
                Toast.makeText(FoodDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        food_name = findViewById(R.id.food_name);
        food_description = findViewById(R.id.food_description);
        food_price = findViewById(R.id.food_price);
        food_image = findViewById(R.id.food_image);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpanededAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance((R.style.CollapsedAppBar));


        //init firebase
        database = FirebaseDatabase.getInstance();
        food = database.getReference("Restaurants").child(Constant.restaurantSelected).child("detail").child("Foods");
        ratingsTable = database.getReference("Restaurants").child(Constant.restaurantSelected).child("Ratings");


        // get intent here
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {

            if (Constant.isConnectedToInternet(this)) {
                getFoodDetails(foodId);
                getRatingsFood(foodId);
            } else {
                Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    private void getRatingsFood(String foodId) {
        Query foodRating = ratingsTable.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    if (item != null) {
                        sum += Integer.parseInt(item.getRateValue());
                    }
                    count++;

                }
                if (count != 0) {
                    float average;
                    average = sum / count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(5)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorAccent)
                .setDescriptionTextColor(R.color.colorAccent)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.colorPrimary)
                .setCommentTextColor(R.color.colorWhite)
                .setCommentBackgroundColor(R.color.colorAccent)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(FoodDetailsActivity.this)
                .show();
    }

    private void getFoodDetails(String foodId) {
        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                //set Image
                Picasso.get().load(currentFood.getImage()).into(food_image);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {

        //get rating and upload to firebase
        final Rating rating = new Rating(
                Constant.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments,
                currentFood.getImage()
        );
     /*   ratingsTable.child(Constant.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Constant.currentUser.getPhone()).exists()) {
                    ratingsTable.child(Constant.currentUser.getPhone()).removeValue();

                    ratingsTable.child(Constant.currentUser.getPhone()).setValue(rating);

                } else

                    ratingsTable.child(Constant.currentUser.getPhone()).setValue(rating);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        //Fix user can rate multiple time
        ratingsTable.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetailsActivity.this, "Thank you for submit!", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}