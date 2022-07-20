package com.example.bookez.ui.seller_booklist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookez.R;
import com.example.bookez.extras.SellingBook;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class BooklistFragment extends Fragment {

    private TextView empty_messsage;
    private RecyclerView recyclerView;
    private String title;
    private String thumbnail;
    private String author;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static  final  String BASE_URL="https://www.googleapis.com/books/v1/volumes?q=";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booklist, container, false);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        empty_messsage=root.findViewById(R.id.empty_message);
        recyclerView=root.findViewById(R.id.seller_book_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        LoadList(userid);
        return root;
    }

    private void LoadList(String userid) {

        Query query=db.collection("SellingList").whereEqualTo("sellerid", userid);

        FirestoreRecyclerOptions<SellingBook> options = new FirestoreRecyclerOptions.Builder<SellingBook>().setQuery(query, SellingBook.class).build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<SellingBook,SellingBooksViewHolder>(options) {
            @Override
            public SellingBooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_book_list, parent, false);
                return new SellingBooksViewHolder(view);
            }

            @SuppressLint("DefaultLocale")
            @Override
            protected void onBindViewHolder(SellingBooksViewHolder viewHolder, int position, final SellingBook model) {
                //final String post_id = getRef(position).getKey();
                viewHolder.row_price.setText(String.format("%d INR", model.getSellingprice()));
                viewHolder.row_quantity.setText(String.format("%dpcs available", model.getQuantities()));
                String final_query=model.getBookid();
                viewHolder.row_title.setText(model.getTitle());
                viewHolder.row_author.setText(model.getAuthor());
                //load image from internet and set it into imageView using Glide
               Glide.with(requireContext()).load(model.getThumbnail()).placeholder(R.drawable.loading_shape).dontAnimate().into(viewHolder.row_thumbnail);

            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private static class SellingBooksViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView row_thumbnail;
        TextView row_title,row_author,row_price,row_quantity;

        SellingBooksViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            row_thumbnail= mView.findViewById(R.id.row_thumbnail);
            row_title=mView.findViewById(R.id.row_title);
            row_author=mView.findViewById(R.id.row_author);
            row_price=mView.findViewById(R.id.row_price);
            row_quantity=mView.findViewById(R.id.row_quantity);

        }
    }

}
