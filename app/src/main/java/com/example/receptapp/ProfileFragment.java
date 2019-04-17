package com.example.receptapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private Button logOutButton;
    private Button passwordChangeButton;
    private Button myRecepesButton;
    private TextView userEmail;
    private TextView emailVerified;
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
        emailVerified = (TextView) v.findViewById(R.id.emailVerifiedID);
        passwordChangeButton = (Button) v.findViewById(R.id.passwordChangeID);
        logOutButton = (Button) v.findViewById(R.id.logOutButtonID);
        myRecepesButton = (Button)v.findViewById(R.id.myRecepesButtonID);

        if(user != null){
            String email=user.getEmail();
            userEmail.setText(email);

            boolean emailVeri = user.isEmailVerified();

            if(emailVeri){
                emailVerified.setText("E-mail verifierad");
            }else{
                emailVerified.setText("E-mail inte verifierad, klicka för att verifiera");
                emailVerified.setOnClickListener(this);
            }
        }

        passwordChangeButton.setOnClickListener(this);
        logOutButton.setOnClickListener(this);
        myRecepesButton.setOnClickListener(this);



        return v;
    }

    @Override
    public void onClick(View view) {
        final View v = view;
        switch (v.getId()){
            case R.id.logOutButtonID:
                mAuth.signOut();
                startActivity(new Intent(v.getContext(), LoginActivity.class));
                break;

            case R.id.passwordChangeID:
                startActivity(new Intent(v.getContext(), PasswordChangeActivity.class));
                break;

            case R.id.myRecepesButtonID:
                startActivity(new Intent(v.getContext(), MyRecepesActivity.class));
                break;

            case R.id.emailVerifiedID:
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(v.getContext(), "Verifikations e-mail skickat", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

}
