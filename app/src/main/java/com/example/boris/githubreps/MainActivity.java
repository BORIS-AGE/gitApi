package com.example.boris.githubreps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.boris.githubreps.api.Client;
import com.example.boris.githubreps.api.Service;
import com.example.boris.githubreps.controller.SqlBrains;
import com.example.boris.githubreps.model.Item;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private List<Item> recyclerItems = new ArrayList<>();
    private MyRecyclerAdapter adapter;
    private boolean isScrolling = false, isSetRecycler = false;
    private int currentItems, scrollOutItems;
    private LinearLayoutManager manager;
    private int currentOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDefaults();
    }

    private void setDefaults() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting repositories");
        progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.recycler);

        loadReps();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                int totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    currentOffset += 100;
                    loadReps();
                }
            }
        });
    }

    private void loadJSON(int offset) {
        progressDialog.show();
        try{
            Service apiService = Client.getClient().create(Service.class);
            Call<List<Item>> call = apiService.getItems(offset + "");

            call.enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    if (!response.isSuccessful()) {
                        makeErrorNotification("code: " + response.code());
                        return;
                    }
                    if (response.body() != null){
                        recyclerItems.addAll(response.body());
                        if (!isSetRecycler)
                            setRecycler(100);
                        else
                        //add to db
                        for (Item item : response.body()){
                            SqlBrains.getSqlBrains(getApplicationContext()).addContact(item.getName(),item.getHtml_url(),item.getOwner().avatar_url);
                        }

                        progressDialog.dismiss();
                    }
                    else
                        makeErrorNotification("response.body() == null");
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    makeErrorNotification("Error: " + t.toString());
                }
            });
        }catch (Exception e){
            makeErrorNotification(e.toString());
        }

    }

    public void makeErrorNotification(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
        System.out.println(str);
    }

    private void setRecycler(int offset){
        progressDialog.show();
        if (!isSetRecycler){
            manager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(manager);
            adapter = new MyRecyclerAdapter(recyclerItems, getApplicationContext());
            recyclerView.setAdapter(adapter);
            isSetRecycler = true;
        }else{
            adapter.notifyItemRangeChanged(currentOffset, currentOffset + offset);
        }
        progressDialog.dismiss();
    }

    private void loadReps(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int size = recyclerItems.size();
                SqlBrains.getSqlBrains(getApplicationContext()).getContacts(recyclerItems, currentOffset);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (size != recyclerItems.size()){
                            currentOffset += recyclerItems.size() - size;
                            setRecycler(30);
                        }
                        else{
                            loadJSON(currentOffset);
                            currentOffset += 100;
                        }
                    }
                });

            }
        }).start();


    }
}
