package com.example.receptapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    Fragment recipeListFragment;
    Fragment favoritesFragment;
    Fragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigationID);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        recipeListFragment = new RecipeListFragmentActivity();
        favoritesFragment = new FavoriteListFragmentActivity();
        profileFragment = new ProfileFragment();


        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container_mainID, recipeListFragment).commit();

        }

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_recepies:
                    selectedFragment = recipeListFragment;
                    break;

                case R.id.nav_favorites:
                    selectedFragment = favoritesFragment;
                    break;

                case R.id.nav_profile:
                    selectedFragment = profileFragment;
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container_mainID, selectedFragment).commit();

            return true;
        }
    };

}

