package com.example.alex.courseapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OverviewActivity extends AppCompatActivity {

    TextView matnrView;
    TextView fullNameView;
    TextView exerciseGroup;
    TextView usernameView;
    TextView bonusView;

    String matnrToDisplay;
    String fullNameToDisplay;
    String exerciseGroupToDisplay;
    String usernameToDisplay;
    int bonus;

    ImageView qr_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        matnrView = (TextView) findViewById(R.id.mat_nr);
        fullNameView = (TextView) findViewById(R.id.fullname);
        exerciseGroup = (TextView) findViewById(R.id.exercise_group);
        usernameView = (TextView) findViewById(R.id.username);
        bonusView = (TextView) findViewById(R.id.bonus);
        qr_image = (ImageView) findViewById(R.id.qr_image);

        Intent intent = getIntent();
        setValuesOnUI(intent);

    }

    private void setValuesOnUI(Intent intent) {

        try {
            String student = intent.getStringExtra("student");
            JSONObject studentJSON = new JSONObject(student);

            matnrToDisplay = studentJSON.getString("matrikelnum");
            fullNameToDisplay = studentJSON.getString("firstname") + " " + studentJSON.getString("lastname");
            exerciseGroupToDisplay = studentJSON.getString("group");
            usernameToDisplay = studentJSON.getString("username");
            bonus = studentJSON.getInt("bonus");

            matnrView.setText(matnrToDisplay);
            fullNameView.setText(fullNameToDisplay);
            usernameView.setText(usernameToDisplay);
            exerciseGroup.setText(exerciseGroupToDisplay);

            if(bonus == 1)
                bonusView.setText("Yes");
            else if(bonus == 0)
                bonusView.setText("No");
            else
                bonusView.setText("Error in database");

            String qrCodeString = studentJSON.getString("qrcode");

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            BitMatrix bitMatrix = multiFormatWriter.encode(qrCodeString, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr_image.setImageBitmap(bitmap );


        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
