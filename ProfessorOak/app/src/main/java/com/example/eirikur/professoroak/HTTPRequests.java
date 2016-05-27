package com.example.eirikur.professoroak;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    //private String url = "https://locations.lehmann.tech/scores";
    private String tmpUrl;
    private ArrayList<Person> workList = new ArrayList<>();

    public HTTPRequests(String url){
        this.tmpUrl = url;

        getAndDisplayData(tmpUrl);
    }

    public ArrayList<Person> getWorkList(){
        return workList;
    }

    void getAndDisplayData(final String url) {
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
                String itemName = item.getString("username");
                int itemScore = item.getInt("score");

                workList.add(new Person(itemName, itemScore));
            }

            Collections.sort(workList, new Comparator<Person>(){
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
