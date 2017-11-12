package com.example.erik.snapchat_team_nm;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGeneratorActivity extends AppCompatActivity {
    String username;
    ImageView ivQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);
        //get the current username
        username = getIntent().getStringExtra("username");
        ivQR = (ImageView)findViewById(R.id.ivQRcode);

        MultiFormatWriter mfw = new MultiFormatWriter();
        try{
            BitMatrix bitmax = mfw.encode(username, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcode = new BarcodeEncoder();
            Bitmap bitmap = barcode.createBitmap(bitmax);
            ivQR.setImageBitmap(bitmap);
        }
        catch (WriterException e){
            e.printStackTrace();
        }

    }
}
