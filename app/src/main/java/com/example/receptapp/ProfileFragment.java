package com.example.receptapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    private TextView userEmail;
    private TextView emailVerified;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth =  FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userEmail = (TextView) v.findViewById(R.id.userEmail);
        emailVerified = (TextView) v.findViewById(R.id.emailVerifiedID);
        passwordChangeButton = (Button) v.findViewById(R.id.passwordChangeID);
        logOutButton = (Button) v.findViewById(R.id.logOutButtonID);

        if(user != null){
            String email=user.getEmail();
            userEmail.setText(email);

            boolean emailVeri = user.isEmailVerified();

            if(emailVeri){
                emailVerified.setText("E-mail verifierad");
            }else{
                emailVerified.setText("E-mail inte verifierad");
            }
        }

        passwordChangeButton.setOnClickListener(this);
        logOutButton.setOnClickListener(this);



        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logOutButtonID:
                mAuth.signOut();
                startActivity(new Intent(v.getContext(), LoginActivity.class));
                break;

            case R.id.passwordChangeID:
                startActivity(new Intent(v.getContext(), PasswordChangeActivity.class));
                break;

        }
    }
}
