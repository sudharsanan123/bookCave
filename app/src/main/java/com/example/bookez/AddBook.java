package com.example.bookez;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookez.extras.PreviewBook;
import com.example.bookez.extras.SellingBook;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.UUID;

public class AddBook extends AppCompatActivity {

    TextView title;
    ImageView thumbnail;
    Button previewBtn;
    private EditText uquantity,uprice,urprice,udprice;
    private Button updateBtn;
    private FirebaseAuth fAuth;
    private String userid;
    final String uniqueid= UUID.randomUUID().toString();
    String up1,urp1,udp1,uq1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        final Intent intent=getIntent();

        final String book_title =intent.getStringExtra("book_title");
        final String image =intent.getStringExtra("book_thumbnail");
        final String book_id =intent.getStringExtra("book_isbn");
        final String preview = intent.getStringExtra("book_preview");
        final String book_author = intent.getStringExtra("book_author");
        final String book_desc = intent.getStringExtra("book_desc");
        final String book_cat = intent.getStringExtra("book_categories");

        title=findViewById(R.id.title);
        uquantity=findViewById(R.id.uquantity);
        uprice=findViewById(R.id.uprice);
        urprice=findViewById(R.id.urprice);
        udprice=findViewById(R.id.udprice);
        updateBtn=findViewById(R.id.updateBtn);
        previewBtn=findViewById(R.id.previewBtn);
        thumbnail=findViewById(R.id.thumbnail);


        title.setText(book_title);
        Glide.with(AddBook.this).load(image).placeholder(R.drawable.loading_shape).dontAnimate().into(thumbnail);
        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddBook.this , PreviewBook.class);
                i.putExtra("book_preview" ,preview);
                startActivity(i);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uq1=uquantity.getText().toString().trim();
                up1=uprice.getText().toString().trim();
                urp1=urprice.getText().toString().trim();
                udp1=udprice.getText().toString().trim();
                int uq=Integer.parseInt(uq1);
                int up=Integer.parseInt(up1);
                int urp=Integer.parseInt(urp1);
                int udp=Integer.parseInt(udp1);
                //Check for empty input
                if(TextUtils.isEmpty(uq1)){
                    Toast.makeText(getApplicationContext(),"Please enter the quantity",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(up1)){
                    Toast.makeText(getApplicationContext(),"Please enter the selling price",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(urp1)){
                    Toast.makeText(getApplicationContext(),"Please enter the renting price",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(udp1)){
                    Toast.makeText(getApplicationContext(),"Please enter the delivery charges",Toast.LENGTH_SHORT).show();
                    return;
                }

                //Save Data to Firestore
                fAuth = FirebaseAuth.getInstance();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                userid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                updateBtn.setText("Adding new stock...");

                SellingBook sellingBook=new SellingBook(book_id,uniqueid,userid,uq,up,urp,udp,book_title,book_author,book_desc,book_cat,image,preview);
                /*
                Map<String, Object> book = new HashMap<>();
                book.put("sellerid", userid);
                book.put("bookid", isbndata);
                book.put("quantities", uq);
                book.put("sellingprice", up);
                book.put("rentingprice", urp);
                book.put("deliverycharges", udp);
                 */

                db.collection("SellingList").document(uniqueid).set(sellingBook).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateBtn.setText("Add book to selling list");
                        if(task.isSuccessful()) {
                            Toast.makeText(AddBook.this,"Stock added successfully", Toast.LENGTH_LONG).show();
                            Intent i =new Intent(AddBook.this, HomeSeller.class);
                        } else{
                            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(AddBook.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
