package com.example.alex.courseapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
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
import java.util.List;

public class OverviewActivity extends Activity {

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
        Intent intent = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        matnrView = (TextView) findViewById(R.id.mat_nr);
        fullNameView = (TextView) findViewById(R.id.fullname);
        exerciseGroup = (TextView) findViewById(R.id.exercise_group);
        usernameView = (TextView) findViewById(R.id.username);
        bonusView = (TextView) findViewById(R.id.bonus);
        qr_image = (ImageView) findViewById(R.id.qr_image);
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
            {
                bonusView.setText("Yes");
            }
            else if(bonus == 0)
            {
                bonusView.setText("No");
            }
            else
            {
                bonusView.setText("Error in database");
            }

            String qrCodeString = studentJSON.getString("qrcode");

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            BitMatrix bitMatrix = multiFormatWriter.encode(qrCodeString, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr_image.setImageBitmap(bitmap );

                this.startService();
                registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));



        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(context, intent);
        }
    };

    private void updateUI(Context context, Intent intent) {
        try {

            String val = intent.getStringExtra("values");
            JSONObject obj = new JSONObject(val);
            val = obj.getString("status");

            if(val.equals("ok"))
            {
                bonusView.setText("Yes");
                this.stopService();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.notification_1);
                mBuilder.setContentTitle("Attendance Tracker");
                mBuilder.setContentText("You achieved your bonus!");

                //TODO After closing the app, and clicking on notification, the overview activity appear... maybe just bug in emulator?
                Intent launchIntent = getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);


                mBuilder.setContentIntent(contentIntent);

                //Add notification
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(1, mBuilder.build());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void startService() {
        Intent t = new Intent(getBaseContext(), BroadcastService.class);
        t.putExtra("username",usernameToDisplay);
        startService(t);
    }

    // Method to stop the service
    public void stopService() {
        unregisterReceiver(broadcastReceiver);
        stopService(new Intent(getBaseContext(), BroadcastService.class));
    }

}
