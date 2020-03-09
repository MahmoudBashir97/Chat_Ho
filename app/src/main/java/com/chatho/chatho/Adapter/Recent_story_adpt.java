package com.chatho.chatho.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatho.chatho.R;
import com.chatho.chatho.pojo.Stories;
import com.chatho.chatho.story_package.Owen_story;
import com.chatho.chatho.story_package.Story_Activity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Recent_story_adpt extends RecyclerView.Adapter<Recent_story_adpt.ViewHolder> {

    private Context context;
    private List<Stories> list;
    String arr[];
    String name;

    public Recent_story_adpt(Context context, List<Stories> list,String name) {
        this.context = context;
        this.list = list;
        this.name=name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_story,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stories st=list.get(position);

        /*arr=new String[list.size()];
        arr[position]=st.getImageURI();
        for (int i=0;i<arr.length;i++){
            Picasso.get().load(arr[i]).resize(100,80).centerInside().into(holder.other_pic);
        }*/
        holder.edit_st.setOnClickListener(view -> {
            Intent i =new Intent(context.getApplicationContext(), Owen_story.class);
            context.startActivity(i);
        });
        if (name !=null){
        holder.other_name.setText(name);}
        Picasso.get().load(R.drawable.i_edit).into(holder.edit_st);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView other_pic;
        TextView other_name,Time;
        RelativeLayout Rel_status;
        ImageView edit_st;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            other_pic=itemView.findViewById(R.id.other_pic);
            Rel_status=itemView.findViewById(R.id.Rel_status);
            edit_st=itemView.findViewById(R.id.edit_st);



            /*Rel_status.setOnClickListener(view -> {
                context.startActivity(new Intent(context.getApplicationContext(), Story_Activity.class));
            });*/
        }
    }
}
