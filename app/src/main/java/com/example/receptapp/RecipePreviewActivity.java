package com.example.receptapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipePreviewActivity extends AppCompatActivity {

    private TextView descText;
    private TextView instText;
    private TextView ingrList;
    private boolean isFavorite = true;
    private Toolbar toolbar;
    private ImageView rImage;
    private Uri imageUri;
    private String recipeTitle;
    private String recipeDescription;
    private String recipeInstructions;
    private ArrayList ingredientList;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_preview);

        descText = (TextView) findViewById(R.id.recepieADescID);
        toolbar = (Toolbar) findViewById(R.id.rToolbar);
        rImage = (ImageView) findViewById(R.id.rImage);
        ingrList = (TextView) findViewById(R.id.ingredientsID);
        instText = (TextView) findViewById(R.id.instructionsID);
        toolbar.setTitle("");

        ingredientList = new ArrayList<String>();

        Bundle b = getIntent().getExtras();

        if(b != null){
            recipeTitle = (String) b.get("title");
            recipeDescription = (String) b.get("desc");
            recipeInstructions = (String) b.get("inst");
            ingredientList = (ArrayList) b.get("ingrList");
            if((Uri) b.get("image") != null){
                imageUri = (Uri) b.get("image");
                rImage.setImageURI(imageUri);
            }

            descText.setText(recipeDescription);
            toolbar.setTitle(recipeTitle);
            instText.setText(recipeInstructions);

            String ingredients = "";
            for(int i = 0; i < ingredientList.size(); i++){
                String ingredient = ingredientList.get(i).toString();
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