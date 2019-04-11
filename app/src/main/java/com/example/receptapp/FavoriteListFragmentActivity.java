package com.example.receptapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FavoriteListFragmentActivity extends Fragment{


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecepieListAdapter adapter;
    private DividerItemDecoration itemDecoration;
    private ImageView favoriteButton;
    private ArrayList<Recept> receptLista;

    private FirebaseFirestore db; // = FirebaseFirestore.getInstance();
//    private CollectionReference receptRef = db.collection("recept");

    private String title;
    private String desc;
    private ArrayList<String> ingredients;
    private String inst;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View v =  inflater.inflate(R.layout.activity_recepie_list, container, false);
        FirebaseApp.initializeApp(container.getContext());
//        db = FirebaseFirestore.getInstance();

  //      final Recept recept = new Recept(title, desc, ingredients, inst);

        final ArrayList<Recept> receptLista;
        receptLista = new ArrayList<Recept>();
/*
//         public void loadRecepes(View v){
        receptRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Recept recept = documentSnapshot.toObject(Recept.class);
                            receptLista.add(recept);
                        }
                    }
                });


//         }*/




            recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewID);
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);

            //itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL );
            //recyclerView.addItemDecoration(itemDecoration);

            adapter = new RecepieListAdapter(receptLista);
            recyclerView.setAdapter(adapter);


        return v;

    }
}
