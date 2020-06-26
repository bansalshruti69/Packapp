package com.example.android.packapp;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        adapter = new ProductCursorAdapter(this,null);
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(0,null,this);
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
}
