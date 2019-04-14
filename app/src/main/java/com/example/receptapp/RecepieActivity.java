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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecepieActivity extends AppCompatActivity {

    private TextView descText;
    private TextView instText;
    private TextView ingrList;
    boolean isFavorite;
    Toolbar toolbar;
    ImageView rImage;
    private String recepeID;
    private Recept recept;

    private FloatingActionButton fab;

    private FirebaseFirestore db;
    private DocumentReference receptRef;
    private CollectionReference favoriteRef;

    private FirebaseAuth mAuth;
    public String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepie);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        favoriteRef = FirebaseFirestore.getInstance().collection("users").document(user).collection("favorites");

        descText = (TextView) findViewById(R.id.recepieADescID);
        toolbar = (Toolbar) findViewById(R.id.rToolbar);
        rImage = (ImageView) findViewById(R.id.rImage);
        ingrList = (TextView) findViewById(R.id.ingredientsID);
        instText = (TextView) findViewById(R.id.instructionsID);
        toolbar.setTitle("");

        Bundle b = new Bundle();
        b = getIntent().getExtras();

        if(b != null){
            recepeID = (String) b.get("recepeID");
            receptRef = db.collection("recept").document(recepeID);
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFavorite){
                    HashMap<String, String> data = new HashMap<>();
                    data.put("recepeID", recepeID);
                    favoriteRef.document(recepeID).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            fab.hide();
                            fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                            Toast.makeText(getApplicationContext(), R.string.addFavorite, Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("!!!", "Error adding document", e);
                        }
                    });
                    isFavorite = true;


                }else{
                    favoriteRef.document(recepeID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            fab.hide();
                            fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                            Toast.makeText(getApplicationContext(), R.string.removeFavorite, Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("!!!", "Error deleting document", e);
                        }
                    });
                    isFavorite = false;
                }

                fab.show();

            }
        });

        receptRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        recept = document.toObject(Recept.class);
                        descText.setText(recept.getDescription());
                        rImage.setImageResource(recept.getImage());
                        toolbar.setTitle(recept.getTitle());
                        instText.setText(recept.getInstructions());

                        String ingredients = "";
                        for(String i : recept.getIngredients()){
                            ingredients = ingredients + "● " + String.valueOf(i) + "\n";
                        }
                        ingrList.setText(ingredients);

                        if(recept.isFavorite()){
                            fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                        }
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

        favoriteRef.document(recepeID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                    isFavorite = true;
                    Log.d("!!!", "Favorit");
                }
                else{
                    fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    isFavorite = false;
                    Log.d("!!!", "Inte Favorit");
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
