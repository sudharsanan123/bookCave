package com.example.bookez.profile;

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

public class PaymentHistoryC extends AppCompatActivity {

    private RecyclerView recyler_payment_history_c;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    public String reason;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history_c);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefreshPaymentHis);
        recyler_payment_history_c = findViewById(R.id.recyler_payment_history_c);
        final String userid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        showPaymentHistory(userid);
        recyler_payment_history_c.setHasFixedSize(true);
        recyler_payment_history_c.setLayoutManager(new LinearLayoutManager(this));
        recyler_payment_history_c.setAdapter(adapter);
        //End of adapter code

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showPaymentHistory(userid);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void showPaymentHistory(String userid) {
        Query query = firebaseFirestore.collection("Orders").whereEqualTo("customerid", userid);

        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Order, PaymentsViewHolder>(options) {
            @NotNull
            @Override
            public PaymentsViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_payment_cus_list, parent, false);

                return new PaymentsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NotNull final PaymentsViewHolder viewHolder, int position, @NotNull Order model) {
                //get id and query to set book name and author
                viewHolder.row_amount.setText(String.format("%s ₹", String.valueOf(model.getPrice())));
                viewHolder.row_paidon.setText(String.valueOf(model.getUpdatedat()));
                String status=String.valueOf(model.getStatus());

                if(status.equals("Delivered"))
                {
                    viewHolder.row_pstatus.setText("Paid");
                }else{
                    viewHolder.row_pstatus.setText("To be paid");
                }
                //For book details
                firebaseFirestore.collection("SellingList").whereEqualTo("bookid",model.getBookid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                reason = document.getString("title");
                                viewHolder.row_for.setText(String.format("For: %s", reason));
                            }
                        }
                    }
                });

            }
        };
        adapter.startListening();
        recyler_payment_history_c.setAdapter(adapter);
    }

    public static class PaymentsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        LinearLayout container; //row_payment_cus_list
        TextView row_amount,row_paidon,row_for,row_pstatus;

        PaymentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            container=mView.findViewById(R.id.container);
            row_amount=mView.findViewById(R.id.row_amount);
            row_paidon=mView.findViewById(R.id.row_paidon);
            row_for=mView.findViewById(R.id.row_for);
            row_pstatus=mView.findViewById(R.id.row_pstatus);

        }
    }
}
