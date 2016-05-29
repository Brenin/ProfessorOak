package com.example.eirikur.professoroak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class HighScoreActivity extends AppCompatActivity {

    private String url = "https://locations.lehmann.tech/scores";
    private ListView listView;
    public static ArrayList<Person> highScore = new ArrayList<>();

    HTTPRequests httpRequests = new HTTPRequests();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        listView = (ListView) findViewById(R.id.highScoreView);
        httpRequests.getAndDisplayHighscore(url);
        displayData();
    }

    @Override
    protected void onResume(){
        super.onResume();
        highScore.clear();
    }

    void displayData(){
        ArrayAdapter<Person> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, highScore);
        listView.setAdapter(adapter);
    }
}
