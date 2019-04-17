package com.example.receptapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class AddRecepeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ProgressBar progressBar;

    private LinearLayout addImageBtn;
    private ImageView recepeImage;
    private ImageView imageDeleteBtn;

    private static final int PICK_IMAGE_REQUEST =1;
    private Uri imageUri;
    private String uniqueId;

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

    private String creator;

    private ImageView ingrButton;

    private EditText rIngr;

    private String title;
    private String desc;
    private String ingr;
    private String inst;

    private ArrayList idList;
    private ArrayList ingrList;
    private ArrayList tags;

    private FirebaseFirestore db;
    private CollectionReference receptRef;

    private StorageReference imageStorageRef;
    private StorageReference fileReference;

    private FirebaseAuth mAuth;

    private Button previewBtn;
    private Button addRecepeBtn;

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

        progressBar = (ProgressBar) findViewById(R.id.progressbarAR);

        mAuth = FirebaseAuth.getInstance();

        imageStorageRef = FirebaseStorage.getInstance().getReference();

        idList = new ArrayList();
        ingrList = new ArrayList();
        tags = new ArrayList();

        addImageBtn = (LinearLayout) findViewById(R.id.addImageLayout);
        addImageBtn.setOnClickListener(this);
        recepeImage = (ImageView) findViewById(R.id.imageLayout);
        imageDeleteBtn = (ImageView) findViewById(R.id.imageDeleteButton);
        imageDeleteBtn.setOnClickListener(this);

        recepeTitle = (EditText) findViewById(R.id.recepeTitleID);
        recepeDesc = (EditText) findViewById(R.id.recepeDescID);
        recepeIngr = (EditText) findViewById(R.id.ingredientID);
        recepeInst = (EditText) findViewById(R.id.recepeInstrID);

        previewBtn = (Button) findViewById(R.id.recepePreviewID);
        previewBtn.setOnClickListener(this);

        addRecepeBtn = (Button) findViewById(R.id.recepeCreateID);
        addRecepeBtn.setOnClickListener(this);

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
                openFileChooser();
                break;

            case R.id.imageDeleteButton:
                imageUri = null;
                imageDeleteBtn.setVisibility(View.GONE);
                recepeImage.setVisibility(View.GONE);
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
                editText.setId(100 + ingrNr);
                editText.setMaxLines(1);
                editText.setHint(R.string.addIngredient);
                editText.setSingleLine(true);
                editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
                editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ingrLayout.addView(textInputLayout);

                idList.add(editText.getId());
                ingrNr += 1;

                break;

            case R.id.recepePreviewID:

                if (checkInput()){
                    Intent intent = new Intent(AddRecepeActivity.this, RecepePreviewActivity.class);

                    intent.putExtra("title", title);
                    intent.putExtra("desc", desc);
                    intent.putExtra("ingrList", ingrList);
                    intent.putExtra("inst", inst);

                    if(imageUri != null){
                        intent.putExtra("image", imageUri);
                    }

                    startActivity(intent);
                }

                break;

            case R.id.recepeCreateID:

                tags.clear();

                checkTags();

                if(checkInput()){
                    uploadRecepe();
                }
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

    private void checkTags(){
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
        if(cbFisk.isChecked()){
            tags.add("fisk");
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).resize(250, 250).onlyScaleDown().centerInside().into(recepeImage);
            if(recepeImage.getVisibility() == View.GONE){
                recepeImage.setVisibility(View.VISIBLE);
                imageDeleteBtn.setVisibility(View.VISIBLE);

            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    private boolean checkInput(){
        title = recepeTitle.getText().toString().trim();
        desc = recepeDesc.getText().toString().trim();
        ingr = recepeIngr.getText().toString().trim();
        inst = recepeInst.getText().toString().trim();
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
            return false;
        }
        if(desc.isEmpty()){
            recepeDesc.setError("Specialkost behöver fyllas i");
            recepeDesc.requestFocus();
            return false;
        }
        if(ingr.isEmpty()){
            recepeIngr = (EditText) findViewById(R.id.ingredientID);
            recepeIngr.setError("Recepttitel behöver fyllas i");
            recepeIngr.requestFocus();
            return false;
        }
        if(inst.isEmpty()){
            recepeInst.setError("Instruktioner behöver fyllas i");
            recepeInst.requestFocus();
            return false;
        }

        return true;


    }

    private void uploadRecepe(){

        if(imageUri != null){
            uniqueId = UUID.randomUUID().toString();
            fileReference = imageStorageRef.child(uniqueId + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("!!!", "2");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Bild kunde inte laddas upp", Toast.LENGTH_SHORT).show();
                    //return;
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }

        Log.d("!!!", "3");

        creator = mAuth.getCurrentUser().toString();

        db = FirebaseFirestore.getInstance();
        receptRef = db.collection("recept");
        String image = "";
        if(imageUri != null){
            image = uniqueId + "." + getFileExtension(imageUri);
        }
        Log.d("!!!", "liststorlek" + String.valueOf(tags.size()) + " " + tags.toString() );
        Recept r = new Recept(title, desc, ingrList, inst, tags, "", image, creator);
        receptRef.add(r).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Intent i = new Intent(AddRecepeActivity.this, MainActivity.class);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Recept kunde inte laddas upp", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
