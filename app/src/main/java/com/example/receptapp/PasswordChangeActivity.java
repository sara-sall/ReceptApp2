package com.example.receptapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class PasswordChangeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    private EditText passwordInput;
    private EditText passwordInput2;

    private ProgressBar progressBar;

    private Button changePassword;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        toolbar = (Toolbar) findViewById(R.id.toolbarID);
        toolbar.setTitle("Byt lösenord");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        passwordInput = (EditText) findViewById(R.id.passwordCh);
        passwordInput2 = (EditText) findViewById(R.id.passwordCh2);
        progressBar = (ProgressBar) findViewById(R.id.progressbarCh);


        changePassword = (Button) findViewById(R.id.changePasswordID);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordChange();
            }
        });

    }

    private void passwordChange(){
        String password = passwordInput.getText().toString().trim();
        String password2 = passwordInput2.getText().toString().trim();

        if(password.isEmpty()){
            passwordInput.setError("Lösenord behöver fyllas i");
            passwordInput.requestFocus();
            return;
        }

        if(password.length()<6){
            passwordInput.setError("Lösenord behöver vara minst 6 tecken");
            passwordInput.requestFocus();
            return;
        }
        if(!password.equals(password2)){
            passwordInput2.setError("Lösenorden måste vara lika");
            passwordInput2.requestFocus();
            return;

        }

        mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Lösenordet ändrat", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Något gick fel", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
