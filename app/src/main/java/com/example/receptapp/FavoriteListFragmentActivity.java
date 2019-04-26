package com.example.receptapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class FavoriteListFragmentActivity extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecepieListAdapter adapterF;
    private Toolbar toolbar;
    private ArrayList<Recept> receptLista;
    private ArrayList<String> favoritLista;

    private FloatingActionButton fab;

    private FirebaseFirestore db;
    private CollectionReference receptRef;
    private CollectionReference favoriteRef;

    private FirebaseAuth mAuth;
    public String user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.activity_recepie_list, container, false);
        FirebaseApp.initializeApp(container.getContext());

        toolbar = (Toolbar) v.findViewById(R.id.toolbarID);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.toolbarTitleFav);
        setHasOptionsMenu(true);

        fab = v.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddRecepeActivity.class);
                intent.putExtra("isfav", true);
                v.getContext().startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();
        receptRef = db.collection("recept");
        favoriteRef = FirebaseFirestore.getInstance().collection("users").document(user).collection("favorites");


        receptLista = new ArrayList<Recept>();
        favoritLista = new ArrayList<String>();


        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewID);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapterF = new RecepieListAdapter(receptLista);
        recyclerView.setAdapter(adapterF);

        favoriteRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot value, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (DocumentChange doc : value.getDocumentChanges()) {
                    switch (doc.getType()){
                        case ADDED:
                            receptRef.document(doc.getDocument().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot d = task.getResult();
                                        if (d.exists()) {
                                            Recept recept = d.toObject(Recept.class);
                                            receptLista.add(recept);
                                            recept.setRecepeID(d.getId());
                                        }
                                    }
                                    adapterF.notifyDataSetChanged();
                                }
                            });
                            break;
                        case REMOVED:
                            Recept r = null;
                            for(Recept recept: receptLista){
                                if(recept.getRecepeID().equals(doc.getDocument().getId())){
                                    r = recept;
                                }
                            }

                            if (r != null){
                                receptLista.remove(r);
                                adapterF.notifyDataSetChanged();
                            }

                            break;
                    }



                }
            }
        });

        return v;

    }
}