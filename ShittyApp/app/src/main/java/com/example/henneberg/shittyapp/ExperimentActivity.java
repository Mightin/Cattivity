package com.example.henneberg.shittyapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henneberg.shittyapp.Util.AppConstants;
import com.example.henneberg.shittyapp.Util.FakeServerCommunication;
import com.example.henneberg.shittyapp.Util.ServerCommunication;
import com.example.henneberg.shittyapp.Util.ServerCommunicationImpl;

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
                    blAdapter.startDiscovery();
                }
            }

            if ((System.currentTimeMillis() - lastSeenRound) > AppConstants.getMaxTime()) {
                reachedMaxTime();
            }
        }
    };



    TextView tvBluetooth, tvOutput, tvServer;
    EditText edPhoneNumber, edRunNumber;
    Button btRestart;
    Spinner spServerComm;

    private ServerCommunication scImpl;
    private ServerCommunication scLocalhost;
    private ServerCommunication scFake;
    private String[] serverComms;

    ServerCommunication serverComm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        paused = true;

        edPhoneNumber = (EditText) findViewById(R.id.edEPhoneNumber);
        edRunNumber = (EditText) findViewById(R.id.edERunNumber);

        edPhoneNumber.setText(AppConstants.getPhoneName());
        edRunNumber.setText(""+AppConstants.getExperimentRun());

        tvBluetooth = (TextView) findViewById(R.id.tvEBluetooth);
        tvOutput = (TextView) findViewById(R.id.tvEOutput);
        tvServer = (TextView) findViewById(R.id.tvEServer);

        tvOutput.setMovementMethod(new ScrollingMovementMethod());
        tvServer.setMovementMethod(new ScrollingMovementMethod());

        btRestart = (Button) findViewById(R.id.btERestart);
        btRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScans();
            }
        });

        scImpl = new ServerCommunicationImpl(AppConstants.getServerAddress(), tvServer);
        scLocalhost = new ServerCommunicationImpl(AppConstants.getServerLocalAddress(), tvServer);
        scFake = new FakeServerCommunication(tvServer);
        serverComms = new String[]{"Real Server", "Localhost Server", "Fake Server"};

        spServerComm = (Spinner) findViewById(R.id.spEServComm);
        spServerComm.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, serverComms));
        spServerComm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setServerComm(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setupBluetoothAdapter();
        registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void startScans() {
        btRestart.setEnabled(false);
        spServerComm.setEnabled(false);

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
            debug("#### Device NOT seen in "+MAX_TIME_BEFORE_EXIT+"ms. Stopping scans.");
            stopScans();
        }
    }

    private void stopScans() {
        paused = true;

        tvBluetooth.setText("Paused");
        btRestart.setText("Restart scans");
        btRestart.setEnabled(true);
    }




    private void sendMeasurement(Short RSSI) {
        scans = 0;
        long timestamp = System.currentTimeMillis();

        JSONObject obj = AppConstants.createJSON_Experiment(timestamp, RSSI);

        serverComm.sendPost(obj, AppConstants.EXPERIMENT_PATH);
    }




    private void setServerComm(int position) {
        switch(position) {
            case 0:
                serverComm = scImpl;
                break;
            case 1:
                serverComm = scLocalhost;
                break;
            case 2:
                serverComm = scFake;
                break;
            default:
                serverComm = scFake;
                break;
        }

        debug("Setting ServComm to: "+serverComms[position]);
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
