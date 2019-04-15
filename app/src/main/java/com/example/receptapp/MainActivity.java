package com.example.receptapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FragmentManager manager;
    Fragment recepieListFragment;
    Fragment favoritesFragment;
    Fragment profileFragment;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        View decorView = getWindow().getDecorView();
//
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigationID);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        recepieListFragment = new RecepieListFragmentActivity();
        favoritesFragment = new FavoriteListFragmentActivity();
        profileFragment = new ProfileFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container_mainID, recepieListFragment).commit();



    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_recepies:
                    selectedFragment = recepieListFragment;
                    break;

                case R.id.nav_favorites:
                    selectedFragment = favoritesFragment;
                //    toolbar.setTitle(R.string.toolbarTitleFav);
                    break;

                case R.id.nav_profile:
                    selectedFragment = profileFragment;
                  //  toolbar.setTitle(R.string.toolbarTitleProf);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container_mainID, selectedFragment).commit();

            return true;
        }
    };


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem mSearch = menu.findItem(R.id.menuSearch);
        SearchView mSV = (SearchView) mSearch.getActionView();
        return super.onCreateOptionsMenu(menu);
    }*/
}

