package com.example.receptapp;

import android.content.Intent;
import android.media.tv.TvContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailInput, passwordInput, passwordInput2;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailInput = (EditText) findViewById(R.id.emailSU);
        passwordInput = (EditText) findViewById(R.id.passwordSU);
        passwordInput2 = (EditText) findViewById(R.id.passwordSU2);
        progressBar = (ProgressBar) findViewById(R.id.progressbarSU);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButtonID).setOnClickListener(this);
        findViewById(R.id.toLogin).setOnClickListener(this);






    }

    private void registerUser(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String password2 = passwordInput2.getText().toString().trim();

        if(email.isEmpty()){
            emailInput.setError("E-mailadress behöver fyllas i");
            emailInput.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError(("Skriv in en giltig e-mailadress"));
            emailInput.requestFocus();
            return;
        }

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



        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Intent intent = new Intent (SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "E-mailadressen är redan registrerad", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Något problem inträffade", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUpButtonID:
                registerUser();
                break;

            case R.id.toLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
