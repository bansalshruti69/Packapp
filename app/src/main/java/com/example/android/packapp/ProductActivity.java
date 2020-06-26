package com.example.android.packapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.android.packapp.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRICE;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRODUCT_IMAGE;
import static com.example.android.packapp.data.ProductContract.ProductEntry.PRODUCT_NAME;
import static com.example.android.packapp.data.ProductContract.ProductEntry.QUANTITY;
import static com.example.android.packapp.data.ProductContract.ProductEntry.SUPPLIER;

public class ProductActivity extends AppCompatActivity {

    private Uri selectedImage = null;
    private static int RESULT_LOAD_IMAGE = 1;

    private EditText mProduct_Name;
    private EditText mPrice;
    private EditText mSupplier;
    private EditText mQuantity;
    private TextView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Button button = (Button) findViewById(R.id.upload_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        mProduct_Name = (EditText) findViewById(R.id.product_name);
        mPrice = (EditText) findViewById(R.id.price);
        mSupplier = (EditText) findViewById(R.id.supplier);
        mQuantity = (EditText) findViewById(R.id.quantity);
        mImage = (TextView) findViewById(R.id.image_text_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            File f = new File(picturePath);
            String imageName = f.getName();
            TextView textView = (TextView) findViewById(R.id.image_text_view);
            textView.setText(imageName);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                try {
                    saveProduct();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() throws IOException {
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME,mProduct_Name.getText().toString().trim());
        values.put(SUPPLIER,mSupplier.getText().toString().trim());
        values.put(QUANTITY,Integer.parseInt(mQuantity.getText().toString().trim()));
        values.put(PRICE,Integer.parseInt(mPrice.getText().toString().trim()));
        InputStream iStream = getContentResolver().openInputStream(selectedImage);
        byte[] inputData = getBytes(iStream);
        values.put(PRODUCT_IMAGE,inputData);
        //Image remaining
        Uri uri = getContentResolver().insert(CONTENT_URI,values);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
