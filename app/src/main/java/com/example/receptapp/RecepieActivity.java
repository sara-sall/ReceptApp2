package com.example.receptapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecepieActivity extends AppCompatActivity {

    private String title;
    private String desc;
    private int image;
    private TextView titleText;
    private TextView descText;
    boolean isFavorite;
    Toolbar toolbar;
    ImageView rImage;
    private String recepeID;
    private FirebaseFirestore db;
    private DocumentReference receptRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepie);

        //FirebaseApp.initializeApp(container.getContext());
        db = FirebaseFirestore.getInstance();


     //   titleText = (TextView) findViewById(R.id.recepieATitleID);
        descText = (TextView) findViewById(R.id.recepieADescID);
        toolbar = (Toolbar) findViewById(R.id.rToolbar);
        rImage = (ImageView) findViewById(R.id.rImage);
        toolbar.setTitle("");


        Bundle b = new Bundle();
        b = getIntent().getExtras();

        if(b != null){

            title = (String) b.get("title");
            desc = (String) b.get("description");
            isFavorite = (boolean) b.get("isFav");
            image = (int) b.get("image");
            recepeID = (String) b.get("recepeID");

            receptRef = db.collection("recept").document(recepeID);

        }

        receptRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Recept recept = document.toObject(Recept.class);
                        descText.setText(recept.getDescription());
                        rImage.setImageResource(recept.getImage());
                        toolbar.setTitle(recept.getTitle());
                    }
                    else{
                        Log.d("!!!", "No document found");
                    }
                }
                else{
                    Log.d("!!!", "get failed with ", task.getException());
                }
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecepieActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(isFavorite){
            fab.setImageResource(R.drawable.ic_favorite_white_24dp);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFavorite){

                    Snackbar.make(view, "Recept sparat i favoriter", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.ic_favorite_white_24dp);

                    isFavorite=true;
                }else{

                    Snackbar.make(view, "Recept borttaget ur favoriter", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    isFavorite = false;
                }
                fab.hide();
                fab.show();

            }
        });
    }

}
