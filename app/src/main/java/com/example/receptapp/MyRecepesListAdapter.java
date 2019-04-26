package com.example.receptapp;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class MyRecepesListAdapter extends RecyclerView.Adapter {

    private static List<Recept> recepieList;
    private Context context;

    public static class RecepieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;
        public TextView textView2;
        public ImageView imageView;
        public ImageView favoriteButton;
        public ImageView deleteButton;
        public CardView main;
        public String recepieID;
        private boolean isFavorite;
        private ArrayList<String> favoriteList;

        private FirebaseAuth mAuth;
        public String user;

        private FirebaseFirestore db;
        private CollectionReference favoriteRef;
        private CollectionReference recepeRef;

        private StorageReference imageRef;

        public Context mContext;



        public RecepieViewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser().getUid();

            db = FirebaseFirestore.getInstance();
            favoriteRef = FirebaseFirestore.getInstance().collection("users").document(user).collection("favorites");
            recepeRef = FirebaseFirestore.getInstance().collection("recept");



            favoriteList = new ArrayList<String>();

            textView = itemView.findViewById(R.id.recepieSquareTitle);
            imageView = itemView.findViewById(R.id.recepieSquareImage);
            favoriteButton = itemView.findViewById(R.id.favoriteButtonID);
            deleteButton = itemView.findViewById(R.id.imageDeleteButtonID);
            main = itemView.findViewById(R.id.recepieSquareMain);

            main.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            mContext = itemView.getContext();


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            final Recept recepieItem = recepieList.get(position);
            recepieID = recepieItem.getRecepeID();
            if (v.getId() == R.id.recepieSquareMain) {
                Intent intent = new Intent(v.getContext(), RecepieActivity.class);
                intent.putExtra("recepeID", recepieID);
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
                            data.put("recepeID", recepieID);
                            favoriteRef.document(recepieID).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    favoriteButton.setImageResource(R.drawable.ic_favorite_color_24dp);
                                    Toast.makeText(view.getContext(), R.string.addFavorite, Toast.LENGTH_LONG).show();

                                }
                            });
                        } else {
                            favoriteRef.document(recepieID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
                imageRef = FirebaseStorage.getInstance().getReference().child(recepieItem.getImageLink());
                recepeRef.document(recepieID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        imageRef.delete();
                        Toast.makeText(imageView.getContext(), "Recept Borttaget", Toast.LENGTH_SHORT  ).show();

                    }
                });

            }


        }
    }

    public MyRecepesListAdapter(List<Recept> recepieList) {
        this.recepieList = recepieList;


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_recepie_square, viewGroup, false);

        RecepieViewHolder recepieViewHolder = new RecepieViewHolder(view);
        context = viewGroup.getContext();
        return recepieViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int in) {
        final RecepieViewHolder vh = (RecepieViewHolder) viewHolder;
        final int i = in;
        vh.textView.setText(recepieList.get(i).getTitle());

        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        if(!recepieList.get(i).getImageLink().equals("")){
            StorageReference sr = firebaseStorage.getReference().child(recepieList.get(i).getImageLink());
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
