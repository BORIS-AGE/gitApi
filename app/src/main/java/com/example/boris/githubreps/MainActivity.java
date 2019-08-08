package com.example.boris.githubreps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.boris.githubreps.api.Client;
import com.example.boris.githubreps.api.Service;
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
        progressDialog.show();

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        loadJSON();
    }

    private void loadJSON() {
        try{
            Service apiService = Client.getClient().create(Service.class);
            Call<List<Item>> call = apiService.getItems();

            call.enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    if (!response.isSuccessful()) {
                        makeErrorNotification("code: " + response.code());
                        return;
                    }
                    if (response.body() != null){
                        recyclerItems.addAll(response.body());
                        adapter = new MyRecyclerAdapter(recyclerItems, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        progressDialog.hide();
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
}
