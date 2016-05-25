package proffesoroak.westerdals.no.professoroak211;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bruker on 19.05.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context){
        super(context, "lol"/*DATABASE_NAME*/, null, 666/*DATABASE_VERSION*/);
    }

    /*private static final String CREATING_POKEMON_DATABASE = "CREATE TABLE "
            + TABLE_NAME + " (" + PERSON_ID + " NUMBER PRIMARY KEY,"
            + POKEMON_NAME + " TEXT NOT NULL,"
            + PERSON_AGE + " NUMBER NOT NULL,"
            + POKEMON_LNG + " NUMBER NOT NULL,"
            + POKEMON_LAT + " TEXT NOT NULL )";*/

    public DatabaseHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version ) {
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(CREATING_POKEMON_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //ToDo Add upgrade logic
    }
}
