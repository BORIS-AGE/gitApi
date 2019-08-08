package com.example.boris.githubreps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.boris.githubreps.api.Client;
import com.example.boris.githubreps.api.Service;
import com.example.boris.githubreps.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        loadJSON(currentOffset);

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
                    loadJSON(currentOffset);
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
                            setRecycler();
                        else
                            updateRecycler();

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

    private void setRecycler(){
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new MyRecyclerAdapter(recyclerItems, getApplicationContext());
        recyclerView.setAdapter(adapter);
        isSetRecycler = true;
    }

    private void updateRecycler(){
        adapter.notifyItemRangeChanged(currentOffset, currentOffset + 100);
    }
}
