package com.example.henneberg.shittyapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.henneberg.shittyapp.Util.AppConstants;

public class MainActivity extends AppCompatActivity {

    Button bluetoothButton;
    Button serverCommButton;
    Button fingerprintOfflineButton;
    Button btBaseline;

    Button btExp;

    Button butSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AppConstants.setContext(getApplicationContext());

        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bluetoothScreen = new Intent(getApplicationContext(), BluetoothDevicesActivity.class);
                startActivity(bluetoothScreen);
            }
        });

        serverCommButton = (Button) findViewById(R.id.serverButton);
        serverCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverCommScreen = new Intent(getApplicationContext(), ServerConnectActivity.class);
                startActivity(serverCommScreen);
            }
        });

        fingerprintOfflineButton = (Button) findViewById(R.id.fingerprintOfflineButton);
        fingerprintOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fingerprintOffline = new Intent(getApplicationContext(), FingerprintOfflineActivity.class);
                startActivity(fingerprintOffline);
            }
        });

        btBaseline = (Button) findViewById(R.id.btBaseline);
        btBaseline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent baseline = new Intent(getApplicationContext(), BaselineActivity.class);
                startActivity(baseline);
            }
        });

        btExp = (Button) findViewById(R.id.btMExp);
        btExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exp = new Intent(getApplicationContext(), ExperimentActivity.class);
                startActivity(exp);
            }
        });

        butSettings = (Button) findViewById(R.id.butSettings);
        butSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
