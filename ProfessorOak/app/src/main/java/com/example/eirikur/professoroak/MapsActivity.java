package com.example.eirikur.professoroak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String apiUrl = "https://locations.lehmann.tech/pokemon/";
    String locations = "https://locations.lehmann.tech/locations";

    public static GoogleMap mMap;

    protected static ArrayList<Pokemon> listAvailable = new ArrayList<>();
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

    @Override
    protected void onResume(){
        super.onResume();
        listAvailable.clear();
        checkList.clear();
        checkPokemon();
    }

    void initStuff(){
        Button myPokemonbtn = (Button) findViewById(R.id.MyPokemons);
        myPokemonbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                whereTo(MyPokemonActivity.class, null);
            }
        });

        Button availiblePokemonbtn = (Button) findViewById(R.id.PokemonList);
        availiblePokemonbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                whereTo(PokemonListActivity.class, listAvailable);
            }
        });

        Button scanbtn = (Button) findViewById(R.id.scanButton);
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

        checkPokemon();
    }

    void checkPokemon(){
        if(checkList.isEmpty()){
            SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(this);
            sqLiteAdapter.open();
            sqLiteAdapter.readAll();

            HTTPRequests httpRequests = new HTTPRequests();

            httpRequests.fetchMyFreePokemons(apiUrl + "s8f9jwewe89fhalifnln39");
            httpRequests.fetchMyFreePokemons(apiUrl + "fadah89dhadiulabsayub73");
            httpRequests.fetchMyFreePokemons(apiUrl + "fj9sfoina9briu420");
            sqLiteAdapter.close();
        }

        HTTPRequests httpRequests = new HTTPRequests();
        httpRequests.getAndDisplayPokemons(locations);
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

    void parseDataFromMyPokemons(String response){
        try {
            JSONObject json = new JSONObject(response);
            Pokemon pokemon = new Pokemon(json.getString("_id"), json.getString("name"));
            MapsActivity.checkList.add(json.getString("name"));

            if(MapsActivity.checkList.contains(pokemon.getName())){

            } else{
                SQLiteAdapter adapter = new SQLiteAdapter(this);
                adapter.open();
                adapter.create(pokemon);
                adapter.close();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void moveCamera(LatLng point){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
    }
}
