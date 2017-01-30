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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText TFPassword;
    EditText TFUsername;

    //added
    private RequestQueue requestQueue;
    private StringRequest request;

    //added
    static String url  = "http://utility-node-147216.appspot.com/api/student";

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

                   login(response);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){

               @Override
               public Map<String, String> getHeaders() throws AuthFailureError {
                   //parameters to be sent to server (username, password)
                   Map<String, String> map = new HashMap<>();
                   map.put("username", entered_username);
                   map.put("password", entered_password);

                   return map;
               }

            };

            //added
            requestQueue.add(request);

        }
        else {
            Toast.makeText(this, "Please enter your username and password to log in", Toast.LENGTH_SHORT).show();
        }

    }


    private void login(String result) {

        try {

            JSONObject jsonObject= new JSONObject(result);

            if(checkStatus(jsonObject) < 0)
                return;

            processStudentData(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int checkStatus(JSONObject result)
    {
        if(result!= null && !result.has("ErrorType"))
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
        intent.putExtra("password",entered_password);
        startActivity(intent);
//        finish();

    }

}
