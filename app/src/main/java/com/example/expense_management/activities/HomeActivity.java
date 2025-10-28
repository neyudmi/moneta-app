package com.example.expense_management.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.api.ApiServices;
import com.example.expense_management.activities.DashBoard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        fab = findViewById(R.id.fab);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new DashBoard());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int idNav = item.getItemId();

            if (idNav == R.id.nav_home) {
                selectedFragment = new DashBoard();
            } else if (idNav == R.id.nav_task) {
                // Add your TaskFragment here when available
                // selectedFragment = new TaskFragment();
            } else if (idNav == R.id.nav_notification) {
                // Add your NotificationFragment here
                // selectedFragment = new NotificationFragment();
            } else if (idNav == R.id.nav_settings) {
                selectedFragment = new Profile();
            } else {
                return false; // prevent crash or unhandled selection (like the FAB placeholder)
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }
}
