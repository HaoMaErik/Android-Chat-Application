package com.example.erik.snapchat_team_nm;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

/**
 * Created by Erik on 2016/10/16.
 */
public class ViewImageActivity extends AppCompatActivity {
    ImageView ivLoad;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageload);

        ivLoad = (ImageView)findViewById(R.id.ivViewImage);
        imageUrl = getIntent().getStringExtra("url");

        Picasso.with(this).load(imageUrl).into(ivLoad);

    }
}
