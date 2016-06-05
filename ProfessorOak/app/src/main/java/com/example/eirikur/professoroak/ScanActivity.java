package com.example.eirikur.professoroak;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ScanActivity extends AppCompatActivity {

    private EditText editText;
    private NfcAdapter nfcAdapter;
    private Button captureBtn;

    String pokemonID = null;
    URLStrings urlStrings = new URLStrings();
    public static ArrayList<String> checkList = new ArrayList<>();
    HashMap<String, LatLng> capturedList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        captureBtn = (Button) findViewById(R.id.captureButton);
        editText = (EditText) findViewById(R.id.captureCode);

        for(Pokemon pokemon : MapsActivity.listAvailable){
            Float lat = Float.parseFloat(pokemon.getLat());
            Float lng = Float.parseFloat(pokemon.getLng());
            LatLng marker = new LatLng(lat, lng);
            capturedList.put(pokemon.toString(), marker);
        }

        initListeners();
        initNFC();
    }

    public void initListeners() {
        editText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        captureBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pokemonID = editText.getText().toString();
                if (pokemonID.isEmpty()){
                    highFive("Invalid or empty String");
                } else {
                    captureMyPokemon(urlStrings.getCapture() + pokemonID);
                }
            }
        });
    }

    void captureMyPokemon(final String api) {
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
                parseCapture(response);
            }
        }.execute();
    }

    public void parseCapture(String response){
        try {
            JSONObject json = new JSONObject(response);
            Pokemon pokemon = new Pokemon(json.getString("_id"),
                    json.getString("name"),
                    "",
                    "");

            if(checkList.contains(pokemon.toString())){
                highFive("You have already captured " + pokemon.getName());
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
            } else {
                checkList.add(pokemon.toString());
                logPokemon(pokemon);
                highFive(pokemon.toString() + " captured");

                highFive("2");
                Marker marker = MapsActivity.markers.get(pokemon.toString());
                MapsActivity.mMap.addMarker(new MarkerOptions()
                        .position(marker.getPosition())
                        .title(pokemon.toString() + " is captured")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                marker.remove();
                highFive("3");
            }

            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);

        } catch (JSONException e) {
            highFive("Invalid ID\n" + response);
            e.printStackTrace();
        }
    }

    public void highFive(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    void logPokemon(Pokemon pokemon){
        SQLiteAdapter adapter = new SQLiteAdapter(this);
        adapter.open();
        adapter.create(pokemon);
        adapter.close();
    }

    void initNFC(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC availible", Toast.LENGTH_SHORT).show();
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
                String error = "No NDEF message found";
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                editText.setText(error);
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
            String tagContent = getTextFromNdefRecord(ndefRecord);
            editText.setText(tagContent.trim());
        } else {
            String error = "No NDEF records found";
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            editText.setText(error);
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
        Intent intent = new Intent(this, ScanActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }
}
