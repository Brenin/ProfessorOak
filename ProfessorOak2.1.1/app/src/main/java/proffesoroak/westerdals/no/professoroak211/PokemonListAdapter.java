package proffesoroak.westerdals.no.professoroak211;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruker on 19.05.2016.
 */
public class PokemonListAdapter extends BaseAdapter {
    private List<Pokemon> pokemons = new ArrayList<Pokemon>();

    private Context context;

    private SQLiteAdapter sqLiteAdapter;

    public PokemonListAdapter(Context context) {
        this.context = context;

        sqLiteAdapter = ListActivity.sqLiteAdapter;
        sqLiteAdapter.open();

        Cursor cursor = sqLiteAdapter.readAll();

        cursor.moveToFirst();

        do {
            Pokemon pokemon = new Pokemon();
            pokemon.setId(cursor.getLong(cursor.getColumnIndex(POKEMON_ID)));
            pokemon.setName(cursor.getString(cursor.getColumnIndex(POKEMON_NAME)));
            pokemon.setLng(cursor.getInt(cursor.getColumnIndex(POKEMON_LNG)));
            pokemon.setLat(cursor.getInt(cursor.getColumnIndex(POKEMON_LAT)));
            pokemons.add(pokemon);
        }
        while (cursor.moveToNext());

        sqLiteAdapter.close();

    @Override
    public int getCount() { return pokemons.size(); }

    @Override
    public Object getItem(int position) { return pokemons.get(position)}

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Pokemon pokemon;

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false);
                holder = new ViewHolder();
                holder.textViewId = (TextView) convertView.findViewById(R.id.textViewId);
                holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);

                convertView.setTag(holder);
                pokemon = getItem(position);

                holder.textViewId.setText(person.getId().toString());
                holder.textViewName.setText(person.getName());
                holder.textViewAge.setText(person.getAge().toString());

            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

       /* if ( position % 2 == 0 ){
            convertView.setBackgroundColor(parent.getResources().getColor(android.R.color.holo_green_light));
        }
        else {
            convertView.setBackgroundColor(parent.getResources().getColor(android.R.color.background_light));
        }*/

            return convertView;
        }

        static class ViewHolder {
            TextView textViewId;
            TextView textViewName;
        }
}
