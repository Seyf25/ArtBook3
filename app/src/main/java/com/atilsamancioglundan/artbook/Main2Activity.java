package com.atilsamancioglundan.artbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    ImageView imgResim;
    EditText txtName;
    ImageButton btnSave;
    static SQLiteDatabase database;
    Bitmap secilenResim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imgResim = findViewById(R.id.imgResim);
        txtName = findViewById(R.id.txtName);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if(info.equalsIgnoreCase("new")){
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.selectphoto);
            imgResim.setImageBitmap(bitmap);
            btnSave.setVisibility(View.VISIBLE);
            txtName.setText("");

        }else{
            btnSave.setVisibility(View.INVISIBLE);
        }
    }


    public void fotocek (View view){

        //System.out.println("Test12345");
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

        }else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if(requestCode == 2){
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
            }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode ==RESULT_OK && data != null){
            Uri secilmisresim = data.getData();
            try {
                secilenResim =MediaStore.Images.Media.getBitmap(this.getContentResolver(), secilmisresim);
                imgResim.setImageBitmap(secilenResim);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void kaydet (View view){
        String artName = txtName.getText().toString();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        secilenResim.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {
            System.out.println("kaydedildi");
            database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO arts (name, image) VALUES (?, ?)";
            SQLiteStatement statement = database.compileStatement(sqlString);
            statement.bindString(1, artName);
            statement.bindBlob(2, byteArray);
            statement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);


    }
}
