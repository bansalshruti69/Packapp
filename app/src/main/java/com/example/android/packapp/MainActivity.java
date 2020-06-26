package com.example.android.packapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import static com.example.android.packapp.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRICE;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRODUCT_IMAGE;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRODUCT_NAME;
import static com.example.android.packapp.data.ProductContract.ProductEntry.QUANTITY;
import static com.example.android.packapp.data.ProductContract.ProductEntry.SUPPLIER;
import static com.example.android.packapp.data.ProductContract.ProductEntry._ID;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private ProductCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });
        ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        adapter = new ProductCursorAdapter(this,null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("Hey","I'm here");
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                Uri uri = ContentUris.withAppendedId(CONTENT_URI,l);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0,null,this);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID,
                PRODUCT_NAME,
                PRICE,
                QUANTITY,
                SUPPLIER,
                PRODUCT_IMAGE
        };
        return new CursorLoader(this,CONTENT_URI,projection,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void decrement(View view){
        int position=(Integer) view.getTag();
        String[] projection = {
                _ID,
                PRODUCT_NAME,
                PRICE,
                QUANTITY,
                SUPPLIER,
                PRODUCT_IMAGE
        };
        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(CONTENT_URI,position),projection,null,null,null);
        cursor.moveToFirst();
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY));
        if(quantity<=0)
            return;
        ContentValues values = new ContentValues();
        values.put(QUANTITY,quantity-1);
        int rowupdated = getContentResolver().update(ContentUris.withAppendedId(CONTENT_URI,position),values,null,null);
    }

}
