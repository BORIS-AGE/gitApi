package com.example.boris.githubreps.api;

import com.example.boris.githubreps.model.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Service {
    @GET("repositories")
    Call<List<Item>> getItems();
}
