package com.example.bookez.ui.seller_currentorders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookez.R;
import com.example.bookez.UpdateOrder;
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

public class SellerCurrentOrdersFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyler_updateorder_s;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private String bookname;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_seller_current_orders, container, false);

        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefreshsupdaterefresh);
        recyler_updateorder_s = root.findViewById(R.id.recyler_updateorder_s);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final String current_user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        showorder(current_user_id);
        recyler_updateorder_s.setHasFixedSize(true);
        recyler_updateorder_s.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyler_updateorder_s.setAdapter(adapter);
        //End of adapter code

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showorder(current_user_id);
                pullToRefresh.setRefreshing(false);
            }
        });
        return root;
    }

    private void showorder(String userid)
    {
        Query query = firebaseFirestore.collection("Orders").whereEqualTo("sellerid", userid);

        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Order, FiltersViewHolder>(options) {
            @NotNull
            @Override
            public FiltersViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_filter_s_list, parent, false);

                return new FiltersViewHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NotNull final FiltersViewHolder viewHolder, int position, @NotNull Order model) {
                //get id and query to set book name and author

                String status=String.valueOf(model.getStatus());
                final String orderid = String.valueOf(model.getOrderid());
                final String address = model.getAddress();
                //For book details
                DocumentReference docRef1 = firebaseFirestore.collection("Orders").document(model.getBookid());
                docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            bookname = documentSnapshot.getString("title");
                            viewHolder.row_bb.setText(bookname);}
                    }
                });

                viewHolder.row_add.setText(model.getAddress());
                viewHolder.row_orderupdate.setText(model.getUpdatedat());
                viewHolder.row_add.setText(String.format("at %s . . . ", address.substring(0, 10)));
                viewHolder.row_ostatus.setText(status);
                viewHolder.row_details.setText(R.string.updatenview);

                viewHolder.row_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), UpdateOrder.class);
                        i.putExtra("orderid",orderid);
                        startActivity(i);
                    }
                });
            }
        };
        adapter.startListening();
        recyler_updateorder_s.setAdapter(adapter);
    }

    public static class FiltersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        LinearLayout container; //row_filter_s_list
        TextView row_ostatus,row_bb,row_orderupdate,row_add;
        Button row_details;

        FiltersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            container=mView.findViewById(R.id.container);

            row_ostatus=mView.findViewById(R.id.row_ostatus);
            row_bb=mView.findViewById(R.id.row_bb);
            row_orderupdate=mView.findViewById(R.id.row_orderupdate);
            row_add=mView.findViewById(R.id.row_add);
            row_details=mView.findViewById(R.id.row_details);
        }
    }
}
