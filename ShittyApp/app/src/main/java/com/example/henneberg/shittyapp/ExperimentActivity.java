package com.example.henneberg.shittyapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henneberg.shittyapp.Util.AppConstants;
import com.example.henneberg.shittyapp.Util.ServerCommunication;

import org.json.JSONObject;

public class ExperimentActivity extends AppCompatActivity {

    private static long MAX_TIME_BEFORE_EXIT = 600000;
    private boolean paused = false;

    long lastSeenRound, lastSeenEver;

    BluetoothAdapter blAdapter;

    private int scans = 0;
    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Short RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getAddress().equalsIgnoreCase(AppConstants.BRACELET_ADDRESS)) {
                    deviceSeen(RSSI);
                }


            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                tvBluetooth.setText("SCANNING ("+scans+")...");
                scans++;
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if(paused) {
                    debug("#### Bluetooth scans stopping.");
                } else {
                    scans = 0;
                    blAdapter.startDiscovery();
                }
            }

            if ((System.currentTimeMillis() - lastSeenRound) > AppConstants.getMaxTime()) {
                reachedMaxTime();
            }
        }
    };

    TextView tvBluetooth, tvOutput, tvServer;
    Button btRestart;
    Switch swEServer;

    ServerCommunication serverComm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        tvBluetooth = (TextView) findViewById(R.id.tvEBluetooth);
        tvOutput = (TextView) findViewById(R.id.tvEOutput);
        tvServer = (TextView) findViewById(R.id.tvEServer);

        swEServer = (Switch) findViewById(R.id.swEServer);
        swEServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDevMode(isChecked);
            }
        });

        btRestart = (Button) findViewById(R.id.btERestart);
        btRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScans();
            }
        });

        serverComm = new ServerCommunication(AppConstants.getServerAddress(), tvServer);

        setupBluetoothAdapter();
        registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        startScans();
    }

    private void startScans() {
        btRestart.setEnabled(false);

        paused = false;
        lastSeenRound = System.currentTimeMillis();
        lastSeenEver = lastSeenRound;

        blAdapter.startDiscovery();
    }


    private void deviceSeen(Short RSSI) {
        debug("---- Device found, RSSI = " + RSSI);
        lastSeenRound = System.currentTimeMillis();
        lastSeenEver = lastSeenRound;

        sendMeasurement(RSSI);
    }

    private void reachedMaxTime() {
        if(paused)
            return;

        debug("//// Device NOT found, RSSI = -110 (default)");
        lastSeenRound = System.currentTimeMillis();

        sendMeasurement((short) -110);
        if((System.currentTimeMillis() - lastSeenEver) > MAX_TIME_BEFORE_EXIT) {
            stopScans();
        }
    }

    private void stopScans() {
        debug("Device NOT seen in "+MAX_TIME_BEFORE_EXIT+"ms. Stopping scans.");
        paused = true;

        tvBluetooth.setText("Paused");
        btRestart.setText("Restart scans");
        btRestart.setEnabled(true);

    }




    private void sendMeasurement(Short RSSI) {
        long timestamp = System.currentTimeMillis();

        JSONObject obj = AppConstants.createJSON_Experiment(timestamp, RSSI);

        serverComm.sendPost(obj, AppConstants.EXPERIMENT_PATH);
    }

    private void setDevMode(boolean isChecked) {
        int setTo;
        if(isChecked)
            setTo = View.VISIBLE;
        else
            setTo = View.INVISIBLE;

        tvServer.setVisibility(setTo);
    }

    private void debug(String msg) {
        tvOutput.append("\n" + msg);
    }




    private void setupBluetoothAdapter() {
        blAdapter = BluetoothAdapter.getDefaultAdapter();

        if(blAdapter == null) {
            (Toast.makeText(this, "No Bluetooth Adapter", Toast.LENGTH_SHORT)).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            if(!blAdapter.isEnabled()) {
                (Toast.makeText(getApplicationContext(), "Enabling BLAdapter", Toast.LENGTH_SHORT )).show();
                blAdapter.enable();
            }
        }
    }
}
