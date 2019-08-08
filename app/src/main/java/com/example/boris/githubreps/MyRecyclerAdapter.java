package com.example.boris.githubreps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boris.githubreps.model.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {
    List<Item> items;
    MainActivity mainActivity;

    public MyRecyclerAdapter(List<Item> items, MainActivity mainActivity) {
        this.items = items;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rep, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Picasso.with(mainActivity).load(items.get(position).getOwner().getAvatar_url())
                .placeholder(R.drawable.load)
                .into(holder.imageView);

        holder.link.setText(items.get(position).getHtml_url());
        holder.title.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title, link;
        CardView cardView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recyclerImage);
            title = itemView.findViewById(R.id.recyclerTitle);
            link = itemView.findViewById(R.id.recyclerLink);
            cardView = itemView.findViewById(R.id.recyclerMain);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(getAdapterPosition()).getHtml_url()));
                    mainActivity.startActivity(browserIntent);
                }
            });
        }
    }
}
