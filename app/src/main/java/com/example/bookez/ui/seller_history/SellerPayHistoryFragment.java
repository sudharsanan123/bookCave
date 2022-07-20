package com.example.bookez.ui.seller_history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookez.R;
import com.example.bookez.extras.Order;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SellerPayHistoryFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyler_payment_history_s;
    private FirestoreRecyclerAdapter adapter;
    private String cusname;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_seller_pay_history, container, false);

        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefreshspayhis);
        recyler_payment_history_s = root.findViewById(R.id.recyler_payment_history_s);
        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String current_user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        showsellerpayhis(current_user_id);
        recyler_payment_history_s.setHasFixedSize(true);
        recyler_payment_history_s.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyler_payment_history_s.setAdapter(adapter);
        //End of adapter code

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showsellerpayhis(current_user_id);
                pullToRefresh.setRefreshing(false);
            }
        });
        return root;
    }

    private void showsellerpayhis(String userid)
    {
        Query query = firebaseFirestore.collection("Orders").whereEqualTo("sellerid", userid);

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
                viewHolder.row_amount.setText(String.format("+ %s ₹", String.valueOf(model.getPrice())));
                viewHolder.row_paidon.setText(String.valueOf(model.getUpdatedat()));
                String status=String.valueOf(model.getStatus());
                String accept=String.valueOf(model.getAccepted());
                if(status.equals("Order delivered"))
                {
                    viewHolder.row_pstatus.setText(R.string.recevied);
                }else
                {viewHolder.row_pstatus.setText(R.string.tobereceived);}
                if(accept.equals("2"))
                {
                    viewHolder.row_pstatus.setText(R.string.rejectedorder);
                }
                DocumentReference docRef1 = firebaseFirestore.collection("Users").document(model.getCustomerid());
                docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) { cusname = documentSnapshot.getString("firstname")+" "+documentSnapshot.getString("lastname");
                            viewHolder.row_for.setText(String.format("by %s", cusname));}
                    }
                });

            }
        };
        adapter.startListening();
        recyler_payment_history_s.setAdapter(adapter);
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
