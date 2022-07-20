package com.example.bookez;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class BookInfo extends AppCompatActivity {

    ImageView bthumbnail;
    TextView btitle,bcategory,bprice,bauthor,bdesc;
    Button bshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        bthumbnail=findViewById(R.id.bthumbnail);
        btitle=findViewById(R.id.btitle);
        bcategory=findViewById(R.id.bcategory);
        bprice=findViewById(R.id.bprice);
        bauthor=findViewById(R.id.bauthor);
        bdesc=findViewById(R.id.bdesc);
        bshow=findViewById(R.id.bshow);

        Intent intent=getIntent();
        final String book_title =intent.getStringExtra("book_title");
        final String image =intent.getStringExtra("book_thumbnail");
        final String book_id =intent.getStringExtra("book_isbn");
        final String preview = intent.getStringExtra("book_preview");
        final String price = intent.getStringExtra("book_price");
        final String book_author = intent.getStringExtra("book_author");
        final String book_desc = intent.getStringExtra("book_desc");
        final String book_cat = intent.getStringExtra("book_categories");

        bshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(preview);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btitle.setText(book_title);
        bcategory.setText(book_cat);
        bprice.setText(price);
        bauthor.setText(book_author);
        bdesc.setText(book_desc);
        Glide.with(BookInfo.this).load(image).placeholder(R.drawable.loading_shape).dontAnimate().into(bthumbnail);

    }
}
