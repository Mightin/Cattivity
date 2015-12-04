package com.example.henneberg.shittyapp.Util;

import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by ander on 22-Nov-15.
 */
public class ServerCommunication {

    TextView debugtv;
    private final String serverURL;

    public ServerCommunication(String IP, TextView debugtv) {
        serverURL = "http://"+IP+"/";
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

    public void sendPost(String[] parameterNames, String[] parameterValues) {
        try {
            String data = "";
            if (parameterNames.length != parameterValues.length) {
                System.out.println("Troubles in ServerComm (Parameter lengths do not match)");
                System.exit(-1);
            }

            for (int i = 0; i < parameterNames.length; i++) {
                data = data + parameterNames[i] + "=" + parameterValues[i] + "&";
            }
            data = data.substring(0, data.length() - 1); // Cut away the last &.
            String encodedParams = URLEncoder.encode(data, "UTF-8");

            URL u = new URL(serverURL+"/fingerprint");
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            debug("Connecting to: " + u.toString());
            debug("With parameters: " + data);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(encodedParams.length()));

            debug("Writing encoded params to server with POST");
            OutputStream os = conn.getOutputStream();
            os.write(encodedParams.getBytes());
            os.flush();
            os.close();

            debug("Preparing to read response from " + conn.getURL().toString());
            BufferedReader response = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            String s = "";
            String result = "";
            while((s = response.readLine()) != null) {
                result += s + System.getProperty("line.separator");
            }
            debug(result);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendPost(JSONObject obj) {
        try {
            String encodedParams = URLEncoder.encode(obj.toString(), "UTF-8");

            URL u = new URL(serverURL+"/fingerprint");
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            debug("Connecting to: " + u.toString());
            debug("With parameters: " + encodedParams);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(encodedParams.length()));

            debug("Writing encoded params to server with POST");
            OutputStream os = conn.getOutputStream();
            os.write(encodedParams.getBytes());
            os.flush();
            os.close();

            debug("Preparing to read response from " + conn.getURL().toString());
            BufferedReader response = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            String s = "";
            String result = "";
            while((s = response.readLine()) != null) {
                result += s + System.getProperty("line.separator");
            }
            debug(result);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void debug(String txt) {
        if(debugtv != null) {
            debugtv.append(txt + "\n");
        }
    }

}
