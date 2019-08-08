package com.example.boris.githubreps.controller;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.boris.githubreps.model.Item;

import java.util.List;

public class SqlBrains extends SQLiteOpenHelper {

    private static SqlBrains sqlBrains;

    public static SqlBrains getSqlBrains(Context context){
        if (sqlBrains == null)
            sqlBrains = new SqlBrains(context);
        return sqlBrains;
    }

    private SqlBrains(Context context){
        super(context, "NAME", null, 1);
        Log.d("database operations", "database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table items (id number, name text, link text, image text)");
        Log.d("database operations", "table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists items");
        onCreate(db);
        Log.d("database operations", "upgrade created");

    }

    public void addContact(String name, String link, String image_url){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("link", link);
        contentValues.put("image", image_url);

        db.insert("items", null, contentValues);
        db.close();
    }

    public void getContacts(List<Item> items, int offset){
        SQLiteDatabase db = getReadableDatabase();

        String[] required = {"id", "name", "link", "image"};
        Cursor cursor = db.query("items",required,null,null,null,null,"id", offset + ",30");
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String link = cursor.getString(cursor.getColumnIndex("link"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            items.add(new Item(name, link, new Item.Owner(image)));
        }
        db.close();
    }

}
