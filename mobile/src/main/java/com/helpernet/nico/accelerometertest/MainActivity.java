package com.helpernet.nico.accelerometertest;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Main";

    private boolean isServiceRunning = false;
    private String isRunningString = "Stop Logging";
    private String isNotRunningString = "Start Logging";

    EditText textInput;
    Button startStopButton;

    private File dataLogFile;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        startStopButton = (Button) findViewById(R.id.start_stop_button);
        setButtonText();
        textInput = (EditText) findViewById(R.id.text_input);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceRunning) {
                    stopLoggingService();
                } else {
                    startLoggingService(textInput.getText().toString());
                }
                setButtonText();
            }
        });

        String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION"};
        requestPermissions(perms, 200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startLoggingService(String fileName) {
        isServiceRunning = true;
        Log.i(TAG, "Starting logging background Service");
        Intent serviceIntent = new Intent(getApplicationContext(), SensorLoggerService.class);
        serviceIntent.putExtra("fileName", fileName);
        startService(serviceIntent);
    }

    public void stopLoggingService() {
        isServiceRunning = false;
        Log.i(TAG, "Stopping logging background Service");
        Intent serviceIntent = new Intent(getApplicationContext(), SensorLoggerService.class);
        stopService(serviceIntent);
    }

    public void setButtonText() {
        if (isServiceRunning) {
            startStopButton.setText(isRunningString);
        } else {
            startStopButton.setText(isNotRunningString);
        }
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
