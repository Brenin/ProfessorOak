package com.example.eirikur.professoroak;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

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

    public void create(final Pokemon pokemon) {
        new AsyncTask<Pokemon, Void, Void>() {
            protected Void doInBackground (final Pokemon... params) {
                Pokemon pokemonCaught = pokemon;
                ContentValues values = new ContentValues();
                values.put(POKEMON_ID,pokemonCaught.getId());
                values.put(POKEMON_NAME, pokemonCaught.getName());
                values.put(POKEMON_LAT, pokemonCaught.getLat());
                values.put(POKEMON_LNG, pokemonCaught.getLng());
                sqLiteDatabase.insert(TABLE_NAME,null,values);
                return null;
            }
        }.execute();
    }

    public Cursor readAll() {
        String[] columns = new String[]{POKEMON_ID, POKEMON_NAME, POKEMON_LAT, POKEMON_LNG};
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public void close() {
        sqLiteDatabase.close();
    }
}
