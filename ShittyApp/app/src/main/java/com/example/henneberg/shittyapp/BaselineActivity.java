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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henneberg.shittyapp.Util.AppConstants;
import com.example.henneberg.shittyapp.Util.ServerCommunicationImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BaselineActivity extends AppCompatActivity {

    private long MAXWAIT = 90000;

    private ArrayList<Short> measurements;
    private int goalMeasures;

    EditText braceletX;
    EditText braceletY;
    EditText phoneName;
    EditText noOfMeasurements;
    EditText noFingRun;
    EditText setupUsed;
    Button scanButton;

    TextView signalStrength;
    Button sendData;

    TextView tvServerResponse;

    long lastSeen;

    BluetoothAdapter blAdapter;

    private int scans = 0;
    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                Short RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getAddress().equalsIgnoreCase(AppConstants.BRACELET_ADDRESS)) {
                    addMeasurement(RSSI);
                }


            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                scans++;
                scanButton.setText("SCANNING ("+scans+")... "+measurements.size());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if(measurements.size() == goalMeasures) {
                    Toast.makeText(getApplicationContext(), "Scan finished", Toast.LENGTH_SHORT).show();
                    scanButton.setText("DONE");
                } else {
                    blAdapter.startDiscovery();
                }
            }

            if ((System.currentTimeMillis() - lastSeen) > MAXWAIT) {
                notifySlow();
                for(int i = measurements.size(); i < goalMeasures-1; i++) {
                    measurements.add((short) -110);
                }
                addMeasurement((short) -110);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseline);

        measurements = new ArrayList<Short>();

        braceletX = (EditText) findViewById(R.id.BxCoord);
        braceletY = (EditText) findViewById(R.id.ByCoord);
        phoneName = (EditText) findViewById(R.id.BphoneName);
        noOfMeasurements = (EditText) findViewById(R.id.BnoOfMeasurements);
        noFingRun = (EditText) findViewById(R.id.BnoFingRun);
        setupUsed = (EditText) findViewById(R.id.BsetupUsed);

        phoneName.setText(""+AppConstants.getPhoneName());
        noOfMeasurements.setText(""+AppConstants.getNoOfMeasurements());
        noFingRun.setText(""+AppConstants.getFingerprintingRun());
        setupUsed.setText(""+AppConstants.getSetupUsed());


        scanButton = (Button) findViewById(R.id.BscanForBracelet);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalMeasures = Integer.parseInt(noOfMeasurements.getText().toString());
                updateSS();
                lastSeen = System.currentTimeMillis();

                boolean success = blAdapter.startDiscovery();

                if(!success) {
                    (Toast.makeText(getApplicationContext(), "BL Scan did not start", Toast.LENGTH_SHORT )).show();
                }
            }
        });

        signalStrength = (TextView) findViewById(R.id.BsignalStrength);

        sendData = (Button) findViewById(R.id.BsendDataButton);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToServer();
            }
        });

        tvServerResponse = (TextView) findViewById(R.id.BtvServerResponse);

        setupBluetoothAdapter();

        registerReceiver(blReceiver, new IntentFilter());
        registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));


    }

    private void notifySlow() {
        (Toast.makeText(getApplicationContext(), "TOO LONG SINCE SEEN: SET -110", Toast.LENGTH_SHORT)).show();
    }

    private void sendDataToServer() {
        try {
            ServerCommunicationImpl sc = new ServerCommunicationImpl(AppConstants.getServerAddress(), tvServerResponse);
            JSONObject obj = new JSONObject();

            obj.put("values", new JSONArray(measurements));
            obj.put("x", Integer.valueOf(braceletX.getText().toString()));
            obj.put("y", Integer.valueOf(braceletY.getText().toString()));
            obj.put("phoneID", Integer.valueOf(phoneName.getText().toString()));
            obj.put("setupUsed", Integer.valueOf(setupUsed.getText().toString()));
            obj.put("run", Integer.valueOf(noFingRun.getText().toString()));

            sc.sendPost(obj, AppConstants.BASELINE_PATH);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toast.makeText(getApplicationContext(), "MOCK SEND: "+measurements.toString(), Toast.LENGTH_SHORT).show();
    }

    private void addMeasurement(Short RSSI) {
        measurements.add(RSSI);
        lastSeen = System.currentTimeMillis();

        updateSS();

        if(measurements.size() == goalMeasures) {
            blAdapter.cancelDiscovery();
            scanButton.setEnabled(false);
            sendData.setEnabled(true);
        } else {
            //blAdapter.cancelDiscovery();
        }
    }

    private void updateSS() {
        signalStrength.setEnabled(true);
        signalStrength.setText("Strength: "+measurements.toString());
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


    private void bluetoothProcess() {
        updateSS();
        lastSeen = System.currentTimeMillis();

        /*if(blAdapter.isDiscovering()){
            blAdapter.cancelDiscovery();
        }

        boolean success = blAdapter.startDiscovery();

        if(!success) {
            (Toast.makeText(getApplicationContext(), "BL Scan did not start", Toast.LENGTH_SHORT )).show();
        }*/
    }

}
