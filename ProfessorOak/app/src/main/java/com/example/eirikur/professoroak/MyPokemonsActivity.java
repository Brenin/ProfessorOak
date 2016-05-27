package com.example.eirikur.professoroak;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

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

        SQLiteAdapter adapter = new SQLiteAdapter(this);
        adapter.open();
        Cursor cursor = adapter.readAll();

        String[] columns = new String[]{
                SQLiteAdapter.POKEMON_ID,
                SQLiteAdapter.POKEMON_NAME
        };

        int[] views = new int[]{
                R.id.pokemonId,
                R.id.pokemonName,
        };

        initStuff();

        CursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.pokemon_info, cursor, columns, views, 0);
        ListView listView = (ListView) findViewById(R.id.MyPokemonsListView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String pokemonName =
                        cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Toast.makeText(getApplicationContext(),
                        pokemonName, Toast.LENGTH_SHORT).show();

            }
        });
    }

        void initStuff(){
            highScorebtn = (Button) findViewById(R.id.highScoreButton);

            highScorebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyPokemonsActivity.this, HighScoreActivity.class);
                    startActivity(intent);
                }
            });
        }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pokemons);


        listView = (ListView) findViewById(R.id.MyPokemonsListView);

        Bundle extras = getIntent().getExtras();
        parseData(extras);

        initStuff();
    }

    void initStuff(){
        highScorebtn = (Button) findViewById(R.id.highScoreButton);

        highScorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPokemonsActivity.this, HighScoreActivity.class);
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

    void displayData(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parsedList);
        listView.setAdapter(adapter);
    }
    */
    }


