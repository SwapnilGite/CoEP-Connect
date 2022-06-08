package com.example.collegeapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.UUID;

public class uploadImageActivity extends AppCompatActivity {
    private Spinner imageCategory;
    private CardView selectImage;
    private Button uploadImgBtn;
    private ImageView galleryImageView;
    private String category;
    Uri imageUri;
    String downloadURl;
    FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference reference;
    ProgressDialog pd;
    ActivityResultLauncher<String> mGetContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        imageCategory = findViewById(R.id.image_category);
        selectImage = findViewById(R.id.addGalleryImage);
        uploadImgBtn = findViewById(R.id.uploadImageBtn);
        galleryImageView = findViewById(R.id.galleryImageView);
        pd = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference().child("Gallery");
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] items = new String[]{"Select category","Function Image","Fests","Seminar"};
        imageCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));
        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(uploadImageActivity.this,"Select image category",Toast.LENGTH_SHORT).show();
            }
        });
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result != null){
                    galleryImageView.setImageURI(result);}
                imageUri = result;
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() { //adding image
            @Override
            public void onClick(View view) {

                mGetContent.launch("image/*");
            }
        });
        //function to upload image
        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri == null)
                {
                    Toast.makeText(uploadImageActivity.this,"Please select image",Toast.LENGTH_SHORT).show();
                }
                else if (category.equals("Select category"))
                {
                    Toast.makeText(uploadImageActivity.this,"Please select category",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pd.setMessage("Uploading");
                    pd.show();
                    uploadImg();
                }
            }
        });
    }

    private void uploadImg() {
        final StorageReference filepath;
        filepath = storageReference.child("Gallery/"+ UUID.randomUUID().toString());
        final UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnCompleteListener(uploadImageActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                    pd.dismiss();
                    Toast.makeText(uploadImageActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData() {
        reference = reference.child(category);
        final String uniqueKey = reference.push().getKey(); //key for the data
        reference.child(uniqueKey).setValue(downloadURl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(uploadImageActivity.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(uploadImageActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(uploadImageActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }


}