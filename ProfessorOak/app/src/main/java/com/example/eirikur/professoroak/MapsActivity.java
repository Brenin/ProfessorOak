package com.example.eirikur.professoroak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String p0 = "s8f9jwewe89fhalifnln39";
    String p1 = "fadah89dhadiulabsayub73";
    String p2 = "fj9sfoina9briu420";
    String apiUrl = "https://locations.lehmann.tech/pokemon/";
    String locations = "https://locations.lehmann.tech/locations";

    HTTPRequests httpRequests;
    public static GoogleMap mMap;
    private Button myPokemonbtn;
    private Button availiblePokemonbtn;
    private Button scanbtn;

    protected static ArrayList<Pokemon> listAvailible = new ArrayList<>();
    protected static ArrayList<Pokemon> listCaptured = new ArrayList<>();
    protected static ArrayList<String> checkList = new ArrayList<>();
    private ProgressBar spinner;

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

    void initStuff(){
        myPokemonbtn = (Button) findViewById(R.id.MyPokemons);
        myPokemonbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                whereTo(MyPokemonsActivity.class, listCaptured);
            }
        });

        availiblePokemonbtn = (Button) findViewById(R.id.PokemonList);
        availiblePokemonbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                whereTo(PokemonListActivity.class, listAvailible);
            }
        });

        scanbtn = (Button) findViewById(R.id.scanButton);
        scanbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                whereTo(ScanActivity.class, null);
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
    }

    private void whereTo(Class target, ArrayList<Pokemon> payload){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(MapsActivity.this, target);
        if(payload != null){
            intent.putExtra("pokemonList", payload);
        }
        spinner.setVisibility(View.GONE);
        startActivity(intent);
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

        fetchMyFreePokemons(p0);
        fetchMyFreePokemons(p1);
        fetchMyFreePokemons(p2);
        getAndDisplayData();
        spinner.setVisibility(View.GONE);
    }

    public void moveCamera(LatLng point){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
    }

    void getAndDisplayData() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... params) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(locations).openConnection();

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
                parseData(response);
            }
        }.execute();
    }

    void parseData(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject item = (JSONObject) jsonArray.get(i);
                String itemId = item.getString("_id");
                String itemName = item.getString("name");
                String itemLat = item.getString("lat");
                String itemLng = item.getString("lng");

                Pokemon pokemon = new Pokemon(itemId, itemName);

                if(checkList.contains(itemName)){
                    putMarker(itemLat, itemLng, itemName, "1");
                } else {
                    listAvailible.add(pokemon);
                    putMarker(itemLat, itemLng, itemName, "2");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void putMarker(String itemLat, String itemLng, String itemName, String hax){
        Float lat = Float.parseFloat(itemLat);
        Float lng = Float.parseFloat(itemLng);
        LatLng marker = new LatLng(lat, lng);

        if(hax == "1"){
            mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(itemName + " is already captured")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        } else {
            mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(itemName + " needs to be captured"));
        }
    }

    void fetchMyFreePokemons(final String api) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... params) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl + api).openConnection();
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
                parseData2(response);
            }
        }.execute();
    }

    void parseData2(String response){
        try {
            JSONObject json = new JSONObject(response);
            Pokemon pokemon = new Pokemon(json.getString("_id"), json.getString("name"));
            checkList.add(json.getString("name"));
            listCaptured.add(pokemon);
            SQLiteAdapter adapter = new SQLiteAdapter(this);
            adapter.open();
            adapter.create(pokemon);
            adapter.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
