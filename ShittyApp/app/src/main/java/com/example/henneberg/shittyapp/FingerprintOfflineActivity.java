package com.example.henneberg.shittyapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henneberg.shittyapp.Util.AppConstants;
import com.example.henneberg.shittyapp.Util.ServerCommunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

public class FingerprintOfflineActivity extends AppCompatActivity {

    private long MAXWAIT = 90000;

    private ArrayList<Short> measurements;
    private int goalMeasures;

    EditText braceletLoc;
    EditText phoneName;
    EditText noOfMeasurements;
    EditText noFingRun;
    Button scanButton;

    TextView signalStrength;
    Button sendData;

    TextView tvServerResponse;

    long lastSeen;


    BluetoothAdapter blAdapter;
    int REQUEST_ENABLE_BT = 5;

    private int discoveries = 0;
    private int scans = 0;
    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            discoveries++;




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
                scanButton.setText("SCANNING ("+scans+")...");
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
        setContentView(R.layout.activity_fingerprint_offline);

        measurements = new ArrayList<Short>();

        braceletLoc = (EditText) findViewById(R.id.braceletLocation);
        phoneName = (EditText) findViewById(R.id.phoneName);
        noOfMeasurements = (EditText) findViewById(R.id.noOfMeasurements);
        noFingRun = (EditText) findViewById(R.id.noFingRun);

        phoneName.setText(AppConstants.getPhoneName());
        noFingRun.setText(""+AppConstants.getFingerprintingRun());

        scanButton = (Button) findViewById(R.id.scanForBracelet);
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

        signalStrength = (TextView) findViewById(R.id.signalStrength);

        sendData = (Button) findViewById(R.id.sendDataButton);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToServer();
            }
        });

        tvServerResponse = (TextView) findViewById(R.id.tvServerResponse);

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
            ServerCommunication sc = new ServerCommunication(AppConstants.getServerAddress(), tvServerResponse);
            JSONObject obj = new JSONObject();

            obj.put("values", new JSONArray(measurements));
            obj.put("placeID", Integer.valueOf(braceletLoc.getText().toString()));
            obj.put("phoneID", Integer.valueOf(phoneName.getText().toString()));
            obj.put("run", Integer.valueOf(noFingRun.getText().toString()));

            sc.sendPost(obj);
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
