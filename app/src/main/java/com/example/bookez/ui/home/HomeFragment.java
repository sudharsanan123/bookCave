package com.example.bookez.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookez.R;
import com.example.bookez.extras.Order;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyler_approve_s;
    private TextView tprofit,torder,tpprofit;
    private FirestoreRecyclerAdapter adapter;
    public String bookname,dateTime;
    private Query query;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tprofit=root.findViewById(R.id.tprofit);
        tpprofit=root.findViewById(R.id.tpprofit);
        torder=root.findViewById(R.id.torder);
        Intent intent= requireActivity().getIntent();
        final String userid =intent.getStringExtra("user_id");
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefreshApprove);
        recyler_approve_s = root.findViewById(R.id.recyler_approve_s);
        firebaseFirestore=FirebaseFirestore.getInstance();
        Calendar calender= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(" EEEE, dd-MM-yyyy hh:mm:ss a");
        dateTime = simpleDateFormat.format(calender.getTime());
        firebaseFirestore.collection("Orders").whereEqualTo("sellerid",userid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                long sum=0;
                int count=0;
                long accepted_count=0;
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    if(documentSnapshot.getLong("price")!=null) {
                        long sp = documentSnapshot.getLong("price");
                        sum = sum + sp;
                    }
                    count++;
                    if((documentSnapshot.getLong("accepted"))==0)
                    {
                        long sp = documentSnapshot.getLong("price");
                        accepted_count = accepted_count + sp;
                    }
                }
                tprofit.setText(String.format("%d ₹", sum));
                tpprofit.setText(String.format("+%s ₹", String.valueOf(accepted_count)));
                torder.setText(String.valueOf(count));
            }
        });

        showUnacceptedOrders(userid);
        recyler_approve_s.setHasFixedSize(true);
        recyler_approve_s.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyler_approve_s.setAdapter(adapter);
        //End of adapter code

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showUnacceptedOrders(userid);
                pullToRefresh.setRefreshing(false);
            }
        });

        return root;
    }

    private void showUnacceptedOrders(String userid)
    {
        query = firebaseFirestore.collection("Orders").whereEqualTo("sellerid",userid)
                                                      .whereEqualTo("accepted",0);

        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Order, UnacceptedsViewHolder>(options) {
            @Override
            public UnacceptedsViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_approve_list, parent, false);

                return new UnacceptedsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NotNull final UnacceptedsViewHolder viewHolder, int position, Order model) {
                //get id and query to set book name and author
                viewHolder.row_orderedon.setText(model.getUpdatedat());
                viewHolder.row_add.setText(model.getAddress());
                String status=String.valueOf(model.getStatus());
                final String orderid = String.valueOf(model.getOrderid());

                //For book details
                firebaseFirestore.collection("SellingList").whereEqualTo("bookid",model.getBookid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                bookname = document.getString("title");
                                viewHolder.row_bookname.setText(String.format("For: %s", bookname));
                            }
                        }
                    }
                });


                viewHolder.acc_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore.getInstance().collection("Orders").document(orderid).update("updatedon",dateTime,"accepted",1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getActivity(), "Order accepted!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                viewHolder.dec_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore.getInstance().collection("Orders").document(orderid).update("updatedon",dateTime,"accepted",2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getActivity(), "Order accepted!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        };
        adapter.startListening();
        recyler_approve_s.setAdapter(adapter);
    }

    public class UnacceptedsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        LinearLayout container; //row_approve_list
        TextView row_bookname,row_orderedon,row_add;
        Button acc_order,dec_order;

        UnacceptedsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            container=mView.findViewById(R.id.container);
            row_bookname=mView.findViewById(R.id.row_bookname);
            row_orderedon=mView.findViewById(R.id.row_orderedon);
            row_add=mView.findViewById(R.id.row_add);
            acc_order=mView.findViewById(R.id.acc_order);
            dec_order=mView.findViewById(R.id.dec_order);
        }
    }
}
