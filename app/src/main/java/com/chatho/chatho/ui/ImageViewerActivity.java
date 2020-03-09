package com.chatho.chatho.ui;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chatho.chatho.R;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity  extends AppCompatActivity {
    private ImageView imageView;
    private String imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageviewer_layout);

        imageView=findViewById(R.id.img_v);
        imageUri=getIntent().getStringExtra("url");
        Picasso.get().load(imageUri).into(imageView);
    }
}
