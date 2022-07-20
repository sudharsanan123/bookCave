
package com.example.bookez;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ViewOrderCustomer extends AppCompatActivity {
    String orderid,bookid,bookname,thumbnail,mailid,fullname,dateTime,updateto="";
    TextView ssellerid,sstatus,sbookname,sorderon,sname,sphno,semail,saddress,spin,stotalamount,sotp;
    ImageView bvthumbnail;
    Button received;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_customer);

        Calendar calender= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(" EEEE, dd-MM-yyyy hh:mm:ss a");
        dateTime = simpleDateFormat.format(calender.getTime());
        updateto="Order delivered";
        orderid = getIntent().getStringExtra("orderid");
        ssellerid=findViewById(R.id.ssellerid);
        sotp=findViewById(R.id.sotp);
        stotalamount=findViewById(R.id.stotamount);
        spin=findViewById(R.id.spin);
        saddress=findViewById(R.id.saddress);
        semail=findViewById(R.id.semail);
        sphno=findViewById(R.id.sphno);
        sname=findViewById(R.id.sname);
        sorderon=findViewById(R.id.sorderon);
        sbookname=findViewById(R.id.sbookname);
        sstatus=findViewById(R.id.sstatus);
        received=findViewById(R.id.received);
        bvthumbnail=findViewById(R.id.bvthumbnail);

        ssellerid.setText(String.valueOf(orderid));
        DocumentReference docRef = db.collection("Orders").document(orderid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String phno = documentSnapshot.getString("phno");
                    String pc = documentSnapshot.getString("pincode");
                    String ad = documentSnapshot.getString("address");
                    mailid = documentSnapshot.getString("email");
                    String oa = documentSnapshot.getString("orderedat");
                    String s = documentSnapshot.getString("status");
                    String otp = documentSnapshot.getString("otp");
                    int ta = Objects.requireNonNull(documentSnapshot.getLong("totalamount")).intValue();
                    bookid = documentSnapshot.getString("bookid");

                    sotp.setText(String.valueOf(otp));
                    stotalamount.setText(String.format("Total amount: %s", String.valueOf(ta)));
                    spin.setText(pc);
                    saddress.setText(ad);
                    semail.setText(mailid);
                    sphno.setText(phno);
                    sorderon.setText(oa);
                    sstatus.setText(String.format("%s: ", s));
                }
            }
        });

        ssellerid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });

        getdata();

        Glide.with(ViewOrderCustomer.this).load(thumbnail).placeholder(R.drawable.loading_shape).dontAnimate().into(bvthumbnail);

        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("Orders").document(orderid).update("updatedat",dateTime,"status",updateto).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ViewOrderCustomer.this, "\uD83C\uDF89 Congrats! Enjoy the Book \uD83E\uDD73", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    public void getdata()
    {
        //For book details
        db.collection("SellingList").whereEqualTo("bookid",bookid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        bookname = document.getString("title");
                        thumbnail = document.getString("thumbnail");
                    }
                }
            }
        });

        db.collection("Users").whereEqualTo("email",mailid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        fullname = document.getString("firstname")+document.getString("lastname");
                    }
                }
            }
        });
        sbookname.setText(bookname);
        sname.setText(fullname);
    }
}
