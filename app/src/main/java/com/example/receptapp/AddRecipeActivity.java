package com.example.receptapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ProgressBar progressBar;

    private LinearLayout addImageBtn;
    private ImageView recipeImage;
    private ImageView imageDeleteBtn;

    private static final int PICK_IMAGE_REQUEST =1;
    private Uri imageUri;
    private String uniqueId;

    private EditText recipeTitle, recipeDesc, recipeIngr, recipeInst;

    private CheckBox cbKött, cbKyckling, cbFisk, cbLax, cbVego, cbPasta, cbPotatis, cbRis, cbBakelse, cbFrukt, cbBeef, cbPork, cbGlutenf, cbLaktosf, cbVegan;

    private LinearLayout tagsBtn;
    private TableLayout tagsLay;
    private ImageView tagArrow;

    private String creator;

    private ImageView ingrButton;

    private EditText rIngr;

    private String title, desc, ingr, inst;


    private ArrayList idList;
    private ArrayList ingrList;
    private ArrayList tags;

    private FirebaseFirestore db;
    private CollectionReference receptRef;
    private CollectionReference favoriteRef;
    private DocumentReference rRef;

    private FirebaseStorage fs;

    private StorageReference imageStorageRef;
    private StorageReference fileReference;

    private FirebaseAuth mAuth;

    private Button previewBtn;
    private Button addRecepeBtn;
    private String imageUrl;

    private Boolean isFav =false;

    private Recept recept;
    private String ingredient, recepeID;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =123;

    private int ingrNr = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("Nytt recept");
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fs = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.progressbarAR);

        mAuth = FirebaseAuth.getInstance();

        imageStorageRef = FirebaseStorage.getInstance().getReference();

        idList = new ArrayList();
        ingrList = new ArrayList();
        tags = new ArrayList();

        addImageBtn = (LinearLayout) findViewById(R.id.addImageLayout);
        addImageBtn.setOnClickListener(this);
        recipeImage = (ImageView) findViewById(R.id.imageLayout);
        imageDeleteBtn = (ImageView) findViewById(R.id.imageDeleteButton);
        imageDeleteBtn.setOnClickListener(this);

        recipeTitle = (EditText) findViewById(R.id.recepeTitleID);
        recipeDesc = (EditText) findViewById(R.id.recepeDescID);
        recipeIngr = (EditText) findViewById(R.id.ingredientID);
        recipeInst = (EditText) findViewById(R.id.recepeInstrID);

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
        cbBeef = (CheckBox) findViewById(R.id.cbBeef);
        cbPork = (CheckBox) findViewById(R.id.cbPork);
        cbLaktosf = (CheckBox) findViewById(R.id.cbLaktosfri);
        cbGlutenf = (CheckBox) findViewById(R.id.cbGlutenfri);
        cbVegan = (CheckBox) findViewById(R.id.cbVeganskt);

        tagsBtn = (LinearLayout) findViewById(R.id.tagsButtonLayout);
        tagsBtn.setOnClickListener(this);
        tagsLay = (TableLayout) findViewById(R.id.tagsLayout);
        tagArrow = (ImageView) findViewById(R.id.tagsArrow);


        ingrButton = (ImageView) findViewById(R.id.addMorIngrButton);
        ingrButton.setOnClickListener(this);

        Bundle b = getIntent().getExtras();

        if(b != null){
            if(b.getBoolean("isfav") == true){
                isFav = true;
            }

            if(b.get("recepeID") != null){
                recepeID =(String) b.get("recepeID");
                getRecepeInfo();
            }
        }

    }

    public void getRecepeInfo(){
       rRef = db.collection("recept").document(recepeID);
        rRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        recept = document.toObject(Recept.class);
                        recipeDesc.setText(recept.getDescription());
                        recipeTitle.setText(recept.getTitle());
                        recipeInst.setText(recept.getInstructions());


                        for(int i = 0; i < recept.getIngredients().size(); i++){
                            ingredient = recept.getIngredients().get(i);
                            if(i == 0){
                                recipeIngr.setText(ingredient);
                            }else{
                                TextInputEditText editText = new TextInputEditText(AddRecipeActivity.this);
                                LinearLayout ingrLayout = (LinearLayout) findViewById(R.id.ingrLayoutID);

                                TextInputLayout textInputLayout = new TextInputLayout(AddRecipeActivity.this);
                                LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);

                                textInputLayout.setLayoutParams(textInputLayoutParams);
                                textInputLayout.addView(editText);
                                editText.setId(100 + ingrNr);
                                editText.setMaxLines(1);
                                editText.setHint(R.string.addIngredient);
                                //textInputLayout.setHint(getResources().getString(R.string.addIngredient));
                                editText.setSingleLine(true);
                                editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
                                editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                ingrLayout.addView(textInputLayout);

                                idList.add(editText.getId());
                                ingrNr += 1;

                                String ID = String.valueOf(idList.get(ingrNr-1));
                                rIngr = (EditText)findViewById(Integer.parseInt(ID));
                                rIngr.setText(ingredient);


                            }
                        }

                        imageUrl = recept.getImageLink();
                        if(!imageUrl.equals("")){
                            StorageReference sr = fs.getReference().child(imageUrl);
                            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUri = uri;
                                    Picasso.with(AddRecipeActivity.this).load(uri).resize(250, 250).onlyScaleDown().centerInside().into(recipeImage);
                                    if(recipeImage.getVisibility() == View.GONE){
                                        recipeImage.setVisibility(View.VISIBLE);
                                        imageDeleteBtn.setVisibility(View.VISIBLE);

                                    }
                                }
                            });
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
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                else{
                    openFileChooser();
                }
                break;

            case R.id.imageDeleteButton:
                imageUri = null;
                imageDeleteBtn.setVisibility(View.GONE);
                recipeImage.setVisibility(View.GONE);
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
                    Intent intent = new Intent(AddRecipeActivity.this, RecipePreviewActivity.class);

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

        if(cbVegan.isChecked()){
            tags.add("vegansk");
        }

        if(cbBeef.isChecked()){
            tags.add("nöt");
        }
        if(cbPork.isChecked()){
            tags.add("fläsk");
        }
        if(cbGlutenf.isChecked()){
            tags.add("glutenfri");
        }
        if(cbLaktosf.isChecked()){
            tags.add("laktosfri");
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

            Picasso.with(this).load(imageUri).resize(250, 250).onlyScaleDown().centerInside().into(recipeImage);
            if(recipeImage.getVisibility() == View.GONE){
                recipeImage.setVisibility(View.VISIBLE);
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
        title = recipeTitle.getText().toString().trim();
        desc = recipeDesc.getText().toString().trim();
        ingr = recipeIngr.getText().toString().trim();
        inst = recipeInst.getText().toString().trim();
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
            recipeTitle.setError("Recepttitel behöver fyllas i");
            recipeTitle.requestFocus();
            return false;
        }
        if(desc.isEmpty()){
            recipeDesc.setError("Specialkost behöver fyllas i");
            recipeDesc.requestFocus();
            return false;
        }
        if(ingr.isEmpty()){
            recipeIngr = (EditText) findViewById(R.id.ingredientID);
            recipeIngr.setError("Recepttitel behöver fyllas i");
            recipeIngr.requestFocus();
            return false;
        }
        if(inst.isEmpty()){
            recipeInst.setError("Instruktioner behöver fyllas i");
            recipeInst.requestFocus();
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

        creator = mAuth.getCurrentUser().getUid();


        receptRef = db.collection("recept");
        String image = "";
        if(imageUri != null){
            image = uniqueId + "." + getFileExtension(imageUri);
        }

        Recept r = new Recept(title, desc, ingrList, inst, tags, "", image, creator);
        receptRef.add(r).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                if(recepeID != null){
                    receptRef.document(recepeID).delete();
                }

                if(isFav){
                    favoriteRef = FirebaseFirestore.getInstance().collection("users").document(creator).collection("favorites");
                    Query q = receptRef.whereEqualTo("title", title).whereEqualTo("creator", creator);

                    q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot q = task.getResult();
                                for(DocumentSnapshot d:q.getDocuments()){
                                    String recepieID = d.getId();
                                    HashMap<String, String> data = new HashMap<>();
                                    data.put("recepeID", recepieID);
                                    favoriteRef.document(recepieID).set(data);
                                }
                            }

                        }
                    });
                }
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Recept kunde inte laddas upp", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openFileChooser();
                }else{
                    addImageBtn.setVisibility(View.GONE);
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
