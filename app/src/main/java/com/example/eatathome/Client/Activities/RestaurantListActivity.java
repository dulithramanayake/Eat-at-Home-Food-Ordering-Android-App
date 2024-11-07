package com.example.eatathome.Client.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.eatathome.Interface.ItemClickListener;
import com.example.eatathome.Client.Model.Restaurant;
import com.example.eatathome.Client.Model.Token;
import com.example.eatathome.Client.Constant.Constant;
import com.example.eatathome.Client.ViewHolder.RestaurantViewHolder;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class RestaurantListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recyclerView;
    TextView textFullName;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> adapter;
    FirebaseRecyclerOptions<Restaurant> firebaseRecyclerOptions;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        //for first-time login, pop up notification to complete profile.
        sharedPreferences = getSharedPreferences("com.example.eatathome.Client", MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Restaurants");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //paper init
        Paper.init(this);

        //init firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Restaurants");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Restaurant>().setQuery(category, Restaurant.class).build();

        updateToken(FirebaseInstanceId.getInstance().getToken());


        //set name for user
        final View headerView = navigationView.getHeaderView(0);
        textFullName = headerView.findViewById(R.id.text_full_name);
        if (Constant.currentUser != null) {
            textFullName.setText(Constant.currentUser.getName());
        }

        recyclerView = findViewById(R.id.recyclerview_menu1);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (Constant.isConnectedToInternet(this))
            loadRestaurant();
        else {
            Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void CompleteProfileNotification() {

        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Incomplete Profile");
        alertDialog.setMessage("Please Add Username and Home Address before ordering.");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_profile = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_profile);
        alertDialog.setIcon(R.drawable.ic_baseline_person_24);

        alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent profileIntent = new Intent(RestaurantListActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.currentUser != null) {
            textFullName.setText(Constant.currentUser.getName());
        }
        if (adapter != null)
            adapter.startListening();
        if (sharedPreferences.getBoolean("firstrun", true)) {
            CompleteProfileNotification();
            sharedPreferences.edit().putBoolean("firstrun", false)
                    .apply();
        }
    }


    private void updateToken(String token) {
        if (Constant.currentUser != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token data = new Token(token, false);
            // false because token send from client app

            tokens.child(Constant.currentUser.getPhone()).setValue(data);

        }
    }


    private void loadRestaurant() {

        adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(firebaseRecyclerOptions) {
            @NonNull
            @Override
            public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
                return new RestaurantViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull final Restaurant model) {
                holder.restaurantName.setText(model.getName());
                holder.restaurantLocation.setText(model.getLocation());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.placeholder)
                        .into(holder.restaurantImage);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(RestaurantListActivity.this, CategoriesActivity.class);
                        Constant.restaurantSelected = adapter.getRef(position).getKey();
                        foodList.putExtra(Constant.RESTAURANT_ID, adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
            loadRestaurant();
        if (item.getItemId() == R.id.cart)
            CartActivity();
        return super.onOptionsItemSelected(item);
    }

    private void CartActivity() {
        Intent cartIntent = new Intent(RestaurantListActivity.this, CartActivity.class);
        startActivity(cartIntent);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent NearbyIntent = new Intent(RestaurantListActivity.this, RestaurantListActivity.class);
            startActivity(NearbyIntent);

        } else if (id == R.id.nav_menu) {
            Intent NearbyIntent = new Intent(RestaurantListActivity.this, NearbyRestaurantsActivity.class);
            startActivity(NearbyIntent);

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(RestaurantListActivity.this, CartActivity.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(RestaurantListActivity.this, OrderStatusActivity.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_sign_out) {
      /*      Paper.book().destroy();
            Intent signIn = new Intent(RestaurantListActivity.this, SignInActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);*/
            ConfirmSignOutDialog();

        } else if (id == R.id.nav_profile) {

            Intent profileIntent = new Intent(RestaurantListActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(RestaurantListActivity.this, FavoritesActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void ConfirmSignOutDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RestaurantListActivity.this);
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
                Intent logout = new Intent(RestaurantListActivity.this, MainActivity.class);
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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ConfirmSignOutDialog();
        }
    }
}