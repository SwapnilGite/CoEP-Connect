package com.example.collegeapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class UploadEbook extends AppCompatActivity {
    private CardView addpdf;
   // private ImageView mImageView;
    private TextView pdfTextView;
    private String pdfName;
    private EditText pdfTitle;
    private Button uploadPDFBtn;
    FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference reference;
    String downloadURl = "";
    Uri pdfUri;
    private ProgressDialog pD;

    ActivityResultLauncher<String> mGetContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_upload_ebook);
        addpdf = findViewById(R.id.upload_pdf);
       // mImageView = findViewById(R.id.noticeImageView);
        pdfTitle = findViewById(R.id.pdf_title);
        uploadPDFBtn = findViewById(R.id.uploadPDFBtn);
        storage = FirebaseStorage.getInstance();
        pdfTextView = findViewById(R.id.pdfTextView);
        pD = new ProgressDialog(this);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            //@RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("Range")
            @Override
            public void onActivityResult(Uri result) {
                if(result != null){
                    //mImageView.setImageURI(result);
                   if(result.toString().startsWith("content://")){
                        Cursor cursor;
                        cursor = UploadEbook.this.getContentResolver().query(result,null,null,null,null);
                        if(cursor!= null && cursor.moveToFirst())
                        {
                            pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            pdfTextView.setText(pdfName);
                        }

                    }else if(result.toString().startsWith("file://"))
                   {
                       pdfName = new File(result.toString()).getName();
                       pdfTextView.setText(pdfName);
                   }
                }
                pdfUri = result;
            }
        });

        addpdf.setOnClickListener(new View.OnClickListener() { //adding PDF
            @Override
            public void onClick(View view) {
                mGetContent.launch("application/pdf/*");
            }
        });
        //function to upload image
        uploadPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pdfTitle.toString().isEmpty()) //if notice title is empty
                {
                    pdfTitle.setError("Empty"); //show empty error
                    pdfTitle.requestFocus();
                }
                else //upload image
                {
                    //upload PDF on button click
                    uploadPDF();
                }
            }
        });
    }

    private void uploadTitle(String downloadURl) //function to upload PDF data
    {
        pD.setMessage("Uploading..");
        pD.show();
        //reference = reference.child("PDF");
        String uniqueKey = reference.child("pdf").push().getKey(); //key for the data
        HashMap data = new HashMap();
        data.put("pdfTitle",pdfName);
        data.put("pdfURL",downloadURl);


        //String title = pdfTitle.getText().toString(); //holds notice title

        //Calendar calForDate = Calendar.getInstance();
        //SimpleDateFormat currDate = new SimpleDateFormat("dd-MM-yy");
        //String date = currDate.format(calForDate.getTime()); //For data

        //Calendar calForTime = Calendar.getInstance();
        //SimpleDateFormat currTime = new SimpleDateFormat("hh:mm a");
        //String time = currTime.format(calForTime.getTime());

        //NoticeData noticeData = new NoticeData(title,downloadURl,date,time,uniqueKey); //adding data
        reference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UploadEbook.this,"PDF Uploaded Successfully",Toast.LENGTH_SHORT).show();
                pD.dismiss();
                Intent i;
                i = new Intent(UploadEbook.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadEbook.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                pD.dismiss();
            }
        });


    }

    private void uploadPDF() {
        if(pdfUri != null)
        {
            final StorageReference filepath;
            filepath = storageReference.child("pdf/"+ pdfName+"-"+System.currentTimeMillis()+".pdf");
            final UploadTask uploadTask = filepath.putFile(pdfUri);
            uploadTask.addOnCompleteListener(UploadEbook.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                        uploadTitle(downloadURl);
                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        pD.dismiss();
                        Toast.makeText(UploadEbook.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}