package com.example.receptapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecepePreviewActivity extends AppCompatActivity {

    private TextView descText;
    private TextView instText;
    private TextView ingrList;
    private boolean isFavorite = true;
    private Toolbar toolbar;
    private ImageView rImage;
    private Uri imageUri;
    private String recepeTitle;
    private String recepeDescription;
    private String recepeInstructions;
    private ArrayList ingridientList;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepe_preview);

        descText = (TextView) findViewById(R.id.recepieADescID);
        toolbar = (Toolbar) findViewById(R.id.rToolbar);
        rImage = (ImageView) findViewById(R.id.rImage);
        ingrList = (TextView) findViewById(R.id.ingredientsID);
        instText = (TextView) findViewById(R.id.instructionsID);
        toolbar.setTitle("");

        ingridientList = new ArrayList<String>();

        Bundle b = getIntent().getExtras();

        if(b != null){
            recepeTitle = (String) b.get("title");
            recepeDescription = (String) b.get("desc");
            recepeInstructions = (String) b.get("inst");
            ingridientList = (ArrayList) b.get("ingrList");
            if((Uri) b.get("image") != null){
                imageUri = (Uri) b.get("image");
                rImage.setImageURI(imageUri);
            }

            descText.setText(recepeDescription);
            toolbar.setTitle(recepeTitle);
            instText.setText(recepeInstructions);

            String ingredients = "";
            for(int i = 0; i < ingridientList.size(); i++){
                String ingredient = ingridientList.get(i).toString();
                ingredients = ingredients + "● " + ingredient + "\n";
            }
            ingrList.setText(ingredients);

        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFavorite){
                    fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                    isFavorite = true;

                }else{
                    fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    isFavorite = false;
                }
                fab.hide();
                fab.show();
            }
        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}