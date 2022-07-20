package com.example.bookez;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookInfoOrder extends AppCompatActivity {

    ImageView bthumbnail;
    TextView btitle,bcategory,bprice,bauthor,bdesc,sname,rp,dc,sadd;
    Button bshow,buybook,rentbook;
    String sn,sa;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info_order);

        bthumbnail=findViewById(R.id.bthumbnail);
        btitle=findViewById(R.id.btitle);
        bcategory=findViewById(R.id.bcategory);
        bprice=findViewById(R.id.bprice);
        bauthor=findViewById(R.id.bauthor);
        bdesc=findViewById(R.id.bdesc);
        bshow=findViewById(R.id.bshow);
        sname=findViewById(R.id.sname);
        sadd=findViewById(R.id.sadd);
        rp=findViewById(R.id.rp);
        dc=findViewById(R.id.dc);
        buybook=findViewById(R.id.buybook);
        rentbook=findViewById(R.id.rentbook);


        Intent intent=getIntent();
        final String book_id =intent.getStringExtra("book_id");
        final String book_title =intent.getStringExtra("book_title");
        final String image =intent.getStringExtra("book_thumbnail");
        final String book_author = intent.getStringExtra("book_author");
        final String book_desc = intent.getStringExtra("book_desc");
        final String preview = intent.getStringExtra("link");
        final String book_cat = intent.getStringExtra("book_cat");
        final String sellerbookid = intent.getStringExtra("sellerbookid");

        final String sellerid=intent.getStringExtra("seller");
        final int sprice = intent.getIntExtra("sp", 0);
        final int rprice = intent.getIntExtra("rp", 0);
        final int dprice = intent.getIntExtra("dc", 0);
        final int quantities = intent.getIntExtra("qu", 0);


        bshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(preview);
               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               startActivity(intent);
            }
        });

        assert sellerid != null;
        DocumentReference docRef = db.collection("Users").document(sellerid);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                        sn = documentSnapshot.getString("shop");
                        sa = documentSnapshot.getString("address");
                        sname.setText(sn);
                        sadd.setText(sa);
                }
                }
            });

        btitle.setText(book_title);
        bcategory.setText(book_cat);
        bauthor.setText(book_author);
        bdesc.setText(book_desc);
        bprice.setText(String.format("Selling @ %s INR", sprice));
        rp.setText(String.format("Renting price: %s INR per day", rprice));
        dc.setText(String.format("Delivery charges: %s INR", dprice));

        Glide.with(BookInfoOrder.this).load(image).placeholder(R.drawable.loading_shape).dontAnimate().into(bthumbnail);

        buybook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantities==0)
                {
                    Toast.makeText(BookInfoOrder.this, "Sorry, book is not currently available \uD83D\uDE22", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(BookInfoOrder.this, BuyBook.class);
                    i.putExtra("book_id", book_id);
                    i.putExtra("book_author", book_author);
                    i.putExtra("book_title", book_title);
                    i.putExtra("book_thumbnail", image);
                    i.putExtra("book_cat", book_cat);

                    i.putExtra("seller", sellerid);
                    i.putExtra("sellerbookid", sellerbookid);
                    i.putExtra("rp", rprice);
                    i.putExtra("sp", sprice);
                    i.putExtra("dc", dprice);
                    i.putExtra("qu", quantities);
                    startActivity(i);
                }
            }
        });

        rentbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantities==0)
                {
                    Toast.makeText(BookInfoOrder.this, "Sorry, book is not currently available \uD83D\uDE22", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(BookInfoOrder.this, RentBook.class);
                    i.putExtra("book_id", book_id);
                    i.putExtra("book_author", book_author);
                    i.putExtra("book_title", book_title);
                    i.putExtra("book_thumbnail", image);
                    i.putExtra("book_cat", book_cat);

                    i.putExtra("seller", sellerid);
                    i.putExtra("sellerbookid", sellerbookid);
                    i.putExtra("rp", rprice);
                    i.putExtra("sp", sprice);
                    i.putExtra("dc", dprice);
                    i.putExtra("qu", quantities);
                    startActivity(i);
                }
            }
        });
    }
}
