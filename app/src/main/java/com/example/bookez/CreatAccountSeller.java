package com.example.bookez;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreatAccountSeller extends AppCompatActivity {

    EditText email,password,firstname,lastname,phno_owner,shopname,gstno,address,pincode,phno_company;
    private String semail,spassword,sfirstname,slastname,saddress,spincode,userid,sphno_owner,sphno_company,sshopname,sgstno;
    private Button createaccButton;
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_account_seller);
        //Edittext
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        phno_owner = findViewById(R.id.phno_owner);
        phno_company = findViewById(R.id.phno_company);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        shopname = findViewById(R.id.shopname);
        gstno = findViewById(R.id.gstno);
        //Button
        createaccButton = findViewById(R.id.createaccButton);

        createaccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrive the strings
                semail= email.getText().toString().trim();
                spassword= password.getText().toString().trim();
                sfirstname= firstname.getText().toString().trim();
                slastname= lastname.getText().toString().trim();
                saddress= address.getText().toString().trim();
                spincode= pincode.getText().toString().trim();
                sphno_owner= phno_owner.getText().toString().trim();
                sphno_company= phno_company.getText().toString().trim();
                sshopname= shopname.getText().toString().trim();
                sgstno= gstno.getText().toString().trim();

                //Check for empty input
                if(TextUtils.isEmpty(semail)){
                    Toast.makeText(getApplicationContext(),"Please enter the email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(spassword)){
                    Toast.makeText(getApplicationContext(),"Please enter the password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sfirstname)){
                    Toast.makeText(getApplicationContext(),"Please enter your First Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(slastname)){
                    Toast.makeText(getApplicationContext(),"Please enter your Last Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(saddress)){
                    Toast.makeText(getApplicationContext(),"Please enter your address",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(spincode)){
                    Toast.makeText(getApplicationContext(),"Please enter your pincode",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sphno_owner)){
                    Toast.makeText(getApplicationContext(),"Please enter your phone number",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sphno_company)){
                    Toast.makeText(getApplicationContext(),"Please enter your company phone number",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sshopname)){
                    Toast.makeText(getApplicationContext(),"Please enter your Shop Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sgstno)){
                    Toast.makeText(getApplicationContext(),"Please enter your GST number",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sphno_owner.length()!=10){
                    phno_owner.setError("Number must be of 10 digits");
                    return;
                }
                if(sphno_company.length()!=10){
                    phno_company.setError("Number must be of 10 digits");
                    return;
                }
                if(spincode.length()!=6){
                    pincode.setError("Pincode must be of 6 digits");
                    return;
                }
                if(sgstno.length()!=15){
                    gstno.setError("GST must be of 15 digits");
                    return;
                }

                //Save Data to Firestore
                //saveData(semail,spassword,sfirstname,slastname,sphno,saddress,spincode);
                fAuth = FirebaseAuth.getInstance();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                createaccButton.setText("Creating ...");
                fAuth.createUserWithEmailAndPassword(semail,spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("userid", userid);
                            user.put("email", semail);
                            user.put("firstname", sfirstname);
                            user.put("lastname", slastname);
                            user.put("phno", sphno_owner);
                            user.put("address", saddress);
                            user.put("pincode", spincode);
                            user.put("phno_company", sphno_company);
                            user.put("shop", sshopname);
                            user.put("gstno", sgstno);
                            user.put("usertype", "Seller");

                            //NEW
                            db.collection("Users").document(userid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        createaccButton.setText("Create Account");
                                        Toast.makeText(CreatAccountSeller.this,"Welcome to BookCave", Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut();
                                        Intent i = new Intent(CreatAccountSeller.this, CustomerLogin.class);
                                        startActivity(i);
                                    } else{
                                        createaccButton.setText("Create Account");
                                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                                        Toast.makeText(CreatAccountSeller.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            //OLD
                            //Users info = new Users(fullName,email,phone,userid); class with getter setter

                        }else {
                            Toast.makeText(CreatAccountSeller.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}

