package com.chatho.chatho.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatho.chatho.R;
import com.chatho.chatho.pojo.Stories;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Owen_adpt extends RecyclerView.Adapter<Owen_adpt.ViewHolder> {
    private Context context;
    private List<Stories> list;
    private String CurrentUID;

    public Owen_adpt(Context context, List<Stories> list,String CurrentUID) {
        this.context = context;
        this.list = list;
        CurrentUID=CurrentUID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.owen_single_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stories st=list.get(position);

        Picasso.get().load(st.getImageURI()).resize(90,90).centerInside().into(holder.img_st);

        holder.deleteimg.setOnClickListener(view -> {
            deleting(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_st;
        ImageView deleteimg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_st=itemView.findViewById(R.id.img_st);
            deleteimg=itemView.findViewById(R.id.deleteimg);


        }
    }

    public void deleting(int i){
        DatabaseReference stories_ref;
        stories_ref= FirebaseDatabase.getInstance().getReference().child("Stories");
        stories_ref.child(CurrentUID).child("U"+i).removeValue();
    }
}
