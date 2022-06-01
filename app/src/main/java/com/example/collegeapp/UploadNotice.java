package com.example.collegeapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class UploadNotice extends AppCompatActivity {

    private CardView addnotice;
    private ImageView mImageView;
    private EditText noticeTitle;
    private Button uploadNoticeBtn;
    FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference reference;
    String downloadURl = "";
    Uri imageUri;
    private ProgressDialog pD;

    ActivityResultLauncher<String> mGetContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_upload_notice);
        addnotice = findViewById(R.id.upload_notice);
        mImageView = findViewById(R.id.noticeImageView);
        noticeTitle = findViewById(R.id.notice_title);
        uploadNoticeBtn = findViewById(R.id.uploadNoticeBtn);
        storage = FirebaseStorage.getInstance();
        pD = new ProgressDialog(this);

    mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
           if(result != null){
            mImageView.setImageURI(result);}
            imageUri = result;
        }
    });

        addnotice.setOnClickListener(new View.OnClickListener() { //adding image
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
        //function to upload image
        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noticeTitle.toString().isEmpty()) //if notice title is empty
                {
                   noticeTitle.setError("Empty"); //show empty error
                   noticeTitle.requestFocus();
                }
                else if(imageUri == null) //if no image is added, then just upload data
                {
                    uploadData();
                }
                else //upload image
                {
                    //upload image on button click
                    uploadImage();
                }
            }
        });
}

    private void uploadData() //function to upload notice data
    {
        pD.setMessage("Uploading..");
        pD.show();
        reference = reference.child("Notice");
        final String uniqueKey = reference.push().getKey(); //key for the data
        String title = noticeTitle.getText().toString(); //holds notice title

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd-MM-yy");
        String date = currDate.format(calForDate.getTime()); //For data

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currTime = new SimpleDateFormat("hh:mm a");
        String time = currTime.format(calForTime.getTime());

        NoticeData noticeData = new NoticeData(title,downloadURl,date,time,uniqueKey); //adding data
        reference.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pD.dismiss();
                Toast.makeText(UploadNotice.this,"Notice Uploaded",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pD.dismiss();
                Toast.makeText(UploadNotice.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void uploadImage() {
        if(imageUri != null)
        {
            final StorageReference filepath;
            filepath = storageReference.child("Notice/"+ UUID.randomUUID().toString());
            final UploadTask uploadTask = filepath.putFile(imageUri);
            uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadURl = String.valueOf(uri);
                                        uploadData();
                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        pD.dismiss();
                        Toast.makeText(UploadNotice.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    }