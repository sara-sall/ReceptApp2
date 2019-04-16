package com.example.receptapp;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AddRecepeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView ingrButton;
    int ingrNr = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recepe);

        toolbar = findViewById(R.id.toolbarID);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ingrButton = (ImageView) findViewById(R.id.addMorIngrButton);
        ingrButton.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.addMorIngrButton:
                TextInputEditText editText = new TextInputEditText(this);
                LinearLayout ingrLayout = (LinearLayout) findViewById(R.id.ingrLayoutID);

                TextInputLayout textInputLayout = new TextInputLayout(this);
                LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                textInputLayout.setLayoutParams(textInputLayoutParams);
                textInputLayout.addView(editText);
               // textInputLayout.setDefaultHintTextColor(R.color.colorAccent);
                //textInputLayout.setHint("Ingrediens");
                editText.setId(R.id.ingredientID + ingrNr);
                editText.setMaxLines(1);
                editText.setHint(R.string.addIngredient);
                editText.setSingleLine(true);
                editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
                editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ingrLayout.addView(textInputLayout);
                ingrNr += 1;
                //setContentView(textInputLayout);
                Log.d("!!!", String.valueOf(editText.getId()));
                break;
        }

    }
}
