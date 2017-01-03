package com.example.alex.courseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TutorDetailsActivity extends AppCompatActivity {

    TextView detailsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_details);

        detailsView = (TextView) findViewById(R.id.details_view);

        Intent intent = getIntent();
        String receivedJoke = intent.getStringExtra("selectedJoke");

        detailsView.setText(receivedJoke);
    }
}
