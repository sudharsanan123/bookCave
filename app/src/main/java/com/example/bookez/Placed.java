package com.example.bookez;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Placed extends AppCompatActivity {
    Button backtomenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed);
        Objects.requireNonNull(getSupportActionBar()).hide();

        backtomenu= findViewById(R.id.backtomenu);
        backtomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Placed.this, HomeCustomer.class);
                startActivity(i);
            }
        });
    }
}
