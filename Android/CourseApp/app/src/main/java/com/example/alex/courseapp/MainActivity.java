package com.example.alex.courseapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //Button login = (Button) findViewById(R.id.Login_button);

    EditText password;
    EditText username;

    RadioButton studentRadio;
    RadioButton tutorRadio;

    HttpURLConnection connection = null;
    BufferedReader reader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);

        studentRadio = (RadioButton) findViewById(R.id.student_radio);
        tutorRadio = (RadioButton) findViewById(R.id.tutor_radio);
    }

    public void login_click(View view) {

        /*Ion.with(this).load("http://api.icndb.com/jokes/").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if(studentRadio.isChecked() || tutorRadio.isChecked()){
                    checkPassword(result);
                }else {
                    Toast.makeText(MainActivity.this, "Please select to log in either as student or tutor", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        new HandleReceivingConnection().execute();
    }



    private void checkPassword(String result) {

        String entered_password = password.getText().toString();
        String entered_username = username.getText().toString();

        try {

            //Chuck norris REST API
            JSONObject jsonObject = new JSONObject(result);
            JSONArray elements = jsonObject.getJSONArray("value");

            //JSON Placeholder REST API
            //JSONArray elements = new JSONArray(result);

            for (int i = 0; i < elements.length(); i++){
                JSONObject item = elements.getJSONObject(i);
                String json_username = item.getString("id");
                String json_password = item.getString("joke");

                if(entered_username.equals(json_username) && entered_password.equals(json_password)){

                    if(tutorRadio.isChecked()){
                        processTutorData(result);
                        break;
                    }else if(studentRadio.isChecked()){
                        processStudentData(json_username, json_password);
                        break;
                    }

                }else{
                    Toast.makeText(this, "Wrong password or username", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processStudentData(String username, String password) {

        Intent intent = new Intent(this, OverviewActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);

    }

        /*

    { "type": "success",
      "value": [
                { "id": 1,
                  "joke": "Chuck Norris uses ribbed condoms inside out, so he gets the pleasure.",
                  "categories": ["explicit"]
                  },

                { "id": 2,
                  "joke": "MacGyver can build an airplane out of gum and paper clips. Chuck Norris can kill him and take it.",
                  "categories": []
                 }
                ]
     }

     */

    private void processTutorData(String result) {

        JSONObject jsonObject = null;
        JSONArray sendWIthIntent = new JSONArray();
        try {
            jsonObject = new JSONObject(result);
            JSONArray elements = jsonObject.getJSONArray("value");

            for (int i = 0; i < 10; i++) {

                JSONObject item = elements.getJSONObject(i);
                sendWIthIntent.put(item);

            }

            Toast.makeText(this, "Check", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, TutorActivity.class);
            intent.putExtra("studentInfoJSON", sendWIthIntent.toString());
            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class HandleReceivingConnection extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL("http://api.icndb.com/jokes/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
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
            } finally {
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

            if(studentRadio.isChecked() || tutorRadio.isChecked()){
                checkPassword(result);
            }else {
                Toast.makeText(MainActivity.this, "Please select to log in either as student or tutor", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
