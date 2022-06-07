package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class deleteNotice extends AppCompatActivity {
    private ProgressBar pd;
    private RecyclerView recyclerView;
    private ArrayList<NoticeData> list;
    private NoticeAdapter adapter;

    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);
        reference = FirebaseDatabase.getInstance().getReference().child("Notice");
        pd = findViewById(R.id.ProgressBar);
        recyclerView = findViewById(R.id.noticeDeleteRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        getNotice();


    }

    private void getNotice() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    NoticeData data = snapshot1.getValue(NoticeData.class);
                     list.add(data);
                }
                adapter = new NoticeAdapter(deleteNotice.this,list);
                adapter.notifyDataSetChanged();
                pd.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.setVisibility(View.GONE);
                Toast.makeText(deleteNotice.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }


}