package com.example.collegeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.connector.zza;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {
    private Context context;
    private ArrayList<NoticeData> list;

    public NoticeAdapter(Context context, ArrayList<NoticeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_itemlayout,parent,false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, @SuppressLint("RecyclerView") int position) {

       // position = holder.getAdapterPosition();
        NoticeData currentItem = list.get(position);
        holder.deleteNoticeTitle.setText(currentItem.getTitle());
        try {
            if(currentItem.getImage() != null){
            Picasso.get().load(currentItem.getImage()).into(holder.deleteNoticeImg);}
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.deleteNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notice");
                StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentItem.image);
                imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context,"Notice Deleted",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();

                    }
                });
                reference.child(currentItem.getKey()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context,"Notice Deleted",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class NoticeViewAdapter extends RecyclerView.ViewHolder {

        private Button deleteNotice;
        private TextView deleteNoticeTitle;
        private ImageView deleteNoticeImg;
        public NoticeViewAdapter(@NonNull View itemView) {

            super(itemView);
            deleteNotice = itemView.findViewById(R.id.deleteNoticeBtn);
            deleteNoticeTitle = itemView.findViewById(R.id.deleteNoticeTitle);
            deleteNoticeImg = itemView.findViewById(R.id.deleteNoticeImg);
        }
    }
}

