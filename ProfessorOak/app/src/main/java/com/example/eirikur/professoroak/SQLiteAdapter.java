package com.example.eirikur.professoroak;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Bruker on 26.05.2016.
 */
public class SQLiteAdapter {
    public static final String DATABASE_NAME = "Pokemons";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME="PokemonTableVersionTwo";

    public static final String ROW_ID = "id";
    public static final String POKEMON_NAME = "name";
    public static final String POKEMON_ID = "pokemonID";
    public static final String POKEMON_LAT = "lat";
    public static final String POKEMON_LNG = "lng";

    private SQLiteHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    private long id = 1;

    public SQLiteAdapter (Context context) {
        this.context = context;
    }

    public SQLiteAdapter open () {
        databaseHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public long create(Pokemon pokemon) {
        ContentValues values = new ContentValues();
        values.put(POKEMON_ID, pokemon.getId());
        values.put(POKEMON_NAME, pokemon.getName());
        values.put(POKEMON_LAT, pokemon.getLat());
        values.put(POKEMON_LNG, pokemon.getLng());

        sqLiteDatabase.insert(TABLE_NAME,null,values);
        return id++;
    }

    public ArrayList<Pokemon> getAllObjects(){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<Pokemon> objectList = new ArrayList<>();

        try {
            if(cursor.moveToFirst()){
                do {
                    Pokemon pokemon = new Pokemon(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3));
                    objectList.add(pokemon);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            cursor.close();
        }
        return objectList;
    }

    public boolean update(Long pokemonId, Pokemon pokemon) {
        ContentValues values = new ContentValues();
        values.put(POKEMON_NAME, pokemon.getName());

        String whereClause = ROW_ID + " = ?";
        //String[] whereArgs = new String[]{personId.toString()};

        //int numberOfRowsUpdated = sqLiteDatabase.update(TABLE_NAME, values, whereClause, whereArgs);

        //return (numberOfRowsUpdated == 1);
        return false;
    }

    public void close() {
        sqLiteDatabase.close();
    }
}
