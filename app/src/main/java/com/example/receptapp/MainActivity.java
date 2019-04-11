package com.example.receptapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FragmentManager manager;
    Fragment recepieListFragment;
    Fragment favoritesFragment;
    Fragment profileFragment;
    Toolbar toolbar;

/*    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference receptRef = db.collection("recept");

    private String title;
    private String desc;
    private ArrayList<String> ingredients;
    private String inst;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbarID);
        toolbar.setTitle(R.string.toolbarTitleRecepe);
        setSupportActionBar(toolbar);


        final ArrayList<Recept> receptLista;
        //        receptLista = new ArrayList<Recept>();
        //
        //        // public void loadRecepes(View v){
        //        receptRef.get()
        //                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        //                    @Override
        //                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
        //                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
        //                            Recept recept = documentSnapshot.toObject(Recept.class);
        //                            receptLista.add(recept);
        //                        }
        //                    }
        //                });
        //
        //
        //        // }




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
                    toolbar.setTitle(R.string.toolbarTitleRecepe);
                    break;

                case R.id.nav_favorites:
                    selectedFragment = favoritesFragment;
                    toolbar.setTitle(R.string.toolbarTitleFav);
                    break;

                case R.id.nav_profile:
                    selectedFragment = profileFragment;
                    toolbar.setTitle(R.string.toolbarTitleProf);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container_mainID, selectedFragment).commit();

            return true;
        }
    };
}
