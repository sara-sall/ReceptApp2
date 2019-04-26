package com.example.receptapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class RecipeListFragmentActivity extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecipeListAdapter adapter;
    private Toolbar toolbar;

    private ArrayList<Recept> receptLista;

    private FloatingActionButton fab;

    private FirebaseFirestore db;
    private CollectionReference receptRef;

    private ArrayList<String> ingredients;
    private ArrayList<String> tags;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.activity_recepie_list, container, false);
        FirebaseApp.initializeApp(container.getContext());
        db = FirebaseFirestore.getInstance();
        receptRef = db.collection("recept");

        toolbar = (Toolbar) v.findViewById(R.id.toolbarID);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.toolbarTitleRecipe);
        setHasOptionsMenu(true);

        fab = v.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddRecipeActivity.class);
                v.getContext().startActivity(intent);
            }
        });


        receptLista = new ArrayList<Recept>();

        ingredients = new ArrayList<String>();
        tags = new ArrayList<String>();

        String title = "Pannkakor";

        String desc = "Ägg, Gluten, Laktos";

        tags.add("vegetarisk");

        ingredients.add("6 dl Mjölk");
        ingredients.add("3 st Ägg");
        ingredients.add("3 dl Mjöl");
        ingredients.add("2 tsk Vaniljsocker");
        ingredients.add("1 tsk Salt");

        String inst = "Blanda mjöl, salt och vaniljsocker tillsammans med hälften av mjölken till en jämn smet.\n\n"+
                "Tillsätt sedan resten av mjölken och äggen\n\n" +
                "Stek pannkakorna i stekpanna och servera sedan med sylt och glass!";


        Recept r = new Recept(title, desc, ingredients, inst, tags, "", "", "admin");

        //receptRef.add(r);

        Log.d("test", "get");


        Log.d("test", "receptlista2 " + receptLista.size());


        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewID);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecipeListAdapter(receptLista);
        recyclerView.setAdapter(adapter);


        receptRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                    adapter.notifyDataSetChanged();
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
                                adapter.notifyDataSetChanged();
                            }

                            break;
                    }



                }
            }
        });



        return v;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        item.setVisible(true);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("test", "onQueryTextSubmit: 1 ");
                receptRef = db.collection("recept");
                receptRef.whereArrayContains("tags", query.toLowerCase()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot q = task.getResult();
                        receptLista.clear();
                        for (DocumentSnapshot d : q.getDocuments()) {
                            Recept recept = d.toObject(Recept.class);
                            receptLista.add(recept);
                            recept.setRecepeID(d.getId());
                        }
                        Log.d("test", "receptlista " + receptLista.size());
                        adapter.notifyDataSetChanged();
                    }
                });

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("test", "onQueryTextChange: 1 ");
/*
                receptRef.whereGreaterThanOrEqualTo("title", newText.toLowerCase()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot q = task.getResult();
                        receptLista.clear();
                        for (DocumentSnapshot d : q.getDocuments()) {
                            Recept recept = d.toObject(Recept.class);
                            receptLista.add(recept);
                            recept.setRecepeID(d.getId());
                        }
                        Log.d("test", "receptlista " + receptLista.size());
                        adapter.notifyDataSetChanged();
                    }
                });*/

                return false;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("test", "onClose: true");
                receptRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot q = task.getResult();
                        receptLista.clear();
                        for (DocumentSnapshot d : q.getDocuments()) {
                            Recept recept = d.toObject(Recept.class);
                            receptLista.add(recept);
                            recept.setRecepeID(d.getId());
                        }
                        Log.d("test", "receptlista " + receptLista.size());
                        adapter.notifyDataSetChanged();

                    }
                });
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
