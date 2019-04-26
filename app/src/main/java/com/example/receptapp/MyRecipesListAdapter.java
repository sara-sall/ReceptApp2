package com.example.receptapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class MyRecipesListAdapter extends RecyclerView.Adapter {

    private static List<Recept> recipeList;
    private Context context;

    public static class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;

        private ImageView imageView;
        private ImageView favoriteButton;
        private ImageView deleteButton;
        private CardView main;
        private String recipeID;
        private TextView editRecipe;

        private FirebaseAuth mAuth;
        public String user;

        private FirebaseFirestore db;
        private CollectionReference favoriteRef;
        private CollectionReference recipeRef;

        private StorageReference imageRef;

        private boolean haveImage;


        public RecipeViewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser().getUid();

            db = FirebaseFirestore.getInstance();
            favoriteRef = db.collection("users").document(user).collection("favorites");
            recipeRef = db.collection("recept");

            editRecipe = itemView.findViewById(R.id.editRecepe);
            textView = itemView.findViewById(R.id.recepieSquareTitle);
            imageView = itemView.findViewById(R.id.recepieSquareImage);
            favoriteButton = itemView.findViewById(R.id.favoriteButtonID);
            deleteButton = itemView.findViewById(R.id.imageDeleteButtonID);
            main = itemView.findViewById(R.id.recepieSquareMain);

            main.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
            editRecipe.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            final Recept recepieItem = recipeList.get(position);
            recipeID = recepieItem.getRecepeID();
            if (v.getId() == R.id.recepieSquareMain) {
                Intent intent = new Intent(v.getContext(), RecipeActivity.class);
                intent.putExtra("recepeID", recipeID);
                v.getContext().startActivity(intent);

            }

            if(v.getId()==R.id.editRecepe){
                Intent intent = new Intent(v.getContext(), AddRecipeActivity.class);
                intent.putExtra("recepeID", recipeID);
                v.getContext().startActivity(intent);
            }

            if (v.getId() == R.id.favoriteButtonID) {
                final View view;
                view = (ImageView) v.findViewById(R.id.favoriteButtonID);

                CollectionReference ref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("favorites");
                Query q = ref.whereEqualTo("recepeID", recipeList.get(position).getRecepeID());

                q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("recepeID", recipeID);
                            favoriteRef.document(recipeID).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    favoriteButton.setImageResource(R.drawable.ic_favorite_color_24dp);
                                    Toast.makeText(view.getContext(), R.string.addFavorite, Toast.LENGTH_LONG).show();

                                }
                            });
                        } else {
                            favoriteRef.document(recipeID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(view.getContext(), R.string.removeFavorite, Toast.LENGTH_LONG).show();
                                    favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                }
                            });
                        }

                    }
                });

            }

            if(v.getId()==R.id.imageDeleteButtonID){
               haveImage = false;
                if(recepieItem.getImageLink() != null && !recepieItem.getImageLink().isEmpty()){
                    imageRef = FirebaseStorage.getInstance().getReference().child(recepieItem.getImageLink());
                    haveImage = true;
                }
                recipeRef.document(recipeID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(haveImage){
                            imageRef.delete();
                        }
                        Toast.makeText(imageView.getContext(), "Recept Borttaget", Toast.LENGTH_SHORT  ).show();

                    }
                });

            }


        }
    }

    public MyRecipesListAdapter(List<Recept> recipeList) {
        this.recipeList = recipeList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_recipe_square, viewGroup, false);

        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(view);
        context = viewGroup.getContext();
        return recipeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int in) {
        final RecipeViewHolder vh = (RecipeViewHolder) viewHolder;
        final int i = in;
        vh.textView.setText(recipeList.get(i).getTitle());

        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        if(!recipeList.get(i).getImageLink().equals("")){
            StorageReference sr = firebaseStorage.getReference().child(recipeList.get(i).getImageLink());
            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context).load(uri).resize(150, 95).onlyScaleDown().centerCrop().into(vh.imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Picasso.with(context).load(R.drawable.ic_restaurant_color_24dp).into(vh.imageView);
                    vh.imageView.setImageResource(R.drawable.ic_restaurant_color_24dp);
                }
            });
        }else{
            Picasso.with(context).load(R.drawable.ic_restaurant_color_24dp).into(vh.imageView);
            vh.imageView.setImageResource(R.drawable.ic_restaurant_color_24dp);
        }


        CollectionReference ref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("favorites");
        Query q = ref.whereEqualTo("recepeID", recipeList.get(i).getRecepeID());

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()) {
                    vh.favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                } else {
                    vh.favoriteButton.setImageResource(R.drawable.ic_favorite_color_24dp);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}
