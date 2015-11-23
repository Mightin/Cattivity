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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henneberg.shittyapp.Util.AppConstants;

import java.util.ArrayList;
import java.util.Set;

public class FingerprintOfflineActivity extends AppCompatActivity {

    private ArrayList<Short> measurements;
    private int goalMeasures;
    private int doneMeasures;

    EditText braceletLoc;
    EditText phoneName;
    EditText noOfMeasurements;
    Button scanButton;

    TextView signalStrength;
    Button sendData;

    BluetoothAdapter blAdapter;
    int REQUEST_ENABLE_BT = 5;

    private int discoveries = 0;
    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            discoveries++;
            (Toast.makeText(getApplicationContext(), "LOL stuff " + discoveries, Toast.LENGTH_SHORT)).show();

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
                scanButton.setText("SCANNING...");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Scan finished", Toast.LENGTH_SHORT).show();
                scanButton.setText("SCAN FOR BRACELET");
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_offline);

        measurements = new ArrayList<Short>();
        doneMeasures = 0;

        braceletLoc = (EditText) findViewById(R.id.braceletLocation);
        phoneName = (EditText) findViewById(R.id.phoneName);
        noOfMeasurements = (EditText) findViewById(R.id.noOfMeasurements);

        scanButton = (Button) findViewById(R.id.scanForBracelet);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalMeasures = Integer.parseInt(noOfMeasurements.getText().toString());
                bluetoothProcess();
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

        setupBluetoothAdapter();

        registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void sendDataToServer() {
        Toast.makeText(getApplicationContext(), "MOCK SEND: "+measurements.toString(), Toast.LENGTH_SHORT).show();
    }

    private void addMeasurement(Short RSSI) {
        measurements.add(RSSI);

        signalStrength.setEnabled(true);
        signalStrength.setText("Strength: "+measurements.toString());

        if(measurements.size() == goalMeasures) {
            blAdapter.cancelDiscovery();
            scanButton.setEnabled(false);
            sendData.setEnabled(true);
        }
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
        if(blAdapter.isDiscovering()){
            blAdapter.cancelDiscovery();
        }

        boolean success = blAdapter.startDiscovery();

        if(!success) {
            (Toast.makeText(getApplicationContext(), "BL Scan did not start", Toast.LENGTH_SHORT )).show();
        }
    }

}
