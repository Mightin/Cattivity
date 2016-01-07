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
    EditText edServerLocalAddr;
    EditText edPhoneName;

    EditText edFingRun;
    EditText edNoMeasurements;
    EditText edSetupUsed;

    EditText edExpRun;
    EditText edMaxTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edServerAddr = (EditText) findViewById(R.id.edServerAddr);
        edServerLocalAddr = (EditText) findViewById(R.id.edSServerLocAddr);
        edPhoneName = (EditText) findViewById(R.id.edPhoneName);

        edFingRun = (EditText) findViewById(R.id.edFingRun);
        edNoMeasurements = (EditText) findViewById(R.id.edSNoM);
        edSetupUsed = (EditText) findViewById(R.id.edSSetup);

        edExpRun = (EditText) findViewById(R.id.edSExpRun);
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
        edServerLocalAddr.setText(AppConstants.getServerLocalAddress());
        edPhoneName.setText(AppConstants.getPhoneName());

        edFingRun.setText(""+AppConstants.getFingerprintingRun());
        edNoMeasurements.setText(""+AppConstants.getNoOfMeasurements());
        edSetupUsed.setText(""+AppConstants.getSetupUsed());

        edExpRun.setText(""+AppConstants.getExperimentRun());
        edMaxTime.setText(""+AppConstants.getMaxTime());
    }

    private void saveNewSettings() {
        AppConstants.setServerAddress(edServerAddr.getText().toString());
        AppConstants.setServerLocalAddress(edServerLocalAddr.getText().toString());
        AppConstants.setPhoneName(edPhoneName.getText().toString());

        AppConstants.setFingerprintingRun(Integer.parseInt(edFingRun.getText().toString()));
        AppConstants.setNoOfMeasurements(Integer.parseInt(edNoMeasurements.getText().toString()));
        AppConstants.setSetupUsed(Integer.parseInt(edSetupUsed.getText().toString()));

        AppConstants.setExperimentRun(Integer.parseInt(edExpRun.getText().toString()));
        AppConstants.setMaxTime(Long.parseLong(edMaxTime.getText().toString()));

        (Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT)).show();
    }


}
