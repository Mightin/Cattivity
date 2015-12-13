package com.example.henneberg.shittyapp.Util;

import org.json.JSONObject;

/**
 * Created by Henneberg on 13-12-2015.
 */
public interface ServerCommunication {

    void sendPost(JSONObject obj, String subpath);
}
