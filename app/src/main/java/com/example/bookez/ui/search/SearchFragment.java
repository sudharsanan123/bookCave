package com.example.bookez.ui.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.bookez.BookInfoOrder;
import com.example.bookez.R;
import com.example.bookez.extras.Book;
import com.example.bookez.extras.SBRecyclerViewAdapter;
import com.example.bookez.extras.SellingBook;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private EditText search_main;
    private TextView No_result;
    private ArrayList<Book> mBooks;
    private RequestQueue mRequestQueue;
    private RecyclerView recyclerview_main;
    private SBRecyclerViewAdapter mAdapter;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private ToggleButton toggleButton;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static  final  String BASE_URL="https://www.googleapis.com/books/v1/volumes?q=";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        //fs
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        toggleButton=root.findViewById(R.id.toggleButton);
        search_main=root.findViewById(R.id.search_main);
        ImageButton search_main_btn = root.findViewById(R.id.search_main_btn);
        No_result=root.findViewById(R.id.No_result);
        recyclerview_main=root.findViewById(R.id.recyclerview_main);
        recyclerview_main.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview_main.setLayoutManager(linearLayoutManager);

        //
        int status;
        mBooks = new ArrayList<>();

        mBooks = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity());

        search_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                No_result.setVisibility(View.INVISIBLE);
                if(toggleButton.isChecked())
                {
                    Toast.makeText(getActivity(), "Searching through web", Toast.LENGTH_SHORT).show();
                    mBooks.clear();
                    search();
                }
                else {
                    mBooks.clear();
                    Toast.makeText(getActivity(), "Searching through our shops", Toast.LENGTH_SHORT).show();
                    if(search_main.getText().toString().equals(""))
                    {
                        Toast.makeText(getActivity(),"Please enter something to search",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(),"Searching -"+search_main.getText().toString(),Toast.LENGTH_SHORT).show();
                        searchFirestore(search_main.getText().toString());
                    }
                }
            }
        });
        return root;
    }

    private void searchFirestore(String input_given) {
        //Insert the code for searching a book in firebase
        Query query = firebaseFirestore.collection("SellingList").whereEqualTo("title", input_given);
        FirestoreRecyclerOptions<SellingBook> options = new FirestoreRecyclerOptions.Builder<SellingBook>()
                .setQuery(query, SellingBook.class)
                .build();

        //get id and query to set book name and author
        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<SellingBook, BookokayViewHolder>(options) {
            @Override
            public BookokayViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_book_search_result, parent, false);

                return new BookokayViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NotNull BookokayViewHolder viewHolder, int position, final SellingBook model) {
                //get id and query to set book name and author
                int sp = model.getSellingprice();
                final String final_query = model.getBookid();
                viewHolder.title.setText(model.getTitle());
                viewHolder.category.setText(model.getCategory());
                viewHolder.author.setText(model.getAuthor());
                viewHolder.author.setText(String.valueOf(sp));
                Glide.with(requireActivity()).load(model.getThumbnail()).placeholder(R.drawable.loading_shape).dontAnimate().into(viewHolder.thumbnail);

                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), BookInfoOrder.class);

                        i.putExtra("book_id", final_query);
                        i.putExtra("book_author", model.getAuthor());
                        i.putExtra("book_title", model.getTitle());
                        i.putExtra("book_thumbnail", model.getThumbnail());
                        i.putExtra("book_desc", model.getDescription());
                        i.putExtra("book_cat", model.getCategory());

                        i.putExtra("link", model.getPreview());
                        i.putExtra("sellerbookid", model.getSellerbookid());
                        i.putExtra("seller", model.getSellerid());
                        i.putExtra("rp", model.getRentingprice());
                        i.putExtra("sp", model.getSellingprice());
                        i.putExtra("dc", model.getDeliverycharges());
                        i.putExtra("qu", model.getQuantities());

                        startActivity(i);
                    }
                });
            }
        };
        adapter.startListening();
        recyclerview_main.setAdapter(adapter);
    }

    private void parseJson(String key) {

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, key, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String title ="";
                        String author ="";
                        String isbn= "empty";
                        String publishedDate = "NoT Available";
                        String description = "No Description";
                        int pageCount = 1000;
                        String categories = "Non categorized ";
                        String buy ="";

                        String price = "not given INR";
                        try {
                            JSONArray items = response.getJSONArray("items");

                            for (int i = 0 ; i< items.length() ;i++){
                                JSONObject item = items.getJSONObject(i);
                                JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                                try{
                                    title = volumeInfo.getString("title");

                                    JSONArray authors = volumeInfo.getJSONArray("authors");
                                    if(authors.length() == 1){
                                        author = authors.getString(0);
                                    }else {
                                        author = authors.getString(0) + "|" +authors.getString(1);
                                    }

                                    publishedDate = volumeInfo.getString("publishedDate");
                                    pageCount = volumeInfo.getInt("pageCount");
                                    JSONObject saleInfo = item.getJSONObject("saleInfo");
                                    JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                                    price = listPrice.getString("amount") + " " +listPrice.getString("currencyCode");
                                    description = volumeInfo.getString("description");
                                    buy = saleInfo.getString("buyLink");
                                    isbn = item.getString("id");
                                    categories = volumeInfo.getJSONArray("categories").getString(0);

                                }catch (Exception e){
                                    Log.d("TAG", e.toString());
                                }



                                String thumbnail = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                                String previewLink = volumeInfo.getString("previewLink");
                                String url = volumeInfo.getString("infoLink");

                                mBooks.add(new Book(title , author , publishedDate , description ,categories
                                        ,thumbnail,buy,previewLink,price,pageCount,url,isbn));

                                mAdapter = new SBRecyclerViewAdapter(getActivity() , mBooks);
                                recyclerview_main.setAdapter(mAdapter);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG" , e.toString());

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }

    private void search() {
            String search_query = search_main.getText().toString();
            if(search_query.equals(""))
            {
                Toast.makeText(getActivity(),"Please enter something to search",Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getActivity()," fetching results",Toast.LENGTH_SHORT).show();
            String final_query=search_query.replace(" ","+");
            Uri uri=Uri.parse(BASE_URL+final_query);
            Uri.Builder buider = uri.buildUpon();
            parseJson(buider.toString());

    }

    public static class BookokayViewHolder extends RecyclerView.ViewHolder {
        View mView;
        LinearLayout container; //row_filter_s_list
        TextView title,category,price,author;
        ImageView thumbnail;

        BookokayViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            container=mView.findViewById(R.id.container);

            title=mView.findViewById(R.id.title);
            category=mView.findViewById(R.id.category);
            price=mView.findViewById(R.id.price);
            thumbnail=mView.findViewById(R.id.thumbnail);
            author=mView.findViewById(R.id.author);
        }
    }


}
