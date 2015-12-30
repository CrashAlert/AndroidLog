package com.helpernet.nico.accelerometertest;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private TextView xAccText;
    private TextView yAccText;
    private TextView zAccText;

    private final String TAG =  "Main";

    private File dataLogFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorGyrometer = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)) {
         Log.d(TAG, "Sensor name: " + sensor.getName());
        }

        xAccText = (TextView) findViewById(R.id.xAccText);
        yAccText = (TextView) findViewById(R.id.yAccText);
        zAccText = (TextView) findViewById(R.id.zAccText);

//        String[] perms = {"android.permissions.WRITE_EXTERNAL_STORAGE"};
//        requestPermissions(perms, 200);

        dataLogFile = new File(Environment.getExternalStorageDirectory().getPath() + "/dataLog.txt");
        if (!dataLogFile.exists()) {
            try {
                dataLogFile.createNewFile();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                long time = event.timestamp;
//              Log.d(TAG, String.format("Sensor data, time: %d, x: %f, y: %f, z: %f", time, x, y, z));

                storeSensorData(time, x, y, z);

//              showSensorValues(x, y, z);
                break;
            default:
                Log.d(TAG, "Data for Sensor not handled: " + sensor.getName());
                break;
        }
    }

    private void showSensorValues(float x, float y, float z) {
        xAccText.setText("x: " + x);
        yAccText.setText("y: " + y);
        zAccText.setText("z: " + z);
    }

    private void storeSensorData(long timestamp, float x, float y, float z) {
        String dataText = String.format("%d,%f,%f,%f", timestamp, x, y, z);
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(dataLogFile, true));
            buf.append(dataText);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "Storing log data failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
