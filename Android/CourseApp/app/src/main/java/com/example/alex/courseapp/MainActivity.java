package com.example.alex.courseapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText TFPassword;
    EditText TFUsername;

    HttpURLConnection connection = null;
    BufferedReader reader = null;

    //added
    private RequestQueue requestQueue;
    private StringRequest request;

    //added
    static String url  = "http://localhost:8080/api/student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TFPassword = (EditText) findViewById(R.id.password);
        TFUsername = (EditText) findViewById(R.id.username);

        //added
        requestQueue = Volley.newRequestQueue(this);

    }


    String entered_password = null;
    String entered_username = null;

    public void login_click(View view) {

        entered_password = this.TFPassword.getText().toString();
        entered_username = this.TFUsername.getText().toString();

        if(entered_username.length() != 0 && entered_password.length() != 0){

            //added
            request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //Response from Server could be also handled here rather than in HandleReceivingConnection()

                    //login(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
               @Override
               protected Map<String, String> getParams() throws AuthFailureError{
                   //parameters to be sent to server (username, password)
                   HashMap<String, String> hashMap = new HashMap<String, String>();
                   hashMap.put("username", entered_username);
                   hashMap.put("password", entered_password);

                   return hashMap;
               }
            };

            //added
            requestQueue.add(request);

            new HandleReceivingConnection().execute();
        }
        else {
            Toast.makeText(this, "Please enter your username and password to log in", Toast.LENGTH_SHORT).show();
        }

    }


    private void login(String result) {

        try {

            JSONObject jsonObject= new JSONObject(result);
            String status = jsonObject.getString("status");

            if(checkStatus(status) < 0)
                return;

            JSONObject studentJSONObject = jsonObject.getJSONObject("value");

            processStudentData(studentJSONObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int checkStatus(String status)
    {
        if(status != null && status.equals("ok"))
        {
            Toast.makeText(MainActivity.this, "Login successful",Toast.LENGTH_SHORT).show();
            return 0;
        }

        Toast.makeText(this, "Wrong password or username", Toast.LENGTH_SHORT).show();
        return -1;

    }

    private void processStudentData(JSONObject student) {

        Intent intent = new Intent(this, OverviewActivity.class);

        intent.putExtra("student",student.toString());
        startActivity(intent);
//        finish();

    }


    public class HandleReceivingConnection extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try {

                String urlString= "http://atsesandbox.getsandbox.com/students?username=" + entered_username + "&password=" + entered_password;
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");


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

            login(result);
        }
    }

}
