package com.example.bookez;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeAddress extends AppCompatActivity {

    EditText textadd,textpin;
    Button submit;
    String cd,cc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        textadd=findViewById(R.id.textadd);
        textpin=findViewById(R.id.textpin);
        submit=findViewById(R.id.submit);

        cd = getIntent().getStringExtra("current_add");
        cc = getIntent().getStringExtra("current_pin");
        textadd.setText(cd);
        textpin.setText(cc);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd= textadd.getText().toString().trim();
                cc= textpin.getText().toString().trim();
                if(cc.length()<6) {
                    Toast.makeText(ChangeAddress.this, "Pincode should be of 6 digits", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(ChangeAddress.this, BuyBook.class);
                    i.putExtra("updated_add", cd);
                    i.putExtra("updated_pin", cc);
                    startActivity(i);
                }
            }
        });
    }
}
