package com.example.receptapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RecipeListAdapter extends RecyclerView.Adapter {

    private static List<Recept> recepieList;
    private Context context;

    public static class RecepieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;
        private TextView textView2;
        private ImageView imageView;
        private ImageView favoriteButton;
        private LinearLayout main;
        private String recipeID;

        private FirebaseAuth mAuth;
        private String user;

        private FirebaseFirestore db;
        private CollectionReference favoriteRef;


        public RecepieViewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser().getUid();

            db = FirebaseFirestore.getInstance();
            favoriteRef = db.collection("users").document(user).collection("favorites");

            textView = itemView.findViewById(R.id.recepieSquareTitle);
            textView2 = itemView.findViewById(R.id.recepieSquareDesc);
            imageView = itemView.findViewById(R.id.recepieSquareImage);
            favoriteButton = itemView.findViewById(R.id.favoriteButtonID);
            main = itemView.findViewById(R.id.recepieSquareMain);

            main.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            final Recept recepieItem = recepieList.get(position);
            recipeID = recepieItem.getRecepeID();
            if (v.getId() == R.id.recepieSquareMain) {
                Intent intent = new Intent(v.getContext(), RecipeActivity.class);
                intent.putExtra("recepeID", recipeID);
                v.getContext().startActivity(intent);

            }

            if (v.getId() == R.id.favoriteButtonID) {
                final View view;
                view = (ImageView) v.findViewById(R.id.favoriteButtonID);

                CollectionReference ref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("favorites");
                Query q = ref.whereEqualTo("recepeID", recepieList.get(position).getRecepeID());

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


        }
    }

    public RecipeListAdapter(List<Recept> recepieList) {
        this.recepieList = recepieList;


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_square, viewGroup, false);

        RecepieViewHolder recepieViewHolder = new RecepieViewHolder(view);
        context = viewGroup.getContext();
        return recepieViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int in) {
        final RecepieViewHolder vh = (RecepieViewHolder) viewHolder;
        final int i = in;
        vh.textView.setText(recepieList.get(i).getTitle());
        vh.textView2.setText(recepieList.get(i).getDescription());


        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        if(!recepieList.get(i).getImageLink().equals("")){
            StorageReference sr = firebaseStorage.getReference().child(recepieList.get(i).getImageLink());
            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context).load(uri).resize(150, 110).onlyScaleDown().centerCrop().into(vh.imageView);
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
        Query q = ref.whereEqualTo("recepeID", recepieList.get(i).getRecepeID());

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
        return recepieList.size();
    }
}
