package com.example.eirikur.professoroak;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    String apiUrl = "http://10.0.3.2:2389/secret";
    String locations = "https://locations.lehmann.tech/locations";
    private GoogleMap mMap;

    private ArrayList<Pokemon> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button pokemonButton = (Button) findViewById(R.id.PokemonList);
        Button scanButton = (Button) findViewById(R.id.scanButton);
    }

    public void PokemonListClick(View view){
        Intent intent = new Intent(this, PokemonListActivity.class);
        intent.putExtra("pokemonList", list);
        startActivity(intent);
    }

    public void MyPokemonsClick(View view){
        Intent intent = new Intent(this, MyPokemonsActivity.class);
        startActivity(intent);
    }

    public void ScannerClick(View view){
        Intent intent = new Intent(
                "com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT"); // This will contain your scan result
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                celebrate();
            }
        }
    }

    void celebrate(){
        String message = "Woopi!";
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast.makeText(context, message, duration).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Oslo and move the camera
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

        initListeners();
        getAndDisplayData();
    }

    public void initListeners() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Hello Professor Oak"));
                moveCamera(point);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Hello Professor Oak"));
                moveCamera(point);
            }
        });
    }

    public void moveCamera(LatLng point){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
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

                Pokemon pokemon = new Pokemon(itemId, itemName, itemLat, itemLng);
                list.add(pokemon);

                Float lat = Float.parseFloat(itemLat);
                Float lng = Float.parseFloat(itemLng);
                LatLng marker = new LatLng(lat, lng);

                mMap.addMarker(new MarkerOptions()
                        .position(marker)
                        .title(itemName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
