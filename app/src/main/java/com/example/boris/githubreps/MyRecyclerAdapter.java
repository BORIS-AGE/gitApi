package com.example.boris.githubreps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boris.githubreps.model.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {
    List<Item> items;
    Context context;

    public MyRecyclerAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rep, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Picasso.with(context).load(items.get(position).getOwner().getAvatar_url())
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

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recyclerImage);
            title = itemView.findViewById(R.id.recyclerTitle);
            link = itemView.findViewById(R.id.recyclerLink);
        }
    }
}
