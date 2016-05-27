package com.example.eirikur.professoroak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MyPokemonsActivity extends AppCompatActivity {

    private ArrayList<Pokemon> list = new ArrayList<>();
    private ArrayList<String> parsedList = new ArrayList<>();

    private ListView listView;
    private Button highScorebtn;

    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pokemons);

        highScorebtn = (Button) findViewById(R.id.highScoreButton);
        listView = (ListView) findViewById(R.id.MyPokemonsListView);

        Bundle extras = getIntent().getExtras();
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
        initListeners();
    }

    private void initListeners() {
        highScorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void displayData(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parsedList);
        listView.setAdapter(adapter);
    }
}
