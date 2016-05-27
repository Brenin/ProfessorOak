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

    private ArrayList<Pokemon> list = new ArrayList<>();
    private ArrayList<String> parsedList = new ArrayList<>();
    private ListView listView;
    StringBuilder sb = new StringBuilder();
    private Button highScorebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        listView = (ListView) findViewById(R.id.listView);

        Bundle extras = getIntent().getExtras();
        parseData(extras);

        initStuff();
    }

    void initStuff(){
        highScorebtn = (Button) findViewById(R.id.highScoreButton1);

        highScorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PokemonListActivity.this, HighScoreActivity.class);
                startActivity(intent);
            }
        });
    }

    void parseData(Bundle extras){
        if(extras != null){
            list = extras.getParcelableArrayList("pokemonList");
        }

        for(int i = 0; i < list.size(); i++){
            sb.append("Name: " + list.get(i).getName()
                    + "\nID: " + list.get(i).getId()
            );
            parsedList.add(sb.toString());
            sb.setLength(0);
        }
        displayData();
    }

    public void HighScoreClick(View view){
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

    void displayData(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parsedList);
        listView.setAdapter(adapter);
    }
}
