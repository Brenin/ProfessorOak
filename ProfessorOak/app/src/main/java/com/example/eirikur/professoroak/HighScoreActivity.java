package com.example.eirikur.professoroak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HighScoreActivity extends AppCompatActivity {

    private String url = "https://locations.lehmann.tech/scores";
    private ListView listView;
    private ArrayList<Person> highScore = new ArrayList<>();

    HTTPRequests httpRequests = new HTTPRequests(url);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        listView = (ListView) findViewById(R.id.highScoreView);

        highScore = httpRequests.getWorkList();

        displayData();
    }

    void displayData(){
        ArrayAdapter<Person> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, highScore);
        listView.setAdapter(adapter);
    }
}
