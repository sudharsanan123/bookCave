package com.example.bookez;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class CustomerLogin extends AppCompatActivity {
    String userid;
    EditText emailInput,passwordInput;
    Button forgotpassLink,createaccLink,createaccsellerLink,loginButton;
    ProgressBar LogInProgress;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        //Hide the ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //ViewBinding
        emailInput = findViewById(R.id.emailInput);
        LogInProgress = findViewById(R.id.LogInProgress);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        createaccLink = findViewById(R.id.createaccLink);
        createaccsellerLink = findViewById(R.id.createselleraccLink);
        forgotpassLink = findViewById(R.id.forgotpassLink);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(CustomerLogin.this, "Welcome back", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(CustomerLogin.this, HomeCustomer.class);
                    startActivity(i);
                    finish();
                } else{
                    Toast.makeText(CustomerLogin.this, "firebase user -"+mFirebaseUser, Toast.LENGTH_SHORT).show();
                }
            }
        };

        forgotpassLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CustomerLogin.this, forgot_password.class);
                startActivity(i);
            }
        });

        createaccLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CustomerLogin.this, CreateAccountCustomer.class);
                startActivity(i);
            }
        });

        createaccsellerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CustomerLogin.this, CreatAccountSeller.class);
                startActivity(i);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Email ID is Required.");
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    passwordInput.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    passwordInput.setError("Password Must be more than 6 Characters");
                    return;
                }

                LogInProgress.setVisibility(View.VISIBLE);
                loginButton.setText("Signing in...");
                // authenticate the user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            userid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                            DocumentReference typeref = db.collection("Users").document(userid);
                            typeref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            String type= documentSnapshot.getString("usertype");
                                            Toast.makeText(CustomerLogin.this, "Logged in Successfully as "+type, Toast.LENGTH_SHORT).show();
                                            assert type != null;
                                            if(type.equals("Customer")){
                                                Intent intent = new Intent(CustomerLogin.this, HomeCustomer.class);
                                                startActivity(intent);
                                                finish();
                                            }else if(type.equals("Seller")){
                                                Intent intent = new Intent(CustomerLogin.this, HomeSeller.class);
                                                intent.putExtra("user_id" ,userid);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        loginButton.setText("Sign In");
                                }
                            });

                        } else {
                            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(CustomerLogin.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                        loginButton.setText("Sign In");
                        LogInProgress.setVisibility(View.GONE);
                    }
                });

            }
        });

    }
}
