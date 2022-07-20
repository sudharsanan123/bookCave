package com.example.bookez.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookez.R;
import com.example.bookez.ViewOrderCustomer;
import com.example.bookez.extras.Order;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OrderHistoryC extends AppCompatActivity {
    private RecyclerView recyler_order_history_c;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth firebaseAuth;
    private String bn,ba,orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_c);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefreshOrderHis);
        recyler_order_history_c = findViewById(R.id.recyler_order_history_c);
        final String userid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        showOrderHistory(userid);
        recyler_order_history_c.setHasFixedSize(true);
        recyler_order_history_c.setLayoutManager(new LinearLayoutManager(this));
        recyler_order_history_c.setAdapter(adapter);
        //End of adapter code

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showOrderHistory(userid);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void showOrderHistory(String userid) {
        Query query = firebaseFirestore.collection("Orders").whereEqualTo("customerid", userid);

        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Order, OrdersViewHolder>(options) {
            @NotNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_order_cus_list, parent, false);

                return new OrdersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NotNull final OrdersViewHolder viewHolder, int position, @NotNull Order model) {
                final String bookid = model.getBookid();
                //For book details
                firebaseFirestore.collection("SellingList").whereEqualTo("bookid",bookid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                bn = document.getString("title");
                                ba = document.getString("author");
                                viewHolder.row_bname.setText(bn);
                                viewHolder.row_bauthor.setText(ba);
                            }
                        }
                    }
                });
                orderid=model.getOrderid();
                viewHolder.row_bstatus.setText(model.getStatus());
                viewHolder.row_bprice.setText(String.valueOf(model.getPrice()));
                viewHolder.row_updatedon.setText(String.valueOf(model.getUpdatedat()));
                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(OrderHistoryC.this, ViewOrderCustomer.class);
                        i.putExtra("orderid",orderid);
                        startActivity(i);
                    }
                });
            }
        };
        adapter.startListening();
        recyler_order_history_c.setAdapter(adapter);
    }
        public static class OrdersViewHolder extends RecyclerView.ViewHolder {
            View mView;
            LinearLayout container; //row_order_cus_list
            TextView row_bname,row_bauthor,row_bprice,row_bstatus,row_updatedon;

            OrdersViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
                container=mView.findViewById(R.id.container);
                row_bname=mView.findViewById(R.id.row_bname);
                row_bauthor=mView.findViewById(R.id.row_bauthor);
                row_bprice=mView.findViewById(R.id.row_bprice);
                row_bstatus=mView.findViewById(R.id.row_bstatus);
                row_updatedon=mView.findViewById(R.id.row_updatedon);

            }
        }
}
