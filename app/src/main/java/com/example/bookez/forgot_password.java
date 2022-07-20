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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class forgot_password extends AppCompatActivity {

    EditText resetemailInput;
    Button sentemailButton,backtolgLink;
    ProgressBar resetProgressBar;
    //Firebase
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //ViewBinding
        resetemailInput = findViewById(R.id.resetemailInput);
        sentemailButton = findViewById(R.id.sentmailButton);
        backtolgLink = findViewById(R.id.backtolgLink);
        resetProgressBar = findViewById(R.id.resetProgressBar);
        firebaseAuth=FirebaseAuth.getInstance();

        sentemailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String remail= resetemailInput.getText().toString();
                if(!TextUtils.isEmpty(remail)){
                    resetProgressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.sendPasswordResetEmail(remail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(forgot_password.this,"Check your email inbox", Toast.LENGTH_LONG).show();
                            }else{
                                String errorMessage= Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(forgot_password.this,"Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                            resetProgressBar.setVisibility(View.GONE);
                        }
                    });
                }else{

                    Toast.makeText(forgot_password.this,"Enter the email", Toast.LENGTH_LONG).show();
                }

            }
        });

        backtolgLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(forgot_password.this, CustomerLogin.class);
                startActivity(i);
            }
        });
    }
}
