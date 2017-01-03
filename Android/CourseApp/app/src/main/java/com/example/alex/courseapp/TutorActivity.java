package com.example.alex.courseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class TutorActivity extends AppCompatActivity {

    /*
         TextView jokeOne;
         jokeOne = (TextView) findViewById(R.id.joke_one);

         Intent intent = getIntent();
         String studentJSONString = intent.getStringExtra("studentInfoJSON");

         JSONArray studentInfoJSON = new JSONArray(studentJSONString);

         JSONObject firstJoke = (JSONObject) studentInfoJSON.get(0);
         String first = firstJoke.getString("joke");

         jokeOne.setText(first);
     */
    JSONArray studentInfoJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        final ListView idList = (ListView) findViewById(R.id.id_list);
        ArrayList<String> id_s = new ArrayList<String>();

        Intent intent = getIntent();
        String studentJSONString = intent.getStringExtra("studentInfoJSON");
        try {

            studentInfoJSON = new JSONArray(studentJSONString);
            for(int i = 0; i < studentInfoJSON.length(); i++){

                JSONObject current = studentInfoJSON.getJSONObject(i);
                String current_id = current.getString("id");
                id_s.add(current_id);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, id_s);
        idList.setAdapter(adapter);
        idList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String idClicked = idList.getItemAtPosition(position).toString();
                try {

                    for(int i = 0; i < studentInfoJSON.length(); i++){

                        JSONObject current = studentInfoJSON.getJSONObject(i);
                        String current_id = current.getString("id");
                        String current_joke = current.getString("joke");
                        if(current_id == idClicked){
                            Intent intent = new Intent(TutorActivity.this, TutorDetailsActivity.class);
                            intent.putExtra("selectedJoke", current_joke);
                            startActivity(intent);
                            break;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
