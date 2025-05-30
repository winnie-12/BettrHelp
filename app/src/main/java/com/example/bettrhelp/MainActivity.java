package com.example.bettrhelp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    MoodFragment moodFragment = new MoodFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //default HomeFragment
        bottomNavigationView.setSelectedItemId(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the ID once

            if (itemId == R.id.home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, homeFragment).commit();
                return true;
            } else if (itemId == R.id.mood) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, moodFragment).commit();
                return true;
            }
            return false;
        });

    }
    public void navigateToMoodFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFragmentContainer, new MoodFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        bottomNavigationView.setSelectedItemId(R.id.mood);
    }

    public void navigateToHomeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFragmentContainer, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

}