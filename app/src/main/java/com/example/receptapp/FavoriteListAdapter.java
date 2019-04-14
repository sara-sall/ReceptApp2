package com.example.receptapp;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter {

    private static List<Recept> recepieList;
    private Context context;
    public static class RecepieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textView;
        public TextView textView2;
        public ImageView imageView;
        public ImageView favoriteButton;
        public CardView main;
        public String recepieID;

        private FirebaseAuth mAuth;
        public String user;

        private FirebaseFirestore db;
        private CollectionReference favoriteRef;



        public RecepieViewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser().getUid();

            db = FirebaseFirestore.getInstance();
            favoriteRef = FirebaseFirestore.getInstance().collection("users").document(user).collection("favorites");

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

            Recept recepieItem = recepieList.get(position);
            recepieID = recepieItem.getRecepeID();

            switch (v.getId()){
                case R.id.recepieSquareMain:
                    Intent intent = new Intent(v.getContext(), RecepieActivity.class);
                    intent.putExtra("recepeID", recepieID);
                    v.getContext().startActivity(intent);
                    break;

                case R.id.favoriteButtonID:
                    final View view;
                    view = (ImageView) v.findViewById(R.id.favoriteButtonID);
                    favoriteRef.document(recepieID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(view.getContext(), R.string.removeFavorite, Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("!!!", "Error deleting document", e);
                        }
                    });
                    break;
            }

/*            if(v.getId() == R.id.recepieSquareMain){
                Intent intent = new Intent(v.getContext(), RecepieActivity.class);
                intent.putExtra("recepeID", recepieID);
                v.getContext().startActivity(intent);

            }

            if(v.getId() == R.id.favoriteButtonID){
                final View view;
                view = (ImageView) v.findViewById(R.id.favoriteButtonID);
                favoriteRef.document(recepieID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(view.getContext(), R.string.removeFavorite, Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("!!!", "Error deleting document", e);
                    }
                });
            }*/

        }

    }

    public FavoriteListAdapter(List<Recept> recepieList) {
        this.recepieList = recepieList;


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recepie_square, viewGroup, false);

        RecepieViewHolder recepieViewHolder = new RecepieViewHolder(view);
        context = viewGroup.getContext();
        return recepieViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        RecepieViewHolder vh = (RecepieViewHolder) viewHolder;
        vh.textView.setText(recepieList.get(i).getTitle());
        vh.textView2.setText(recepieList.get(i).getDescription());
        vh.imageView.setImageResource(recepieList.get(i).getImage());


        if (recepieList.get(i).isFavorite()) {
            vh.favoriteButton.setImageResource(R.drawable.ic_favorite_color_24dp);
        }else if(!recepieList.get(i).isFavorite()){
            vh.favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return recepieList.size();
    }
}
