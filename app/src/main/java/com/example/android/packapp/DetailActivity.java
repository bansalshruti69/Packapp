package com.example.android.packapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.packapp.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRICE;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRODUCT_IMAGE;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRODUCT_NAME;
import static com.example.android.packapp.data.ProductContract.ProductEntry.QUANTITY;
import static com.example.android.packapp.data.ProductContract.ProductEntry.SUPPLIER;
import static com.example.android.packapp.data.ProductContract.ProductEntry._ID;

public class DetailActivity extends AppCompatActivity  implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private Uri Curruri;

    private CircleImageView imageView;
    private TextView product_name;
    private TextView price;
    private TextView supplier;
    private TextView quantity;
    private Button order;
    private Button add;
    private Button sub;

    private int quan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        Curruri = intent.getData();

        imageView = (CircleImageView) findViewById(R.id.product_image);
        product_name = (TextView) findViewById(R.id.product_name);
        price = (TextView) findViewById(R.id.price);
        supplier = (TextView) findViewById(R.id.supplier);
        quantity = (TextView) findViewById(R.id.quantity);
        order = (Button) findViewById(R.id.order);
        add = (Button) findViewById(R.id.add_quantity);
        sub = (Button) findViewById(R.id.subtract_quantity);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addone();
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subone();
            }
        });
        getLoaderManager().initLoader(0,null,this);
    }
    private void addone(){
        quan = quan + 1;
        ContentValues values = new ContentValues();
        values.put(QUANTITY,quan);
        getContentResolver().update(Curruri,values,null,null);
        quantity.setText(String.valueOf(quan));
    }

    private void subone(){
        if(quan>0){
            quan = quan - 1;
            ContentValues values = new ContentValues();
            values.put(QUANTITY,quan);
            getContentResolver().update(Curruri,values,null,null);
            quantity.setText(String.valueOf(quan));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.action_delete:
                // Do nothing for now
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                // Navigate back to parent activity (CatalogActivity)
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID,
                PRODUCT_NAME,
                PRICE,
                QUANTITY,
                SUPPLIER,
                PRODUCT_IMAGE
        };
        return new CursorLoader(this,Curruri,projection,null,null,null);

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (data == null || (data != null && data.getCount() == 0))
            return;
        data.moveToFirst();
        //ImageView Remaining
        product_name.setText(data.getString(data.getColumnIndexOrThrow(PRODUCT_NAME)));
        price.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(PRICE))) + " Rs.");
        supplier.setText(data.getString(data.getColumnIndexOrThrow(SUPPLIER)));
        quan = data.getInt(data.getColumnIndexOrThrow(QUANTITY));
        quantity.setText(String.valueOf(quan));
        final String mail_address = data.getString(data.getColumnIndexOrThrow(SUPPLIER));
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] addresses = {mail_address};
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_more));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        if(data.isNull(data.getColumnIndexOrThrow(PRODUCT_IMAGE)))
            return;
        byte[] blob = data.getBlob(data.getColumnIndex(PRODUCT_IMAGE));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        // TODO: Implement this method
        if(Curruri!=null) {
            int r = getContentResolver().delete(Curruri, null, null);
            Toast.makeText(this,R.string.product_deleted,Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
