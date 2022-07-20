package com.example.bookez;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class UpdateOrder extends AppCompatActivity {
    String orderid,bookid,bookname,updateto,dateTime,mailid,fullname;
    TextView ssellerid,sstatus,sbookname,sorderon,sname,sphno,semail,saddress,spin,stotalamount,sotp;
    Spinner updatespinner;
    Button supdate;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);
        orderid = getIntent().getStringExtra("orderid");

        Calendar calender= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(" EEEE, dd-MM-yyyy hh:mm:ss a");
        dateTime = simpleDateFormat.format(calender.getTime());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        ssellerid=findViewById(R.id.ssellerid);
        updatespinner=findViewById(R.id.updatespinner);
        supdate=findViewById(R.id.supdate);
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
                    int ta = documentSnapshot.getLong("totalamount").intValue();
                    bookid = documentSnapshot.getString("bookid");

                    sotp.setText(String.valueOf(otp));
                    stotalamount.setText(String.format("Total Amount: ₹ %s", String.valueOf(ta)));
                    spin.setText(pc);
                    saddress.setText(ad);
                    semail.setText(mailid);
                    sphno.setText(phno);
                    sorderon.setText(oa);
                    sstatus.setText(String.format("%s: ", s));
                    getname();
                }
            }
        });

        ssellerid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getname();
            }
        });

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Order placed");
        arrayList.add("Processing");
        arrayList.add("Packed");
        arrayList.add("Out for delivery!");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UpdateOrder.this,android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updatespinner.setAdapter(arrayAdapter);
        updatespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateto=parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        supdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("Orders").document(orderid).update("updatedat",dateTime,"status",updateto).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(UpdateOrder.this, "Status updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void getname()
    {
        db.collection("SellingList").whereEqualTo("bookid",bookid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        bookname = document.getString("title");

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
