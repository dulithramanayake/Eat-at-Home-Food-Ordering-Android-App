package com.example.eatathome.Administrator.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eatathome.Administrator.ConstantAdmin;
import com.example.eatathome.Administrator.Model.AddRestaurants;
import com.example.eatathome.Administrator.ViewHolder.AddRestaurantsViewHolder;
import com.example.eatathome.MainActivity;
import com.example.eatathome.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AddRestaurantsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase references and variables
    FirebaseDatabase database;
    DatabaseReference restaurants;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<AddRestaurants, AddRestaurantsViewHolder> adapter;

    // View elements
    RecyclerView recycler_menu_admin;
    RecyclerView.LayoutManager layoutManager;

    //Add new menu layout
    TextInputEditText edtName, edtId, edtLocation, latitude, longitude;
    MaterialButton btnUpload, btnSelect;

    AddRestaurants newRestaurants;
    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurants);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Restaurants");
        setSupportActionBar(toolbar);

        // Initialize Firebase and views
        database = FirebaseDatabase.getInstance();
        restaurants = database.getReference("Restaurants");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Set click listener for the "Add Restaurant" button
        FloatingActionButton add_restaurants = findViewById(R.id.fab_add_restaurants);
        add_restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRestaurants();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize the RecyclerView and load restaurant data
        recycler_menu_admin = findViewById(R.id.recycler_restaurants);
        recycler_menu_admin.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu_admin.setLayoutManager(layoutManager);

        loadRestaurants();
    }


    // Load restaurant data from Firebase
    private void loadRestaurants() {
        // FirebaseRecyclerAdapter to populate restaurant data
        FirebaseRecyclerOptions<AddRestaurants> options = new FirebaseRecyclerOptions.Builder<AddRestaurants>()
                .setQuery(restaurants, AddRestaurants.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<AddRestaurants, AddRestaurantsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddRestaurantsViewHolder holder, int position, @NonNull AddRestaurants model) {
                holder.txtMenuName.setText(model.getName());
                holder.txtLocation.setText(model.getLocation());
                Picasso.get().load(model.getImage()).into(holder.imageView);
            }

            @NonNull
            @Override
            public AddRestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_item, parent, false);
                return new AddRestaurantsViewHolder(itemView);
            }
        };

        //refresh data if have data changed
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_menu_admin.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // Method to add a new restaurant
    private void addRestaurants() {
        // Create an AlertDialog to get restaurant details from the user
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Add New Restaurant");
        alertDialog.setMessage("Please fill information");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_restaurant_layout = inflater.inflate(R.layout.add_new_restaurants, null);
        edtName = add_restaurant_layout.findViewById(R.id.et_name_restaurants);
        edtId = add_restaurant_layout.findViewById(R.id.et_name_restaurants_id);
        edtLocation = add_restaurant_layout.findViewById(R.id.et_name_restaurants_location);
        latitude = add_restaurant_layout.findViewById(R.id.et_name_restaurants_latitude);
        longitude = add_restaurant_layout.findViewById(R.id.et_name_restaurants_longitude);
        btnSelect = add_restaurant_layout.findViewById(R.id.btn_restaurant_select);
        btnUpload = add_restaurant_layout.findViewById(R.id.btn_restaurant_upload);
        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //let users select image from gallery and save URL of this image
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload image
                uploadImage();
            }
        });
        // Event listeners for the "Select" and "Upload" buttons
        // Handle button clicks for confirmation
        alertDialog.setView(add_restaurant_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_restaurant_24);
        //set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AddRestaurants restaurant = new AddRestaurants();
                restaurant.setName(edtName.getText().toString());
                restaurant.setRestaurantId(edtId.getText().toString());
                restaurant.setLocation(edtLocation.getText().toString());
                restaurant.setLatitude(latitude.getText().toString());
                restaurant.setLongitude(longitude.getText().toString());
                restaurant.setImage(saveUri.toString());

                restaurants.child(edtId.getText().toString())
                        .setValue(restaurant)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddRestaurantsActivity.this, "Restaurant Created Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddRestaurantsActivity.this, "Failed to Create Restaurant!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    // Method to allow the user to choose an image
    private void chooseImage() {
        // Let users select an image from the gallery and save its URL
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), ConstantAdmin.PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of selecting an image
        if (requestCode == ConstantAdmin.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {

            saveUri = data.getData();
            btnSelect.setText("Image Selected!");
        }
    }
    // Method to upload the selected image
    private void uploadImage() {

        // Upload the selected image and save the URL
        if (saveUri != null) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(AddRestaurantsActivity.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //set value for newRestaurants if image upload and we can get download link
                            newRestaurants = new AddRestaurants();
                            newRestaurants.setName(edtName.getText().toString());
                            newRestaurants.setLocation(edtLocation.getText().toString());
                            newRestaurants.setRestaurantId(edtId.getText().toString());
                            newRestaurants.setLatitude(latitude.getText().toString());
                            newRestaurants.setLongitude(longitude.getText().toString());
                            newRestaurants.setImage(uri.toString());
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(AddRestaurantsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploading" + progress + " % ");
                        }
                    });
        }
    }

    //Update and delete

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // Handle context menu item selection
        if (item.getTitle().equals(ConstantAdmin.UPDATE)) {
            // Show a dialog to update the restaurant details
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        } else if (item.getTitle().equals(ConstantAdmin.DELETE)) {
            // Show a confirmation dialog for deleting the restaurant
            ConfirmDeleteDialog(item);
        }

        return super.onContextItemSelected(item);
    }

    private void ConfirmDeleteDialog(final MenuItem item) {

        // Show a confirmation dialog for deleting the restaurant
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Confirm Delete?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_delete_layout, null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteCategory(adapter.getRef(item.getOrder()).getKey());
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

    private void showUpdateDialog(final String key, final AddRestaurants item) {

        // Show a dialog to update the restaurant details
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        alertDialog.setTitle("Update Restaurant");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View update_restaurant_layout = inflater.inflate(R.layout.add_new_restaurants, null);

        edtName = update_restaurant_layout.findViewById(R.id.et_name_restaurants);
        edtId = update_restaurant_layout.findViewById(R.id.et_name_restaurants_id);
        edtLocation = update_restaurant_layout.findViewById(R.id.et_name_restaurants_location);
        latitude = update_restaurant_layout.findViewById(R.id.et_name_restaurants_latitude);
        longitude = update_restaurant_layout.findViewById(R.id.et_name_restaurants_longitude);
        btnSelect = update_restaurant_layout.findViewById(R.id.btn_restaurant_select);
        btnUpload = update_restaurant_layout.findViewById(R.id.btn_restaurant_upload);

        //set default name
        edtName.setText(item.getName());
        edtId.setText(item.getRestaurantId());
        edtLocation.setText(item.getLocation());
        latitude.setText(item.getLatitude());
        longitude.setText(item.getLongitude());
        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //let users select image from gallery and save URL of this image
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload image
                changeImage(item);
            }
        });
        alertDialog.setView(update_restaurant_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        //set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //update information
                item.setName(edtName.getText().toString());
                restaurants.child(key).setValue(item);
                Toast.makeText(AddRestaurantsActivity.this, "Restaurant Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void deleteCategory(String key) {
        // Delete a restaurant category and its associated data
        DatabaseReference foods = database.getReference("Restaurants");
        final Query foodInCategory = foods.orderByChild("menuId").equalTo(key);
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    postSnapShot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        restaurants.child(key).removeValue();
        Toast.makeText(AddRestaurantsActivity.this, "Restaurant Deleted Successfully!", Toast.LENGTH_SHORT).show();
    }
    private void changeImage(final AddRestaurants item) {
        // Change the restaurant image
        if (saveUri != null) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(AddRestaurantsActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //set value for newCategory if image upload and we can get download link
                            item.setImage(uri.toString());
                            Toast.makeText(AddRestaurantsActivity.this, "Image Changed Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(AddRestaurantsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploading " + progress + " % ");
                        }
                    });
        }
    }
    // Handle navigation drawer item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_restaurants) {

        } else if (id == R.id.nav_add_admins) {
            Intent cartIntent = new Intent(AddRestaurantsActivity.this, AddRestaurantsAdminActivity.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_sign_out) {
            ConfirmSignOutDialog();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ConfirmSignOutDialog() {

        // Show a confirmation dialog for signing out
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(AddRestaurantsActivity.this);
        alertDialog.setTitle("Confirm Sign Out?");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_signout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_signout);
        alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);

        alertDialog.setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //log out
                Intent logout = new Intent(AddRestaurantsActivity.this, MainActivity.class);
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
        // Show the "Are you sure you want to log out?" dialog
        ConfirmSignOutDialog();
    }

}