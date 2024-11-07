package com.example.eatathome.Client.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.example.eatathome.Client.Database.Database;
import com.example.eatathome.Interface.ItemClickListener;
import com.example.eatathome.Client.Model.Favorites;
import com.example.eatathome.Client.Model.Food;
import com.example.eatathome.Client.Model.Order;
import com.example.eatathome.Client.ViewHolder.FoodViewHolder;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;
import com.example.eatathome.Client.Constant.Constant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class FoodListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView textFullName;
    FirebaseDatabase database;
    DatabaseReference foodList;
    RecyclerView recyclerViewFood;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerOptions<Food> firebaseRecyclerOptions;
    String categoryId = "";
    String RestaurantId = "";


    Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        CounterFab counterFab = findViewById(R.id.fab);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" Food List");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //init firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Restaurants").child(Constant.restaurantSelected).child("detail").child("Foods");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodList, Food.class).build();

        //local db
        localDB = new Database(this);

        //set name for user
        final View headerView = navigationView.getHeaderView(0);
        textFullName = headerView.findViewById(R.id.text_full_name);
        if (Constant.currentUser != null) {
            textFullName.setText(Constant.currentUser.getName());
        }

        //Load menu
        recyclerViewFood = findViewById(R.id.recyclerview_menu1);
        recyclerViewFood.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerViewFood.setLayoutManager(layoutManager);

        // get intent here
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra(Constant.CATEGORY_ID);
        if (!categoryId.isEmpty() && categoryId != null) {

            if (Constant.isConnectedToInternet(this))
                loadFood(categoryId);
            else {
                Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // get intent here
        if (getIntent() != null)
            RestaurantId = getIntent().getStringExtra(Constant.RESTAURANT_ID);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFood(String categoryId) {
        //create query by category ID
        Query searchByName = foodList.orderByChild("menuId").equalTo(categoryId);
        //create option with query
        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName, Food.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Food, com.example.eatathome.Client.ViewHolder.FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final com.example.eatathome.Client.ViewHolder.FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("%s", model.getPrice().toString()));
                Picasso.get().load(model.getImage()).placeholder(R.drawable.placeholderfood).into(viewHolder.food_image);
                //Quick cart
                viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isExists = new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(), Constant.currentUser.getPhone());

                        if (!isExists) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    Constant.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getImage(),
                                    model.getRestaurantId()
                            ));
                        } else {
                            new Database(getBaseContext()).increaseCart(Constant.currentUser.getPhone(), adapter.getRef(position).getKey());
                        }
                        Toast.makeText(FoodListActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                });
                //add favourites
                if (localDB.isFavourite(adapter.getRef(position).getKey(), Constant.currentUser.getPhone()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite);
                //click to share
                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* Picasso.get()
                                .load(model.getImage())
                                .into(target);*/
                    }
                });
                //click to change the status of favourites
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favorites favorites = new Favorites();
                        favorites.setFoodId(adapter.getRef(position).getKey());
                        favorites.setFoodName(model.getName());
                        favorites.setFoodDescription(model.getDescription());
                        favorites.setFoodImage(model.getImage());
                        favorites.setFoodMenuId(model.getMenuId());
                        favorites.setUserPhone(Constant.currentUser.getPhone());
                        favorites.setFoodPrice(model.getPrice());

                        if (!localDB.isFavourite(adapter.getRef(position).getKey(), Constant.currentUser.getPhone())) {

                            localDB.addToFavourites(favorites);
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite);
                            Toast.makeText(FoodListActivity.this, "" + model.getName() +
                                    " was added to Favourites", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFromFavourites(adapter.getRef(position).getKey(), Constant.currentUser.getPhone());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border);
                            Toast.makeText(FoodListActivity.this, "" + model.getName() +
                                    " was removed from Favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent foodDetail = new Intent(FoodListActivity.this, FoodDetailsActivity.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey()); //send FoodId to new activity
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public com.example.eatathome.Client.ViewHolder.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new com.example.eatathome.Client.ViewHolder.FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerViewFood.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh)
            loadFood(categoryId);
        if (item.getItemId() == R.id.cart)
            CartActivity();
        return super.onOptionsItemSelected(item);
    }

    private void CartActivity() {
        Intent cartIntent = new Intent(FoodListActivity.this, CartActivity.class);
        startActivity(cartIntent);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent NearbyIntent = new Intent(FoodListActivity.this, RestaurantListActivity.class);
            startActivity(NearbyIntent);

        } else if (id == R.id.nav_menu) {
            Intent NearbyIntent = new Intent(FoodListActivity.this, NearbyRestaurantsActivity.class);
            startActivity(NearbyIntent);

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(FoodListActivity.this, CartActivity.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(FoodListActivity.this, OrderStatusActivity.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_sign_out) {

            ConfirmSignOutDialog();

        } else if (id == R.id.nav_profile) {

            Intent profileIntent = new Intent(FoodListActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(FoodListActivity.this, FavoritesActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void ConfirmSignOutDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodListActivity.this);
        alertDialog.setTitle("Confirm Sign Out?");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_signout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_signout);
        alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);

        alertDialog.setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Delete remember user && password
                Paper.book().destroy();

                //log out
                Intent logout = new Intent(FoodListActivity.this, MainActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);

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