package com.example.receptapp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddRecepeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private LinearLayout addImageBtn;
    private ImageView recepeImage;

    private EditText recepeTitle;
    private EditText recepeDesc;
    private EditText recepeIngr;
    private EditText recepeInst;
    private CheckBox cbKött;
    private CheckBox cbKyckling;
    private CheckBox cbFisk;
    private CheckBox cbLax;
    private CheckBox cbVego;
    private CheckBox cbPasta;
    private CheckBox cbPotatis;
    private CheckBox cbRis;
    private CheckBox cbBakelse;
    private CheckBox cbFrukt;

    private LinearLayout tagsBtn;
    private TableLayout tagsLay;
    private ImageView tagArrow;

    private ImageView ingrButton;

    private EditText rIngr;

    private ArrayList idList;
    private ArrayList ingrList;
    private ArrayList tags;

    private FirebaseFirestore db;
    private CollectionReference receptRef;

    private Button previewBtn;

    int ingrNr = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recepe);

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("Nytt recept");
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        idList = new ArrayList();
        ingrList = new ArrayList();
        tags = new ArrayList();

        addImageBtn = (LinearLayout) findViewById(R.id.addImageLayout);
        addImageBtn.setOnClickListener(this);
        recepeImage = (ImageView) findViewById(R.id.imageLayout);

        recepeTitle = (EditText) findViewById(R.id.recepeTitleID);
        recepeDesc = (EditText) findViewById(R.id.recepeDescID);
        recepeIngr = (EditText) findViewById(R.id.ingredientID);
        recepeInst = (EditText) findViewById(R.id.recepeInstrID);

        previewBtn = (Button) findViewById(R.id.recepePreviewID);
        previewBtn.setOnClickListener(this);

        cbBakelse = (CheckBox) findViewById(R.id.cbBakelse);
        cbFisk = (CheckBox) findViewById(R.id.cbFisk);
        cbFrukt = (CheckBox) findViewById(R.id.cbFrukt);
        cbKyckling = (CheckBox) findViewById(R.id.cbKyckling);
        cbKött = (CheckBox) findViewById(R.id.cbKött);
        cbLax = (CheckBox) findViewById(R.id.cbLax);
        cbPasta = (CheckBox) findViewById(R.id.cbPasta);
        cbPotatis = (CheckBox) findViewById(R.id.cbPotatis);
        cbRis = (CheckBox) findViewById(R.id.cbRis);
        cbVego = (CheckBox) findViewById(R.id.cbVegetarisk);

        tagsBtn = (LinearLayout) findViewById(R.id.tagsButtonLayout);
        tagsBtn.setOnClickListener(this);
        tagsLay = (TableLayout) findViewById(R.id.tagsLayout);
        tagArrow = (ImageView) findViewById(R.id.tagsArrow);


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
            case R.id.addImageLayout:

                if(recepeImage.getVisibility() == View.GONE){
                    recepeImage.setVisibility(View.VISIBLE);
                }else{
                    recepeImage.setVisibility(View.GONE);
                }
                break;


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
                editText.setId(100 + ingrNr);
                editText.setMaxLines(1);
                editText.setHint(R.string.addIngredient);
                editText.setSingleLine(true);
                editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
                editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ingrLayout.addView(textInputLayout);

                idList.add(editText.getId());
                ingrNr += 1;
                //setContentView(textInputLayout);
                Log.d("!!!", String.valueOf(editText.getId()));
                break;

            case R.id.recepePreviewID:
                String title = recepeTitle.getText().toString().trim();
                String desc = recepeDesc.getText().toString().trim();
                String ingr = recepeIngr.getText().toString().trim();
                String inst = recepeInst.getText().toString().trim();
                ingrList.clear();
                ingrList.add(ingr);

                for(int i = 0; i<ingrNr; i++){
                    String ID = String.valueOf(idList.get(i));
                    rIngr = (EditText)findViewById(Integer.parseInt(ID));
                    String ingredient = rIngr.getText().toString().trim();
                    if(!ingredient.equals("")){
                        ingrList.add(ingredient);
                    }
                }

                if(title.isEmpty()){
                    recepeTitle.setError("Recepttitel behöver fyllas i");
                    recepeTitle.requestFocus();
                    return;
                }
                if(desc.isEmpty()){
                    recepeDesc.setError("Specialkost behöver fyllas i");
                    recepeDesc.requestFocus();
                    return;
                }
                if(ingr.isEmpty()){
                    recepeIngr = (EditText) findViewById(R.id.ingredientID);
                    recepeIngr.setError("Recepttitel behöver fyllas i");
                    recepeIngr.requestFocus();
                    return;
                }
                if(inst.isEmpty()){
                    recepeInst.setError("Instruktioner behöver fyllas i");
                    recepeInst.requestFocus();
                    return;
                }

                Intent intent = new Intent(AddRecepeActivity.this, RecepePreviewActivity.class);

                intent.putExtra("title", title);
                intent.putExtra("desc", desc);
                intent.putExtra("ingrList", ingrList);
                intent.putExtra("inst", inst);

                startActivity(intent);
                break;

            case R.id.recepeCreateID:
                title = recepeTitle.getText().toString().trim();
                desc = recepeDesc.getText().toString().trim();
                ingr = recepeIngr.getText().toString().trim();
                inst = recepeInst.getText().toString().trim();
                ingrList.clear();
                tags.clear();
                ingrList.add(ingr);

                for(int i = 0; i<ingrNr; i++){
                    String ID = String.valueOf(idList.get(i));
                    rIngr = (EditText)findViewById(Integer.parseInt(ID));
                    String ingredient = rIngr.getText().toString().trim();
                    if(!ingredient.equals("")){
                        ingrList.add(ingredient);
                    }
                }

                if(title.isEmpty()){
                    recepeTitle.setError("Recepttitel behöver fyllas i");
                    recepeTitle.requestFocus();
                    return;
                }
                if(desc.isEmpty()){
                    recepeDesc.setError("Specialkost behöver fyllas i");
                    recepeDesc.requestFocus();
                    return;
                }
                if(ingr.isEmpty()){
                    recepeIngr = (EditText) findViewById(R.id.ingredientID);
                    recepeIngr.setError("Recepttitel behöver fyllas i");
                    recepeIngr.requestFocus();
                    return;
                }
                if(inst.isEmpty()){
                    recepeInst.setError("Instruktioner behöver fyllas i");
                    recepeInst.requestFocus();
                    return;
                }

                if(cbBakelse.isChecked()){
                    tags.add("bakelse");
                }
                if(cbVego.isChecked()){
                    tags.add("vegetarisk");
                }
                if(cbRis.isChecked()){
                    tags.add("ris");
                }
                if(cbPotatis.isChecked()){
                    tags.add("potatis");
                }
                if(cbPasta.isChecked()){
                    tags.add("pasta");
                }
                if(cbLax.isChecked()){
                    tags.add("lax");
                }
                if(cbKött.isChecked()){
                    tags.add("kött");
                }
                if(cbKyckling.isChecked()){
                    tags.add("kyckling");
                }
                if(cbFrukt.isChecked()) {
                    tags.add("frukt");
                }

                db = FirebaseFirestore.getInstance();
                receptRef = db.collection("recept");

                Recept r = new Recept(title, desc, ingrList, inst, tags, "");

                receptRef.add(r);

                Intent i = new Intent(AddRecepeActivity.this, MainActivity.class);

                startActivity(i);

                break;

            case R.id.tagsButtonLayout:
                int vis = tagsLay.getVisibility();
                if(vis == View.VISIBLE){
                    tagsLay.setVisibility(View.GONE);
                    tagArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                }else{
                    tagsLay.setVisibility(View.VISIBLE);
                    tagArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
                break;


        }



    }
}
