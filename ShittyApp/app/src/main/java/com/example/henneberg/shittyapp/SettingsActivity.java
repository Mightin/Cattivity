package com.example.henneberg.shittyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.henneberg.shittyapp.Util.AppConstants;

public class SettingsActivity extends AppCompatActivity {

    Button saveButton;

    EditText edServerAddr;
    EditText edPhoneName;
    EditText edFingRun;
    EditText edMaxTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edServerAddr = (EditText) findViewById(R.id.edServerAddr);
        edPhoneName = (EditText) findViewById(R.id.edPhoneName);
        edFingRun = (EditText) findViewById(R.id.edFingRun);
        edMaxTime = (EditText) findViewById(R.id.edMaxTime);
        loadCurrentSettings();

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewSettings();
            }
        });


    }

    private void loadCurrentSettings() {
        edServerAddr.setText(AppConstants.getServerAddress());
        edPhoneName.setText(AppConstants.getPhoneName());
        edFingRun.setText(""+AppConstants.getFingerprintingRun());
        edMaxTime.setText(""+AppConstants.getMaxTime());
    }

    private void saveNewSettings() {
        AppConstants.setServerAddress(edServerAddr.getText().toString());
        AppConstants.setPhoneName(edPhoneName.getText().toString());
        AppConstants.setFingerprintingRun(Integer.parseInt(edFingRun.getText().toString()));
        AppConstants.setMaxTime(Long.parseLong(edMaxTime.getText().toString()));

        (Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT)).show();
    }


}
