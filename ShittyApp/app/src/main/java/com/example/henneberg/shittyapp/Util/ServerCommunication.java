package com.example.henneberg.shittyapp.Util;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ander on 22-Nov-15.
 */
public class ServerCommunication {

    TextView debugtv;
    private String serverURL;

    public ServerCommunication(String IP, TextView debugtv) {
        serverURL = "http://"+IP+":8080";
        this.debugtv = debugtv;

    }

    public void attemptConnection() {
        try {

            String result = "";

            debug("Connecting to... "+serverURL);
            URL fullURL = new URL(serverURL);
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
            //debug("Malformed: "+e);
        } catch (IOException e) {
            e.printStackTrace();
            debug("IOException: "+e);
        }

        debug("------------------------------------------------");
    }

    private void debug(String txt) {
        debugtv.append(txt + "\n");
    }

}
