package com.example.henneberg.shittyapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView textView1, textView2, textView3;

    EditText editField;

    Button button;
    Button bluetoothButton;
    Button serverCommButton;
    ProgressBar progressBar;
    VideoView videoView;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.tv1);
        textView2 = (TextView) findViewById(R.id.tv2);
        textView3 = (TextView) findViewById(R.id.tv3);

        editField = (EditText) findViewById(R.id.editField);
        button = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        setupButtonOnClick();

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

    }

    private void setupButtonOnClick() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColors();
                String input = editField.getText().toString();
                if (input.equalsIgnoreCase("forsen")) {
                    progressBar.setProgress(progressBar.getMax());
                    textView1.setTextColor(Color.GREEN);
                } else if (input.equalsIgnoreCase("trump")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=YaG5SAw1n0")));
                    progressBar.setProgress(0);
                    textView3.setTextColor(Color.RED);
                } else {
                    textView2.setTextColor(Color.BLUE);
                }
            }
        });
    }

    private void resetColors() {
        textView1.setTextColor(Color.BLACK);
        textView2.setTextColor(Color.BLACK);
        textView3.setTextColor(Color.BLACK);
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
