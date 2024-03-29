package com.example.henneberg.shittyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.henneberg.shittyapp.Util.AppConstants;
import com.example.henneberg.shittyapp.Util.ServerCommunicationImpl;

public class ServerConnectActivity extends AppCompatActivity {

    private TextView debug;
    private EditText IP;
    private Button connectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connect);

        debug = (TextView) findViewById(R.id.textView3);
        debug.setMovementMethod(new ScrollingMovementMethod());
        connectButton = (Button) findViewById(R.id.connectButton);
        IP = (EditText) findViewById(R.id.serverAddr);
        IP.setText(AppConstants.getServerAddress());

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerCommunicationImpl sc = new ServerCommunicationImpl(IP.getText().toString(), debug);
                sc.attemptConnection();
            }
        });

    }


    private void debug(String txt) {
        debug.append(txt + "\n");
    }


}
