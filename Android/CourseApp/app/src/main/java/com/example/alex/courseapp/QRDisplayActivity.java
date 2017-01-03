package com.example.alex.courseapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRDisplayActivity extends AppCompatActivity {

    TextView studentName;
    ImageView qr_image;
    String toBeEncoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdisplay);

        studentName = (TextView) findViewById(R.id.studentName);
        qr_image = (ImageView) findViewById(R.id.qr_image);

        Intent intent = getIntent();
        toBeEncoded = intent.getStringExtra("toBeEncoded").trim();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(toBeEncoded, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr_image.setImageBitmap(bitmap );
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}
