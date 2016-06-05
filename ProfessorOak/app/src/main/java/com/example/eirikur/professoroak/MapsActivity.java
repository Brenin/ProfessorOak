package com.example.eirikur.professoroak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    URLStrings urlStrings = new URLStrings();
    SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(this);

    public static HashMap<String, Marker> markers = new HashMap<>();
    ArrayList<String> tmpList = new ArrayList<>();
    public static ArrayList<Person> highScore = new ArrayList<>();
    protected static ArrayList<Pokemon> listAvailable = new ArrayList<>();
    protected static ArrayList<Pokemon> listCaptured = new ArrayList<>();
    private ProgressBar spinner;
    ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng oslo = new LatLng(59.9139, 10.7522);
        moveCamera(oslo);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        initStuff();
        spinner.setVisibility(View.GONE);
    }

    void initStuff(){

        Button myPokemonbtn = (Button) findViewById(R.id.MyPokemons);
        myPokemonbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                tmpList.clear();
                listCaptured.clear();
                fillPokemonArray();
                alertPopup();
            }
        });

        final Button highScorebtn = (Button) findViewById(R.id.highScoreButton);
        highScorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highScore.clear();
                getHighscore(urlStrings.getHighScoreUrl());
                whereTo(HighScoreActivity.class);
            }
        });

        Button scanbtn = (Button) findViewById(R.id.scanButton);
        scanbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                whereTo(ScanActivity.class);
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                moveCamera(point);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                moveCamera(point);
            }
        });

        listView = new ListView(this);
        captureFreePokemon();
        getAndDisplayPokemons(urlStrings.getLocationsUrl());
    }

    private void fillPokemonArray() {
        if(listView.getParent() != null){
            ((ViewGroup)listView.getParent()).removeView(listView);
        }

        if(listCaptured.isEmpty()){
            openSQL();
            listCaptured = sqLiteAdapter.getAllObjects();
            closeSQL();
        }

        for(int i = 0; i < listCaptured.size(); i++){
            tmpList.add(listCaptured.get(i).toString());
        }

        tmpList.toArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.pokemons, R.id.pokemonText, tmpList);
        listView.setAdapter(adapter);
    }

    private void alertPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setCancelable(true);
        builder.setView(listView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void openSQL(){
        sqLiteAdapter.open();
    }

    void closeSQL(){
        sqLiteAdapter.close();
    }

    void captureFreePokemon(){
        if(listCaptured.isEmpty()){
            fetchMyFreePokemons(urlStrings.getCapture() + "s8f9jwewe89fhalifnln39");
            fetchMyFreePokemons(urlStrings.getCapture() + "fadah89dhadiulabsayub73");
            fetchMyFreePokemons(urlStrings.getCapture() + "fj9sfoina9briu420");
        }
    }

    void whereTo(Class target){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(MapsActivity.this, target);
        spinner.setVisibility(View.GONE);
        startActivity(intent);
    }

    void writeToDB(Pokemon pokemon){
        SQLiteAdapter adapter = new SQLiteAdapter(this);
        adapter.open();
        adapter.create(pokemon);
        adapter.close();
    }

    void moveCamera(LatLng point){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
    }

    void getAndDisplayPokemons(final String url) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... params) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

                    try {
                        InputStream inputStream = connection.getInputStream();
                        Scanner scanner = new Scanner(inputStream);

                        StringBuilder sb = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            sb.append(scanner.nextLine());
                        }

                        return sb.toString();
                    } catch (FileNotFoundException e) {
                        return "GOT ERROR WITH CODE: " + connection.getResponseCode() + " with message: " + connection.getResponseMessage();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void onPostExecute(final String response) {
                super.onPostExecute(response);
                parseLocations(response);
            }
        }.execute();
    }

    void parseLocations(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject item = (JSONObject) jsonArray.get(i);
                String itemId = item.getString("_id");
                String itemName = item.getString("name");
                String itemLat = item.getString("lat");
                String itemLng = item.getString("lng");

                Pokemon pokemon = new Pokemon(itemId, itemName, itemLat, itemLng);
                listAvailable.add(pokemon);
                putMarker(itemLat, itemLng, itemName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void putMarker(String itemLat, String itemLng, String itemName){
        Float lat = Float.parseFloat(itemLat);
        Float lng = Float.parseFloat(itemLng);
        LatLng marker = new LatLng(lat, lng);

        Marker mo = mMap.addMarker(new MarkerOptions()
                .position(marker)
                .title(itemName + " needs to be captured"));
        markers.put(itemName, mo);
    }

    void fetchMyFreePokemons(final String api) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... params) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(api).openConnection();
                    connection.setRequestProperty("X-Token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.IlByb2Zlc3Nvck9hayI.ZX5O-gmxK-ctYGtOoZnrEw1Dg0joIQ9-GGz0ycA8fNA");

                    try {
                        InputStream inputStream = connection.getInputStream();
                        StringBuilder sb = new StringBuilder();

                        Scanner scanner = new Scanner(inputStream);

                        while (scanner.hasNextLine()) {
                            sb.append(scanner.nextLine());
                        }

                        return sb.toString();
                    } catch (Exception e) {
                        return "GOT ERROR WITH CODE: " + connection.getResponseCode() + " with message: " + connection.getResponseMessage();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void onPostExecute(final String response) {
                super.onPostExecute(response);
                parseData(response);
            }
        }.execute();
    }

    void parseData(String response){
        try {
            JSONObject json = new JSONObject(response);
            Pokemon pokemon = new Pokemon(json.getString("_id"),
                    json.getString("name"),
                    "",
                    "");

            if(listCaptured.contains(pokemon.getName())){
                // Do nothing
            } else{
                ScanActivity.checkList.add(pokemon.getName());
                writeToDB(pokemon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getHighscore(final String url) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... params) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

                    try {
                        InputStream inputStream = connection.getInputStream();
                        Scanner scanner = new Scanner(inputStream);

                        StringBuilder sb = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            sb.append(scanner.nextLine());
                        }

                        return sb.toString();
                    } catch (FileNotFoundException e) {
                        return "GOT ERROR WITH CODE: " + connection.getResponseCode() + " with message: " + connection.getResponseMessage();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void onPostExecute(final String response) {
                super.onPostExecute(response);
                parseFromHighscore(response);
            }
        }.execute();
    }

    void parseFromHighscore(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject item = (JSONObject) jsonArray.get(i);
                String itemName = item.getString("username");
                int itemScore = item.getInt("score");

                highScore.add(new Person(itemName, itemScore));
            }

            Collections.sort(highScore, new Comparator<Person>(){
                public int compare(Person m1, Person m2) {
                    if (m1.getScore() == m2.getScore()) {
                        return 0;
                    } else if (m1.getScore() > m2.getScore()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
