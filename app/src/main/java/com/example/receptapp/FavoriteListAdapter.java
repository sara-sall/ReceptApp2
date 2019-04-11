package com.example.receptapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


        public RecepieViewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);

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

            if(v.getId() == R.id.recepieSquareMain){
                Intent intent = new Intent(v.getContext(), RecepieActivity.class);
                intent.putExtra("title", recepieItem.getTitle());
              //  intent.putExtra("image", recepieItem.getImage());
                intent.putExtra("description", recepieItem.getDescription());
              //  intent.putExtra("isFav", recepieItem.isFavorite());
                v.getContext().startActivity(intent);


            }
/*            if(v.getId() == R.id.favoriteButtonID){



                Log.d("Test", recepieItem.getTitle());
                if(!recepieItem.isFavorite()){
                    favoriteButton.setImageResource(R.drawable.ic_favorite_color_24dp);
                    Toast.makeText(v.getContext() , "Recept tillagt i favoriter", Toast.LENGTH_SHORT).show();
                    recepieItem.setFavorite(true);
                }else{
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    Toast.makeText(v.getContext() , "Recept borttaget ur favoriter", Toast.LENGTH_SHORT).show();
                    recepieItem.setFavorite(false);
                }
            }*/
           //





        }



    }

    public FavoriteListAdapter(List<Recept> recepieList) {
        this.recepieList = recepieList;


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_square, viewGroup, false);

        RecepieViewHolder recepieViewHolder = new RecepieViewHolder(view);
        context = viewGroup.getContext();
        return recepieViewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        RecepieViewHolder vh = (RecepieViewHolder) viewHolder;
        vh.textView.setText("Du har inga favoriter sparade!");
        vh.textView.setText(recepieList.get(i).getTitle());
        vh.textView2.setText(recepieList.get(i).getDescription());
      //  vh.imageView.setImageResource(recepieList.get(i).getImage());
       // vh.favoriteButton.setImageResource(recepieList.get(i).getFavoriteImage());
    }

    @Override
    public int getItemCount() {
        return recepieList.size();
    }
}
