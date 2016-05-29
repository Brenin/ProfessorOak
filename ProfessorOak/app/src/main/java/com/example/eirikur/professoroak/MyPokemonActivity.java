package com.example.eirikur.professoroak;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MyPokemonActivity extends AppCompatActivity {

    private ListView listView;

    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pokemons);

        initStuff();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initStuff();
    }

    void initStuff(){
        Button highScorebtn = (Button) findViewById(R.id.highScoreButton);

        highScorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPokemonActivity.this, HighScoreActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView) findViewById(R.id.MyPokemonsListView);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MapsActivity.checkList);
        listView.setAdapter(adapter2);
    }
}


