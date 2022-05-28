package com.example.collegeapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URI;

public class UploadNotice extends AppCompatActivity {

    private CardView addnotice;
    private ImageView mImageView;
//
//    private final int REQ = 1;
//
//    private Bitmap bitmap;

    ActivityResultLauncher<String> mGetContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);
        addnotice = findViewById(R.id.upload_notice);
        mImageView = findViewById(R.id.noticeImageView);
    mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            mImageView.setImageURI(result);
        }
    });

        addnotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
    }

//    private void opengallery() {
//        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(pickImage,REQ);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQ && resultCode == RESULT_OK)
//        {
//            Uri uri = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getContentUri(getContentResolver(),uri);
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//        }
//    }
}