package com.example.eirikur.professoroak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HighScoreActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        listView = (ListView) findViewById(R.id.highScoreView);
        displayData();
    }

    void displayData(){
        ArrayAdapter<Person> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, MapsActivity.highScore);
        listView.setAdapter(adapter);
    }
}
