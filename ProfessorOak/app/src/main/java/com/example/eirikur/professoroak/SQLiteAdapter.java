package com.example.eirikur.professoroak;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

/**
 * Created by Bruker on 26.05.2016.
 */
public class SQLiteAdapter {
    public static final String DATABASE_NAME = "Pokemons";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME="pokemon";

    public static final String POKEMON_ID = "id";
    public static final String POKEMON_NAME = "name";
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
        databaseHelper = new SQLiteHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public long create(Pokemon pokemon) {
        ContentValues values = new ContentValues();
        values.put(POKEMON_ID, id);
        values.put(POKEMON_NAME, pokemon.getName());
        values.put(POKEMON_LAT, pokemon.getLat());
        values.put(POKEMON_LNG, pokemon.getLng());

        sqLiteDatabase.insert(TABLE_NAME,null,values);
        return id++;
    }

    public String readAll() {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT name FROM " + TABLE_NAME, null);
        if(cursor.moveToFirst() && cursor.getCount() <= 1){
            for(int i = 0; i < cursor.getCount(); i ++){
                MapsActivity.checkList.add(cursor.getString(i));
            }
        }

        cursor.close();
        return "";
    }

    public boolean update(Long pokemonId, Pokemon pokemon) {
        ContentValues values = new ContentValues();
        values.put(POKEMON_NAME, pokemon.getName());

        String whereClause = POKEMON_ID + " = ?";
        //String[] whereArgs = new String[]{personId.toString()};

        //int numberOfRowsUpdated = sqLiteDatabase.update(TABLE_NAME, values, whereClause, whereArgs);

        //return (numberOfRowsUpdated == 1);
        return false;
    }

    public void close() {
        sqLiteDatabase.close();
    }
}
