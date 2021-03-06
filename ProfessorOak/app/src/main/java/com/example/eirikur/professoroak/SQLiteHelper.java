package com.example.eirikur.professoroak;
import com.example.eirikur.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bruker on 26.05.2016.
 */
public class SQLiteHelper extends SQLiteOpenHelper{
    private static final String SCRIPT_CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS PokemonTableVersionTwo ("
            + "pokemonID text not null, "
            + "name text not null, "
            + "lat text not null, "
            + "lng text not null)";

    public SQLiteHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version ) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
