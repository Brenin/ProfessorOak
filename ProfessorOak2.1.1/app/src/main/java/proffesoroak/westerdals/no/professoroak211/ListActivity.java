package proffesoroak.westerdals.no.professoroak211;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ListActivity extends AppCompatActivity {
    String apiUrl = "http://10.0.3:2389";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //this.responseListView = (ListView) findViewById(R.id.listView);
        getAndDisplayData();
    }

    void getAndDisplayData() {
        new AsyncTask<Void, Void, String> {
            protected String doInBackground(final Void params) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();;
                    connection.setRequestProperty("X-Token", "give me access plz");
                    try {
                        InputStream stream = connection.getInputStream();
                        Scanner scanner = new Scanner(stream);
                        StringBuilder sb = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            sb.append(scanner.nextLine());
                        }

                    } catch (FileNotFoundException e) {
                        return "GOT ERROR WITH CODE: " + connection.getResponseCode();

                    }
                }catch (IOException f) {
                    f.printStackTrace();
                }




            }
        }
}
