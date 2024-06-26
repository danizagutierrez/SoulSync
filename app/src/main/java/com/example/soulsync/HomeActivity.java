package com.example.soulsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.soulsync.databinding.ActivityHomeBinding;
import com.example.soulsync.fragments.AccountFragment;
import com.example.soulsync.fragments.HelpFragment;
import com.example.soulsync.fragments.JournalEntryFragment;
import com.example.soulsync.fragments.MindfulnessFragment;
import com.example.soulsync.fragments.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActivityHomeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.navigationView.setNavigationItemSelectedListener(this);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        if (savedInstanceState == null) {
            Fragment fragment = new JournalEntryFragment();
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_fragment_one) {
                switchFragment(new JournalEntryFragment());
            } else if (item.getItemId() == R.id.navigation_fragment_two) {
                switchFragment(new MindfulnessFragment());
            }
            return true;
        });

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