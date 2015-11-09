package com.example.henneberg.shittyapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class BluetoothDevicesActivity extends AppCompatActivity {

    ListView devices;
    Button scanButton;
    TextView thisDevice;

    BluetoothAdapter blAdapter;
    int REQUEST_ENABLE_BT = 5;
    ArrayAdapter<String> arrayAdapter;

    private int discoveries = 0;
    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            discoveries++;
            (Toast.makeText(getApplicationContext(), "LOL stuff "+discoveries, Toast.LENGTH_SHORT )).show();

            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                Short RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(turnDeviceIntoString(device, RSSI));

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                scanButton.setText("SCANNING...");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Scan finished", Toast.LENGTH_SHORT).show();
                scanButton.setText("SCAN");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        thisDevice = (TextView) findViewById(R.id.thisDeviceTV);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        devices = (ListView) findViewById(R.id.listView1);
        devices.setAdapter(arrayAdapter);

        setupBluetoothAdapter();

        scanButton = (Button) findViewById(R.id.button1);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothProcess();
            }
        });


        registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));


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

            thisDevice.setText(blAdapter.getName());
        }
    }


    private void bluetoothProcess() {
        arrayAdapter.clear();

        Set<BluetoothDevice> bonded = blAdapter.getBondedDevices();
        for(BluetoothDevice bd : bonded) {
            arrayAdapter.add(turnDeviceIntoString(bd, Short.MIN_VALUE));
        }

        if(blAdapter.isDiscovering()){
            scanButton.setText("SCAN");
            blAdapter.cancelDiscovery();
        }

        boolean success = blAdapter.startDiscovery();
        //arrayAdapter.add("FAGGOT FAKE DEVICE");

        if(!success) {
            (Toast.makeText(getApplicationContext(), "BL Scan did not start", Toast.LENGTH_SHORT )).show();
        }
    }

    private String turnDeviceIntoString(BluetoothDevice bd, Short RSSI) {
        String sb = bd.getName() + " -- " + bd.getAddress() + " -- " + RSSI;

        return sb;
    }




}
