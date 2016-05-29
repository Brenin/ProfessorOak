package com.example.eirikur.professoroak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class PokemonListActivity extends AppCompatActivity {

    private ArrayList<String> parsedList = new ArrayList<>();
    private ListView listView;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        listView = (ListView) findViewById(R.id.listView);

        parseData();
        initStuff();
    }

    void initStuff(){
        Button highScorebtn = (Button) findViewById(R.id.highScoreButton1);

        highScorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PokemonListActivity.this, HighScoreActivity.class);
                startActivity(intent);
            }
        });
    }

    void parseData(){

        for(int i = 0; i < MapsActivity.listAvailable.size(); i++){
            sb.append("Name: " + MapsActivity.listAvailable.get(i).getName()
                    + "\nID: " + MapsActivity.listAvailable.get(i).getId()
            );
            parsedList.add(sb.toString());
            sb.setLength(0);
        }
        displayData();
    }

    void displayData(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parsedList);
        listView.setAdapter(adapter);
    }
}
