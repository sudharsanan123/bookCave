package com.example.bookez;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RentBook extends AppCompatActivity {

    public TextView oname,obname,obauthorname,ogenre,oprice,oitemprice,odeliprice,ototalprice,famount,onumber,showdays;
    public EditText oaddress,opin;
    public Button order;
    public ImageView bthumbnail;
    private int total,days;
    private SeekBar seekBar;
    private String userid,bookid,bookauthor,booktitle,image,rprice,genre,mailid,sid,dateTime,sbid;
    private int sprice,dprice,available,newsp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_book);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String uniqueid= UUID.randomUUID().toString();
        Calendar calender= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(" EEEE, dd-MM-yyyy hh:mm:ss a");
        dateTime = simpleDateFormat.format(calender.getTime());

        oname=findViewById(R.id.oname);
        onumber=findViewById(R.id.onumber);
        oaddress=findViewById(R.id.oaddress);
        obname=findViewById(R.id.obname);
        obauthorname=findViewById(R.id.obauthorname);
        ogenre=findViewById(R.id.ogenre);
        oprice=findViewById(R.id.oprice);
        opin=findViewById(R.id.opin);
        oitemprice=findViewById(R.id.oitemprice);
        odeliprice=findViewById(R.id.odeliprice);
        ototalprice=findViewById(R.id.ototalprice);
        famount=findViewById(R.id.famount);
        order=findViewById(R.id.placeorder);
        bthumbnail=findViewById(R.id.bthumbnail);
        seekBar=findViewById(R.id.seekBar);
        showdays=findViewById(R.id.showdays);

        //get the order information
        bookid = getIntent().getStringExtra("book_id");
        sbid= getIntent().getStringExtra("sellerbookid");
        bookauthor = getIntent().getStringExtra("book_author");
        booktitle= getIntent().getStringExtra("book_title");
        image= getIntent().getStringExtra("book_thumbnail");
        genre= getIntent().getStringExtra("book_cat");
        sprice= getIntent().getIntExtra("sp",0);
        dprice= getIntent().getIntExtra("dc",0);
        available = getIntent().getIntExtra("qu",0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                days=progress;
                showdays.setText(String.valueOf(days));
                newsp=sprice*progress;
                total=newsp+dprice;
                oitemprice.setText(String.format("%s ₹", String.valueOf(newsp)));
                ototalprice.setText(String.format("%s ₹", String.valueOf(total)));
                famount.setText(String.format("₹ %s", String.valueOf(total)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //set text
        Glide.with(RentBook.this).load(image).placeholder(R.drawable.loading_shape).dontAnimate().into(bthumbnail);
        obname.setText(booktitle);
        obauthorname.setText(bookauthor);
        ogenre.setText(genre);
        oprice.setText(String.format("%s ₹", String.valueOf(sprice)));
        ogenre.setText("");
        oitemprice.setText(String.format("%s ₹", String.valueOf(sprice)));
        odeliprice.setText(String.format("%s ₹", String.valueOf(dprice)));
        //total=sprice+dprice;
        ototalprice.setText(String.format("%s ₹", String.valueOf(total)));
        famount.setText(String.format("₹ %s", String.valueOf(total)));

        //get all the data
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        //For user/customer
        DocumentReference docRef = db.collection("Users").document(userid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("firstname")+" "+documentSnapshot.getString("lastname");
                    String phno = documentSnapshot.getString("phno");
                    String pc = documentSnapshot.getString("pincode");
                    String ad = documentSnapshot.getString("address");
                    mailid = documentSnapshot.getString("email");
                    oname.setText(fullName);
                    onumber.setText(phno);
                    oaddress.setText(ad);
                    opin.setText(pc);
                }
            }
        });

        //For book details
        DocumentReference docRef1 = db.collection("SellingList").document(sbid);
        docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) { sid = documentSnapshot.getString("sellerid"); }
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(opin.getText().toString().trim().length()<6) {
                    Toast.makeText(RentBook.this, "Pincode should be of 6 digits", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(oaddress.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(),"Please enter your address",Toast.LENGTH_LONG).show();
                    return;
                }
                Map<String, Object> order = new HashMap<>();
                //unique ids
                order.put("orderid", uniqueid);
                order.put("bookid", bookid);
                order.put("sellerid", sid);
                order.put("customerid", userid);
                //book info can be taken from SellingList
                //customer info
                order.put("address", oaddress.getText().toString().trim());
                order.put("pincode", opin.getText().toString().trim());
                order.put("phno", onumber.getText().toString().trim());
                order.put("email", mailid);
                //order info
                order.put("type", "Rent");
                order.put("price", sprice);
                order.put("deliverycharge", dprice);
                order.put("totalamount", total);
                order.put("orderedat", dateTime);
                order.put("status", "Order placed");
                order.put("accepted", 0);
                order.put("updatedat", dateTime);

                order.put("docverified",0);
                order.put("days",days);
                int otp = (int) (Math.random() * 9000) + 1000;
                order.put("otp", String.valueOf(otp));

                //Get all the details of the order and save it in data
                db.collection("Orders").document(uniqueid).set(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RentBook.this, "Order placed \uD83C\uDF89", Toast.LENGTH_LONG).show();

                            db.collection("SellingList").document(sbid).update("quantities", FieldValue.increment(-1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(RentBook.this, Placed.class);
                                        startActivity(i);
                                    }
                                }
                            });
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RentBook.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //Remove 1 quantity from the ordered book's name
            }
        });
    }
}
