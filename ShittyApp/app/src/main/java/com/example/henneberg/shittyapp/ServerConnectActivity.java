package com.example.henneberg.shittyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerConnectActivity extends AppCompatActivity {

    private TextView debug;
    private Button connectButton;
    private final String ServerURL = "http://192.168.0.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connect);

        debug = (TextView) findViewById(R.id.textView3);
        connectButton = (Button) findViewById(R.id.connectButton);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptConnection();
            }
        });

    }

    private void attemptConnection() {
        try {

            String result = "";

            debug("Connecting to... "+ServerURL);
            URL fullURL = new URL(ServerURL);
            HttpURLConnection connection = (HttpURLConnection) (fullURL.openConnection());
            debug("Connection opened");
            BufferedReader response = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            debug("Reading response");

            String s = "";
            while((s = response.readLine()) != null) {
                result += s + System.getProperty("line.separator");
            }

            debug(result);

            debug("Read finished");




        } catch (MalformedURLException e) {
            e.printStackTrace();
            debug("Malformed: "+e);
        } catch (IOException e) {
            e.printStackTrace();
            debug("IOException: "+e);
        }

        debug("------------------------------------------------");

    }

    private void debug(String txt) {
        debug.append(txt + "\n");
    }


}
