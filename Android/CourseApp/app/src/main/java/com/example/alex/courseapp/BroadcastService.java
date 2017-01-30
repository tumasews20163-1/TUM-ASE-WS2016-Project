package com.example.alex.courseapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by memi on 1/12/17.
 */
public class BroadcastService extends Service {

    BufferedReader reader;
    HttpURLConnection connection;

    private RequestQueue requestQueue;
    private StringRequest request;

    //added
    static String url  = "http://utility-node-147216.appspot.com/api/student";


    public static final String BROADCAST_ACTION = "BROADCAST";
    public Context context = this;
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        public void run() {
            //Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();

            new HandleReceivingConnection().execute();
            handler.postDelayed(this, 10000); //10s

        }
    };

    Intent intentService;
    Intent intent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        this.intent = intent;
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        intentService = new Intent(BROADCAST_ACTION);

        handler.removeCallbacks(runnable);

        handler.postDelayed(runnable, 2000); //2s

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }


    public class HandleReceivingConnection extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                String urlString= url;
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");

                connection.setRequestProperty("username",BroadcastService.this.intent.getStringExtra("username"));
                connection.setRequestProperty("password", BroadcastService.this.intent.getStringExtra("password"));
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String result = buffer.toString();

                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  finally {
                if(connection != null){
                    connection.disconnect();
                }
                try {
                    if(reader != null){
                        reader.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            intentService.putExtra("values",result);
            sendBroadcast(intentService);
        }
    }

}
