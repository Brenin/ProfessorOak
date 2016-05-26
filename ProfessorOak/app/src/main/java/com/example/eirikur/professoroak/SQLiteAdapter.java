package com.example.eirikur.professoroak;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Bruker on 26.05.2016.
 */
public class SQLiteAdapter {
    public static final String DATABASE_NAME = "Pokemons";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME="pokemon";

    public static final String POKEMON_ID = "_id";
    public static final String POKEMON_NAME = "name";
    public static final String POKEMON_LAT = "lat";
    public static final String POKEMON_LNG = "lng";

    public static final String PREFERENCES_DB = "PreferencesDb";

    private SQLiteHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    private SharedPreferences preferences;

    private long id = 1;

    public SQLiteAdapter (Context context) {
        this.context = context;

        preferences = context.getSharedPreferences(PREFERENCES_DB, Activity.MODE_APPEND);
        if(preferences != null){
            id = preferences.getLong("nextId", 1);
        }
    }

    public SQLiteAdapter open () {
        databaseHelper = new SQLiteHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
        sqLiteDatabase = databaseHelper.getWritableDatabase(); //Should be in a background thread, i.e. Async
        return this;
    }

    public long create(Pokemon pokemon) {
        ContentValues values = new ContentValues();
        values.put(POKEMON_ID,id);
        values.put(POKEMON_NAME, pokemon.getName());
        values.put(POKEMON_LAT, pokemon.getLat());
        values.put(POKEMON_LNG, pokemon.getLng());

        sqLiteDatabase.insert(TABLE_NAME,null,values);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("nextId",id+1);
        editor.commit();

        return id++;
    }

    public Cursor readAll() {
        String[] columns = new String[]{POKEMON_ID, POKEMON_NAME, POKEMON_LAT, POKEMON_LNG};
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        //return sqLiteDatabase.query(TABLE_NAME, columns, null, null, null, null, null);
        //(Table_name, columns, where, where args, group by, order by, having)
    }

    public boolean update(Long pokemonId, Pokemon pokemon) {
        ContentValues values = new ContentValues();
        values.put(POKEMON_NAME, pokemon.getName());
        //values.put(PERSON_AGE, person.getAge());

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
