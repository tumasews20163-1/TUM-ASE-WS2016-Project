package com.example.alex.courseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OverviewActivity extends AppCompatActivity {

    TextView matnrView;
    TextView nameView;
    TextView exerciseGroup;

    Spinner groups;
    ArrayAdapter<CharSequence> adapter;
    String[] group_names = {"1", "2", "3"};
    String selected_group;

    String matnrToDisplay;
    String nameToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        matnrView = (TextView) findViewById(R.id.mat_nr);
        nameView = (TextView) findViewById(R.id.name);
        exerciseGroup = (TextView) findViewById(R.id.exercise_group);

        groups = (Spinner) findViewById(R.id.group_spinner);

        Intent intent = getIntent();
        matnrToDisplay = intent.getStringExtra("username");
        nameToDisplay = intent.getStringExtra("password");

        matnrView.setText(matnrToDisplay);
        nameView.setText(nameToDisplay);

        adapter = new  ArrayAdapter(this, android.R.layout.simple_spinner_item, group_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groups.setAdapter(adapter);
        groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_group = (String) parent.getItemAtPosition(position);
                exerciseGroup.setText("You are requesting to attend group " + selected_group + ". Press Confirm to submit change");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void qrcode_click(View view) {
        Intent intent = new Intent(this, QRDisplayActivity.class);
        intent.putExtra("toBeEncoded", nameToDisplay);
        startActivity(intent);
    }

    public void confirm_click(View view) {
        String myUrl = "";
        JSONObject json = createPackageToSend();
        try {
            URL url = new URL(myUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            printout.write(json.toString().getBytes("UTF8"));

            printout.flush();
            printout.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private JSONObject createPackageToSend() {
        JSONObject result = new JSONObject();

        try {
            result.put("matrikelnummer", matnrToDisplay);
            result.put("name", nameToDisplay);
            result.put("group", selected_group);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
