package com.example.receptapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private Button logOutButton;
    private Button passwordChangeButton;
    private Button myRecipesButton;
    private TextView userEmail;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.toolbarID);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.toolbarTitleProf);

        mAuth =  FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userEmail = (TextView) v.findViewById(R.id.userEmail);
        passwordChangeButton = (Button) v.findViewById(R.id.passwordChangeID);
        logOutButton = (Button) v.findViewById(R.id.logOutButtonID);
        myRecipesButton = (Button)v.findViewById(R.id.myRecepesButtonID);

        if(user != null){
            String email=user.getEmail();
            userEmail.setText(email);

        }

        passwordChangeButton.setOnClickListener(this);
        logOutButton.setOnClickListener(this);
        myRecipesButton.setOnClickListener(this);



        return v;
    }

    @Override
    public void onClick(View view) {
        final View v = view;
        switch (v.getId()){
            case R.id.logOutButtonID:
                mAuth.signOut();
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                break;

            case R.id.passwordChangeID:
                startActivity(new Intent(v.getContext(), PasswordChangeActivity.class));
                break;

            case R.id.myRecepesButtonID:
                startActivity(new Intent(v.getContext(), MyRecipesActivity.class));
                break;

        }
    }

}
