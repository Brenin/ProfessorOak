package com.example.eirikur.professoroak;

import android.os.AsyncTask;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Created by Eirikur on 27/05/2016.
 */
public class HTTPRequests {

    MapsActivity maps;

    public HTTPRequests(){
    }

    void getAndDisplayHighscore(final String url) {
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
                parseDataFromHighscore(response);
            }
        }.execute();
    }

    void parseDataFromHighscore(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject item = (JSONObject) jsonArray.get(i);
                String itemName = item.getString("username");
                int itemScore = item.getInt("score");

                HighScoreActivity.highScore.add(new Person(itemName, itemScore));
            }

            Collections.sort(HighScoreActivity.highScore, new Comparator<Person>(){
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

                if(MapsActivity.checkList.contains(itemName)){
                    putMarker(itemLat, itemLng, itemName, "1");
                } else {
                    MapsActivity.listAvailable.add(pokemon);
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
            MapsActivity.mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(itemName + " is already captured")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        } else {
            MapsActivity.mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(itemName + " needs to be captured"));
        }
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
                maps = new MapsActivity();
                maps.parseDataFromMyPokemons(response);
            }
        }.execute();
    }

}
