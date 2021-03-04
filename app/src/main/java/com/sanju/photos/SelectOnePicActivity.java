package com.sanju.photos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class SelectOnePicActivity extends AppCompatActivity {

    public static final int PERMISSION_CODE = 1000;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;
    Uri imageUri;
    Button cameraBtn;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_one_pic);

        cameraBtn = (Button)findViewById(R.id.cameraBtn);
        photo = (ImageView) findViewById(R.id.photo);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(SelectOnePicActivity.this);
                builder.setTitle("Choose Photo...");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                if(checkSelfPermission(Manifest.permission.CAMERA) ==
                                        PackageManager.PERMISSION_DENIED ||
                                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                        PackageManager.PERMISSION_DENIED){
                                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                    requestPermissions(permission, PERMISSION_CODE);
                                }
                                else {
                                    TakePicture();
                                }
                            }
                            else {
                                TakePicture();
                            }
                        } else if (options[item].equals("Choose from Gallery")) {
                            if (ActivityCompat.checkSelfPermission(SelectOnePicActivity.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(SelectOnePicActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        100);
                                return;
                            }
                            Intent gallery = new Intent();
                            gallery.setType("image/*");
                            gallery.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    TakePicture();
                }
                else {
                    Toast.makeText(SelectOnePicActivity.this,"Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(imageBitmap);
        }

        //gallery
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SelectOnePicActivity.this.getApplicationContext().getContentResolver(), imageUri);
                photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}