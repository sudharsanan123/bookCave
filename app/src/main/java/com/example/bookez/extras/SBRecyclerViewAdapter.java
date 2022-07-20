package com.example.bookez.extras;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookez.BookInfo;
import com.example.bookez.R;

import java.util.List;

public class SBRecyclerViewAdapter extends RecyclerView.Adapter<SBRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Book> mData;

    public SBRecyclerViewAdapter(Context mContext, List<Book> mData)
    {
        this.mContext = mContext;
        this.mData = mData;
        RequestOptions options = new RequestOptions().centerCrop().error(R.drawable.loading_shape);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.row_book_search_main, parent , false);
        final MyViewHolder viewHolder =  new MyViewHolder(view);
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext , BookInfo.class);
                int pos = viewHolder.getAdapterPosition();
                i.putExtra("book_title" ,mData.get(pos).getTitle());
                i.putExtra("book_author" ,mData.get(pos).getAuthors());
                i.putExtra("book_desc" ,mData.get(pos).getDescription());
                i.putExtra("book_categories" ,mData.get(pos).getCategories());
                i.putExtra("book_publish_date" ,mData.get(pos).getPublishedDate());
                i.putExtra("book_info" ,mData.get(pos).getmUrl());
                i.putExtra("book_buy" ,mData.get(pos).getBuy());
                i.putExtra("book_preview" ,mData.get(pos).getPreview());
                i.putExtra("book_thumbnail" ,mData.get(pos).getThumbnail());
                i.putExtra("book_isbn" ,mData.get(pos).getmIsbn());
                i.putExtra("book_price",mData.get(pos).getPrice());

                mContext.startActivity(i);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Book book = mData.get(i);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthors());
        holder.tvPrice.setText(book.getPrice());
        holder.tvCategory.setText(book.getCategories());
        //load image from internet and set it into imageView using Glide
        Glide.with(mContext).load(book.getThumbnail()).placeholder(R.drawable.loading_shape).dontAnimate().into(holder.tvThumbnail);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView tvThumbnail ;
        TextView tvTitle , tvCategory , tvPrice , tvAuthor;
        LinearLayout container ;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvThumbnail = itemView.findViewById(R.id.thumbnail);
            tvTitle = itemView.findViewById(R.id.title);
            tvAuthor = itemView.findViewById(R.id.author);
            tvCategory = itemView.findViewById(R.id.category);
            tvPrice = itemView.findViewById(R.id.price);
            container = itemView.findViewById(R.id.container);


        }
    }
}
