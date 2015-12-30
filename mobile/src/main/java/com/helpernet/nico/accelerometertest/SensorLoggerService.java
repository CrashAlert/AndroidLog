package com.helpernet.nico.accelerometertest;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by nico on 29/12/15.
 */
public class SensorLoggerService extends Service implements SensorEventListener {

    private final String TAG = "SensorLoggerService";

    private File dataLogFile = null;

    private final int[] sensorTypes = new int[]{
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_ROTATION_VECTOR
    };

    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            handleLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        registerSensors();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        int minTime = 100;
        int minDistance = 10;

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String logFileName = extras.getString("fileName");
            createLogFile(logFileName);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSensors();
        //locationManager.removeUpdates(locationListener);
    }

    public void createLogFile(String fileName) {
        dataLogFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + fileName + ".txt");
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


    public void registerSensors() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        for (int sensorType : sensorTypes) {

            Sensor sensor = sensorManager.getDefaultSensor(sensorType);

            String name = sensor.getName();
            float maxVal = sensor.getMaximumRange();
            float resolution = sensor.getResolution();

            Log.d(TAG, String.format("%s range: %f and resolution: %f", name, maxVal, resolution));

            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void unregisterSensors() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        SensorData data = new SensorData(event.timestamp);
        float x, y, z;

        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                data.setAcc_x(x);
                data.setAcc_y(y);
                data.setAcc_z(z);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                data.setMag_x(x);
                data.setMag_y(y);
                data.setMag_z(z);
                break;
            case Sensor.TYPE_GYROSCOPE:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                data.setGyr_x(x);
                data.setGyr_y(y);
                data.setGyr_z(z);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                data.setRot_x(x);
                data.setRot_y(y);
                data.setRot_z(z);
                Log.d(TAG, event.values[3] + " " + event.values[4]);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                Log.d(TAG, event.values[1] + " " + event.values[2]);
                data.setLin_acc(event.values[0]);
                break;
            case Sensor.TYPE_PRESSURE:
                data.setPressure(event.values[0]);
                break;
            default:
                Log.d(TAG, "Data for Sensor not handled: " + sensor.getName());
                break;
        }

        String csvLine = data.toString();
        Log.d(TAG, sensor.getName() + " " + csvLine + " " + event.values.length);
        new StoreStringTask().execute(csvLine);
    }

    public void handleLocation(Location location) {
        SensorData data = new SensorData(location.getTime());

        data.setLat(location.getLatitude());
        data.setLng(location.getLongitude());
        data.setBearing(location.getBearing());
        data.setAlt(location.getAltitude());
        data.setGPSError(location.getAccuracy());
        String dataString = data.toString();
        new StoreStringTask().execute(dataString);
    }

    private class StoreStringTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... lines) {
            for (String line : lines) {
                storeSensorData(line);
            }
            return null;
        }

        private void storeSensorData(String line) {
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(dataLogFile, true));
                buf.append(line);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                Log.e(TAG, "Storing log data failed!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
