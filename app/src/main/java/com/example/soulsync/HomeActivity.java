package com.example.soulsync;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.soulsync.databinding.ActivityHomeBinding;
import com.example.soulsync.fragments.AccountFragment;
import com.example.soulsync.fragments.HelpFragment;
import com.example.soulsync.fragments.HomeFragment;
import com.example.soulsync.fragments.JournalEntryFragment;
import com.example.soulsync.fragments.MindfulnessFragment;
import com.example.soulsync.fragments.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "SoulSync";

    ActivityHomeBinding binding;
    FirebaseFirestore db;

    String photoID = "ic_user.png";

    FirebaseStorage storage;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.navigationView.setNavigationItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        loadUserImage();


        if (savedInstanceState == null) {
            Fragment fragment = new JournalEntryFragment();
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_fragment_one) {
                switchFragment(new JournalEntryFragment());
            } else if (item.getItemId() == R.id.navigation_fragment_two) {
                switchFragment(new MindfulnessFragment());
            } else if (item.getItemId() == R.id.navigation_fragment_three) {
                switchFragment(new HomeFragment());
            }
            return true;
        });

    }

    private void loadUserImage() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userID = user.getUid();


        //Retrieving the photoID from FireStore
        db.collection("users").document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            if(document.exists()){
                                photoID = document.getString("photoID");
                                Log.d(TAG, "THIS IS THE PHOTO" + photoID);

                                if (photoID!= null){

                                    try {
                                        StorageReference pathReference = storageReference.child("users/" + photoID);
                                        Log.d(TAG, "THIS IS THE SECOND " + photoID);
                                        final long ONE_MEGABYTE = 1024 * 1024;
                                        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Log.d("Firebase", "User photo downloaded");
                                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                updateDrawerImage(bmp);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Log.d("Firebase", "There is an error downloading the user photo");
                                            }
                                        });

                                    } catch (Exception e){
                                        Log.d("Firebase","Error:" + e);
                                    }

                                }

                            }else{
                                Toast.makeText(HomeActivity.this, "No file on records", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Log.w(TAG, "Error getting document", task.getException());
                        }
                    }
                });


    }

    private void updateDrawerImage(Bitmap bmp) {

        View headerView = binding.navigationView.getHeaderView(0);
        ImageView userpic = headerView.findViewById(R.id.drawerPic);
        userpic.setImageBitmap(bmp);

        Glide.with(this)
                .load(bmp)
                .apply(RequestOptions.circleCropTransform())
                .into(userpic);
    }

    public void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.navigation_fragment_one) {
            switchFragment(new JournalEntryFragment());
        } else if (item.getItemId() == R.id.navigation_fragment_two) {
            switchFragment(new MindfulnessFragment());
        }else if (item.getItemId() == R.id.navigation_fragment_three) {
            switchFragment(new AccountFragment());
        } else if (item.getItemId() == R.id.navigation_fragment_four) {
            switchFragment(new HelpFragment());
        }else if (item.getItemId() == R.id.navigation_fragment_five) {
            switchFragment(new SettingsFragment());
        }


        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}