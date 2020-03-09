package com.chatho.chatho.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatho.chatho.R;
import com.chatho.chatho.pojo.Messages;
import com.chatho.chatho.ui.ImageViewerActivity;
import com.chatho.chatho.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Messages> usermessageList;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private String RecieverID;

    public MessageAdapter(List<Messages> usermessageList, String RecieverID) {
        this.usermessageList = usermessageList;
        this.RecieverID=RecieverID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message,parent,false);
        auth=FirebaseAuth.getInstance();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String messageSenderId=auth.getCurrentUser().getUid();

        Messages messages=usermessageList.get(position);

        String fromUserID=messages.getFrom();
        String fromMessageType=messages.getType();


        userRef= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(fromUserID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")){
                    String recieverImage=dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(recieverImage).resize(100,100).centerInside().placeholder(R.drawable.user_icon).into(holder.recieverprof_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.recieverMessagetext.setVisibility(View.GONE);
        holder.recieverprof_img.setVisibility(View.GONE);
        holder.senderMessagetext.setVisibility(View.GONE);
        holder.messageSenderpic.setVisibility(View.GONE);
        holder.messageRecieverpic.setVisibility(View.GONE);



        if (fromMessageType.equals("text")) {

            if (fromUserID.equals(messageSenderId)){

                holder.senderMessagetext.setVisibility(View.VISIBLE);


                holder.senderMessagetext.setBackgroundResource(R.drawable.sender_message);
                holder.senderMessagetext.setTextColor(Color.WHITE);
                holder.senderMessagetext.setText(messages.getMessage());

                holder.text_message_time_sender.setText(messages.getTime());

            }else
            {

                holder.recieverMessagetext.setVisibility(View.VISIBLE);
                holder.recieverprof_img.setVisibility(View.VISIBLE);


                holder.recieverMessagetext.setBackgroundResource(R.drawable.reciever_message);
                holder.recieverMessagetext.setTextColor(Color.WHITE);
                holder.recieverMessagetext.setText(messages.getMessage());

                holder.text_message_time_reciever.setText(messages.getTime());



            }

        }
        else if (fromMessageType.equals("image")){
            if (fromUserID.equals(messageSenderId)){
                holder.messageSenderpic.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).resize(300,300).centerInside().into(holder.messageSenderpic);
            }else {

                holder.recieverprof_img.setVisibility(View.VISIBLE);
                holder.messageRecieverpic.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).resize(300,300).centerInside().into(holder.messageRecieverpic);
            }

        }
        else if (fromMessageType.equals("pdf")||fromMessageType.equals("docx") ){
            if (fromUserID.equals(messageSenderId)) {

                holder.messageSenderpic.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/chatapp-dbc2a.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=fa01db04-c7ac-4028-b38d-176325c5affd")
                        .into(holder.messageSenderpic);


                holder.itemView.setOnClickListener(view -> {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageList.get(position).getMessage()));
                    holder.itemView.getContext().startActivity(intent);

                });
            }else {

                holder.recieverprof_img.setVisibility(View.VISIBLE);
                holder.messageRecieverpic.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/chatapp-dbc2a.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=fa01db04-c7ac-4028-b38d-176325c5affd")
                        .into(holder.messageRecieverpic);


            }
            }



        if (fromUserID.equals(messageSenderId)){


            holder.itemView.setOnClickListener(view -> {


                if (usermessageList.get(position).getType().equals("pdf") || usermessageList.get(position).getType().equals("docx")){

                    CharSequence options[]=new CharSequence[]{
                            "Delete For me",
                            "Download and View This Document",
                            "Cancel",
                            "Delete for Everyone"
                    };
                    AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Delete Message?");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (i==0){
                                deleteSendMessage(position,holder);
                                Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                holder.itemView.getContext().startActivity(intent);
                            }
                            else if (i==1){

                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);

                            }
                            else if (i==3){
                                deleteMessageForEveryone(position,holder);
                                Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                holder.itemView.getContext().startActivity(intent);
                            }
                        }
                    });
                    builder.show();
                } else if (usermessageList.get(position).getType().equals("text")){

                                    CharSequence options[]=new CharSequence[]{
                                            "Delete For me",
                                            "Cancel",
                                            "Delete for Everyone"
                                    };
                                    AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                                    builder.setTitle("Delete Message?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            if (i==0){
                                                deleteSendMessage(position,holder);
                                                Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                                holder.itemView.getContext().startActivity(intent);
                                            }
                                            else if (i==2){
                                                deleteMessageForEveryone(position,holder);
                                                Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                                holder.itemView.getContext().startActivity(intent);
                                            }
                                        }
                                    });
                                    builder.show();
                                }else if (usermessageList.get(position).getType().equals("image") ){

                    CharSequence options[]=new CharSequence[]{
                            "Delete For me",
                            "View This Image",
                            "Cancel",
                            "Delete for Everyone"
                    };
                    AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Delete Message?");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (i==0){
                                deleteSendMessage(position,holder);
                                Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                holder.itemView.getContext().startActivity(intent);
                            }
                            else if (i==1){
                                Intent intent=new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                intent.putExtra("url",usermessageList.get(position).getMessage());
                                holder.itemView.getContext().startActivity(intent);
                            }
                            else if (i==3){
                                deleteMessageForEveryone(position,holder);

                            }
                        }
                    });
                    builder.show();
                }

                            });
                }else
                    { holder.itemView.setOnClickListener(view -> {
                if (usermessageList.get(position).getType().equals("pdf") || usermessageList.get(position).getType().equals("docx")){

                    CharSequence options[]=new CharSequence[]{
                            "Delete For me",
                            "Download and View This Document",
                            "Cancel",

                    };
                    AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Delete Message?");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (i==0){
                                deleteRecieveMessage(position,holder);
                            }
                            else if (i==1){

                                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageList.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);

                            }

                        }
                    });
                    builder.show();
                } else if (usermessageList.get(position).getType().equals("text")){

                    CharSequence options[]=new CharSequence[]{
                            "Delete For me",
                            "Cancel",
                    };
                    AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Delete Message?");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (i==0){
                                deleteRecieveMessage(position,holder);
                            }

                        }
                    });
                    builder.show();
                }else if (usermessageList.get(position).getType().equals("image") ){

                    CharSequence options[]=new CharSequence[]{
                            "Delete For me",
                            "View This Image",
                            "Cancel",
                    };
                    AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Delete Message?");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (i==0){

                                deleteRecieveMessage(position,holder);
                                Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                holder.itemView.getContext().startActivity(intent);

                            }
                            else if (i==1){
                                Intent intent=new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                intent.putExtra("url",usermessageList.get(position).getMessage());
                                holder.itemView.getContext().startActivity(intent);
                            }

                        }
                    });
                    builder.show();
                }
        });

        }
    }

    @Override
    public int getItemCount() {
        return usermessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView senderMessagetext ,recieverMessagetext,text_message_time_reciever,text_message_time_sender;
        CircleImageView recieverprof_img;
        ImageView messageSenderpic,messageRecieverpic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessagetext=itemView.findViewById(R.id.sender_message);
            recieverMessagetext=itemView.findViewById(R.id.reciver_message);
            recieverprof_img=itemView.findViewById(R.id.message_prof_img);
            messageSenderpic=itemView.findViewById(R.id.message_sender_img_view);
            messageRecieverpic=itemView.findViewById(R.id.message_reciever_img_view);
            text_message_time_reciever=itemView.findViewById(R.id.text_message_time_reciever);
            text_message_time_sender=itemView.findViewById(R.id.text_message_time_sender);

        }
    }

    private void deleteSendMessage(final int position,final ViewHolder holder ){
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(usermessageList.get(position).getFrom())
        .child(usermessageList.get(position).getTo())
        .child(usermessageList.get(position).getMessageID())
        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void deleteRecieveMessage(final int position,final ViewHolder holder ){
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(usermessageList.get(position).getTo())
                .child(usermessageList.get(position).getFrom())
                .child(usermessageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void deleteMessageForEveryone(final int position,final ViewHolder holder ){
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(usermessageList.get(position).getTo())
                .child(usermessageList.get(position).getFrom())
                .child(usermessageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    rootRef.child("Messages")
                            .child(usermessageList.get(position).getFrom())
                            .child(usermessageList.get(position).getTo())
                            .child(usermessageList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(holder.itemView.getContext(), "Deleted Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
