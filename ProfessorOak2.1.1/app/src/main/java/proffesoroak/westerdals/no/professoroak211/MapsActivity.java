package proffesoroak.westerdals.no.professoroak211;

import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map; //Nytt felt
    String apiUrl = "http://10.0.3.2:2389/secret";
    private TextView responseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Kaller super onCreate for åha mulighet til å spare
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //Er bare et fragment - rett og slett bare et map
        //Jeg vil ha dette fragmentet som har en app
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //Vil ha en referanse til kartet
    }

    void getAndDisplayData() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... params) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                    connection.setRequestProperty("X-Token", "give me access plz");

                    try {
                        InputStream inputStream = connection.getInputStream();
                        Scanner scanner = new Scanner(inputStream);

                        StringBuilder stringBuilder = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            stringBuilder.append(scanner.nextLine());
                        }

                        return stringBuilder.toString();
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
                responseTextView.setText(response);
            }
        }.execute();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney")); //Kan sende inn LatLng
        //Kan sette farger på markeren +++

        if (ActivityCompat.checkSelfPermission(this, )) {
            return;
        }
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //Flytter kamera med kamera update (factory) til å flytet kamera til nytt LatLng
        //Kan sette zoom, hvordan den skal beege seg dit osv.
    }
}
