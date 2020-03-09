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
import com.chatho.chatho.pojo.Group_Messages;
import com.chatho.chatho.ui.ImageViewerActivity;
import com.chatho.chatho.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class groupmessage_adpt extends RecyclerView.Adapter<groupmessage_adpt.ViewHolder>  {

    private Context context;
    private List<Group_Messages> usersmessageList;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private String myID;
    private String groupname;

    public groupmessage_adpt(List<Group_Messages> usersmessageList, String myID, String groupname) {
        this.usersmessageList = usersmessageList;
        myID = myID;
        this.groupname=groupname;
    }

    @NonNull
    @Override
    public groupmessage_adpt.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_group_messages,parent,false);
        auth=FirebaseAuth.getInstance();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull groupmessage_adpt.ViewHolder holder, int position) {

        String messageSenderId=auth.getCurrentUser().getUid();

        Group_Messages messages=usersmessageList.get(position);

        String fromUserID=messages.getFrom();
        String fromMessageType=messages.getType();

        //Toast.makeText(holder.itemView.getContext(), ""+fromUserID+"------"+messageSenderId, Toast.LENGTH_SHORT).show();

        userRef= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(fromUserID);



        holder.recieverMessagetext.setVisibility(View.GONE);
        holder.reciever_name.setVisibility(View.GONE);
        holder.senderMessagetext.setVisibility(View.GONE);
        holder.sender_name.setVisibility(View.GONE);
        holder.messageSenderpic.setVisibility(View.GONE);
        holder.messageRecieverpic.setVisibility(View.GONE);


        holder.text_message_time_sender.setVisibility(View.GONE);
        holder.text_message_time_reciever.setVisibility(View.GONE);



        if (fromMessageType.equals("text")) {

        if (fromUserID.equals(messageSenderId)){

            holder.senderMessagetext.setVisibility(View.VISIBLE);
            holder.sender_name.setVisibility(View.VISIBLE);
            holder.text_message_time_sender.setVisibility(View.VISIBLE);

            holder.sender_name.setText(messages.getName());
            holder.senderMessagetext.setBackgroundResource(R.drawable.sender_message);
            holder.senderMessagetext.setTextColor(Color.WHITE);
            holder.senderMessagetext.setText(messages.getMessage());

            holder.text_message_time_sender.setText(messages.getTime());

        }else
        {

            holder.recieverMessagetext.setVisibility(View.VISIBLE);
            holder.reciever_name.setVisibility(View.VISIBLE);
            holder.text_message_time_reciever.setVisibility(View.VISIBLE);

            holder.reciever_name.setText(messages.getName());
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
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usersmessageList.get(position).getMessage()));
                holder.itemView.getContext().startActivity(intent);

            });
        }else {

            holder.messageRecieverpic.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load("https://firebasestorage.googleapis.com/v0/b/chatapp-dbc2a.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=fa01db04-c7ac-4028-b38d-176325c5affd")
                    .into(holder.messageRecieverpic);


        }
    }



        if (fromUserID.equals(messageSenderId)){


        holder.itemView.setOnClickListener(view -> {


            if (usersmessageList.get(position).getType().equals("pdf") || usersmessageList.get(position).getType().equals("docx")){

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

                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usersmessageList.get(position).getMessage()));
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
            } else if (usersmessageList.get(position).getType().equals("text")){

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
            }else if (usersmessageList.get(position).getType().equals("image") ){

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
                            intent.putExtra("url",usersmessageList.get(position).getMessage());
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
        if (usersmessageList.get(position).getType().equals("pdf") || usersmessageList.get(position).getType().equals("docx")){

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

                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usersmessageList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);

                    }

                }
            });
            builder.show();
        } else if (usersmessageList.get(position).getType().equals("text")){

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
        }else if (usersmessageList.get(position).getType().equals("image") ){

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
                        intent.putExtra("url",usersmessageList.get(position).getMessage());
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
        return usersmessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessagetext ,recieverMessagetext,text_message_time_reciever,text_message_time_sender,sender_name,reciever_name;
        ImageView messageSenderpic,messageRecieverpic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessagetext=itemView.findViewById(R.id.sender_message);
            recieverMessagetext=itemView.findViewById(R.id.reciver_message);
            messageSenderpic=itemView.findViewById(R.id.message_sender_img_view);
            messageRecieverpic=itemView.findViewById(R.id.message_reciever_img_view);
            text_message_time_reciever=itemView.findViewById(R.id.text_message_time_reciever);
            text_message_time_sender=itemView.findViewById(R.id.text_message_time_sender);
            sender_name=itemView.findViewById(R.id.sender_name);
            reciever_name=itemView.findViewById(R.id.reciver_name);

        }
    }

private void deleteSendMessage(final int position,final ViewHolder holder ){
    DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
    rootRef.child("Groups")
            .child(groupname).child(usersmessageList.get(position).getFrom())
            .child(usersmessageList.get(position).getMessageID())
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
        rootRef.child("Groups")
                .child(groupname)
                .child(usersmessageList.get(position).getFrom())
                .child(usersmessageList.get(position).getMessageID())
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
        rootRef.child("Groups")
                .child(groupname)
                .child(usersmessageList.get(position).getFrom())
                .child(usersmessageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    rootRef.child("Groups")
                            .child(groupname)
                            .child(usersmessageList.get(position).getFrom())
                            .child(usersmessageList.get(position).getMessageID())
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
