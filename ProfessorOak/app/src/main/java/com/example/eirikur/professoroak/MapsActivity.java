package com.example.eirikur.professoroak;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String p0 = "s8f9jwewe89fhalifnln39";
    String p1 = "fadah89dhadiulabsayub73";
    String p2 = "fj9sfoina9briu420";
    String apiUrl = "https://locations.lehmann.tech/pokemon/";
    String locations = "https://locations.lehmann.tech/locations";
    private GoogleMap mMap;
    private NfcAdapter nfcAdapter;
    private ArrayList<Pokemon> listAvailible = new ArrayList<>();
    private ArrayList<Pokemon> listCaptured = new ArrayList<>();
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

        initNFC();
    }

    void initNFC(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC availible", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please turn on NFC", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this, "NFC intent received", Toast.LENGTH_SHORT).show();

            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(parcelables != null && parcelables.length > 0){
                readTextFromMessage((NdefMessage)parcelables[0]);
            } else {
                Toast.makeText(this, "No NDEF message found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onResume(){
        enableForegroundDispatchSystem();
        super.onResume();
    }

    @Override
    protected void onPause(){
        disableForegroundDispatchSystem();
        super.onPause();
    }

    private void readTextFromMessage(NdefMessage ndefMessage){
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length > 0){
            NdefRecord ndefRecord = ndefRecords[0];
            String tagcontent = getTextFromNdefRecord(ndefRecord);
            Toast.makeText(this, tagcontent, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No NDEF records found", Toast.LENGTH_SHORT).show();
        }
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord){
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e){
            Log.e("GetTextFromNdefRecord", e.getMessage(), e);
        }

        return tagContent;
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this, MapsActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void availiblePokemonListClick(View view){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, PokemonListActivity.class);
        intent.putExtra("pokemonList", listAvailible);
        spinner.setVisibility(View.GONE);
        startActivity(intent);
    }

    public void MyPokemonClickList(View view){
        spinner.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, MyPokemonsActivity.class);
        intent.putExtra("pokemonList", listCaptured);
        spinner.setVisibility(View.GONE);
        startActivity(intent);
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
        fetchMyPokemons(p0);
        fetchMyPokemons(p1);
        fetchMyPokemons(p2);
        spinner.setVisibility(View.GONE);
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

                Pokemon pokemon = new Pokemon(itemId, itemName, itemLat, itemLng);
                listAvailible.add(pokemon);

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

    void fetchMyPokemons(final String api) {
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
            Pokemon pokemon = new Pokemon(json.getString("id"), json.getString("name"));
            listCaptured.add(pokemon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
