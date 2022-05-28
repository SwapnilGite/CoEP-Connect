package com.example.collegeapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class UploadNotice extends AppCompatActivity {

    private CardView addnotice;
    private ImageView mImageView;
    private EditText noticeTitle;
    private Button uploadNoticeBtn;
    FirebaseStorage storage;
    Uri imageUri;
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
        noticeTitle = findViewById(R.id.notice_title);
        uploadNoticeBtn = findViewById(R.id.uploadNoticeBtn);
        storage = FirebaseStorage.getInstance();

    mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
           if(result != null){
            mImageView.setImageURI(result);}
            imageUri = result;
        }
    });

        addnotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload image on button click
                uploadImage();
            }
        });
//        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(noticeTitle.getText().toString().isEmpty())
//                {
//                    noticeTitle.setError("Empty");
//                    noticeTitle.requestFocus();
//                }else if(mGetContent == NULL)
//            }
//        });
//    }

}

    private void uploadImage() {
        if(imageUri != null)
        {
            StorageReference reference = storage.getReference().child("images/"+ UUID.randomUUID().toString());
            //reference to store image in firebase storage
            //it will be stored inside images folder in firebase

            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                       Toast.makeText(UploadNotice.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(UploadNotice.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
    }