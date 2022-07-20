package com.example.bookez.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.bookez.BookInfoOrder;
import com.example.bookez.R;
import com.example.bookez.extras.SellingBook;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomecFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_homec, container, false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        RecyclerView recyler_home_page_books = root.findViewById(R.id.recyler_home_page_books);
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        showNewAvailables();
        recyler_home_page_books.setHasFixedSize(true);
        recyler_home_page_books.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyler_home_page_books.setAdapter(adapter);
        //End of adapter code

        ImageSlider slider=root.findViewById(R.id.slider);
        List<SlideModel> sliderModels=new ArrayList<>();
        sliderModels.add(new SlideModel("https://i.imgur.com/IGIu5Fb.jpg"));
        sliderModels.add(new SlideModel("https://i.imgur.com/PObprBN.jpg"));
        slider.setImageList(sliderModels, true);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showNewAvailables();
                pullToRefresh.setRefreshing(false);
            }
        });
        return root;
    }

    private void showNewAvailables() {
        Query query = firebaseFirestore.collection("SellingList");

        FirestoreRecyclerOptions<SellingBook> options = new FirestoreRecyclerOptions.Builder<SellingBook>()
                .setQuery(query, SellingBook.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<SellingBook, SellingBooksViewHolder>(options) {
            @NotNull
            @Override
            public SellingBooksViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_book_list, parent, false);
                return new SellingBooksViewHolder(view);
            }

            @SuppressLint("DefaultLocale")
            @Override
            protected void onBindViewHolder(@NotNull SellingBooksViewHolder viewHolder, int position, @NotNull final SellingBook model) {
                viewHolder.row_price.setText(String.format("%d INR", model.getSellingprice()));
                viewHolder.row_quantity.setText(String.format("%dpcs available", model.getQuantities()));
                final String final_query=model.getBookid();
                viewHolder.row_title.setText(model.getTitle());
                viewHolder.row_author.setText(model.getAuthor());
                //load image from internet and set it into imageView using Glide
                Glide.with(requireActivity()).load(model.getThumbnail()).placeholder(R.drawable.loading_shape).dontAnimate().into(viewHolder.row_thumbnail);

                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity() , BookInfoOrder.class);

                        i.putExtra("book_id" ,final_query);
                        i.putExtra("book_author" ,model.getAuthor());
                        i.putExtra("book_title",model.getTitle());
                        i.putExtra("book_thumbnail",model.getThumbnail());
                        i.putExtra("book_desc",model.getDescription());
                        i.putExtra("book_cat",model.getCategory());

                        i.putExtra("link",model.getPreview());
                        i.putExtra("sellerbookid", model.getSellerbookid());
                        i.putExtra("seller",model.getSellerid());
                        i.putExtra("rp",model.getRentingprice());
                        i.putExtra("sp",model.getSellingprice());
                        i.putExtra("dc",model.getDeliverycharges());
                        i.putExtra("qu",model.getQuantities());

                        startActivity(i);
                    }
                });

            }
        };
    }

    private static class SellingBooksViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView row_thumbnail;
        LinearLayout container;
        TextView row_title,row_author,row_price,row_quantity;

        SellingBooksViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            container=mView.findViewById(R.id.container);
            row_thumbnail= mView.findViewById(R.id.row_thumbnail);
            row_title=mView.findViewById(R.id.row_title);
            row_author=mView.findViewById(R.id.row_author);
            row_price=mView.findViewById(R.id.row_price);
            row_quantity=mView.findViewById(R.id.row_quantity);

        }
    }
    @Override
    public void onStart(){
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop(){
        super.onStop();
        adapter.stopListening();
    }
    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onPause(){
        super.onPause();
        adapter.stopListening();
    }

}
