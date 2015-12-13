package com.example.henneberg.shittyapp.Util;

import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Henneberg on 13-12-2015.
 */
public class FakeServerCommunication implements ServerCommunication {

    TextView debugtv;

    public FakeServerCommunication(TextView debug) {
        this.debugtv = debug;
    }

    @Override
    public void sendPost(JSONObject obj, String subpath) {
        String data = obj.toString();
        debug("(FAKE) connecting to a server");
        debug("With parameters: " + data);
        debug("------- CONNECTION DONE");
    }

    private void debug(String txt) {
        if(debugtv != null) {
            debugtv.append(txt + "\n");
        }
        System.out.println(txt);
    }

    @Override
    public String getName() {
        return "Fake Server Comm.";
    }
}
