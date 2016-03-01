package com.helpernet.nico.accelerometertest;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class SensorLoggerService extends Service implements
         SensorEventListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/sensorLogger/";

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL = 500;
    private static final long ACTIVITY_RECOGNITION_INTERVAL = 1000;

    protected LocationRequest mLocationRequest;
    protected Location initialLocation;
    protected GoogleApiClient mGoogleApiClient;

    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    private final String TAG = "SensorLoggerService";

    private SensorManager sensorManager = null;

    private File dataLogFile = null;


    private final int[] sensorTypes = new int[]{
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_ORIENTATION
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        buildGoogleApiClient();
        registerSensors();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            String logFileName = extras.getString("fileName");
            createLogFile(logFileName);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(DetectedActivitiesIntentService.INTENT_NAME));

        mGoogleApiClient.connect();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSensors();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            stopActivityUpdates();
        }
    }


    public void createLogFile(String fileName) {
        File dataLogDir = new File(FILE_PATH);
        dataLogDir.mkdirs();
        dataLogFile = new File(dataLogDir, fileName + ".csv");
        if (!dataLogFile.exists()) {
            try {
                dataLogFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Couldnt create LogFile: " + e.toString());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(dataLogFile, true));
                buf.append(SensorData.header);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                Log.e(TAG, "Writing header failed!");
                e.printStackTrace();
            }
        }
    }


    public void registerSensors() {
        for (int sensorType : sensorTypes) {
            Sensor sensor = sensorManager.getDefaultSensor(sensorType);

            if (sensor == null) {
                Log.w(TAG, "Sensor not available: " + sensorType);
                continue;
            }

            String name = sensor.getName();
            float maxVal = sensor.getMaximumRange();
            float resolution = sensor.getResolution();

            Log.d(TAG, String.format("%s range: %f and resolution: %f", name, maxVal, resolution));

            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }


    public void unregisterSensors() {
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        // TODO: Compare event.timestamp with SystemClock.elapsedRealtimeNanos()
        SensorData data = new SensorData(event.timestamp);

        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                data.setAcc_x(event.values[0]);
                data.setAcc_y(event.values[1]);
                data.setAcc_z(event.values[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                data.setMag_x(event.values[0]);
                data.setMag_y(event.values[1]);
                data.setMag_z(event.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                data.setGyr_x(event.values[0]);
                data.setGyr_y(event.values[1]);
                data.setGyr_z(event.values[2]);
                break;
            case Sensor.TYPE_ORIENTATION:
                data.setRot_x(event.values[0]);
                data.setRot_y(event.values[1]);
                data.setRot_z(event.values[2]);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                data.setLin_acc_x(event.values[0]);
                data.setLin_acc_y(event.values[1]);
                data.setLin_acc_z(event.values[2]);
                break;
            case Sensor.TYPE_PRESSURE:
                data.setPressure(event.values[0]);
                break;
            default:
                Log.d(TAG, "Data for Sensor not handled: " + sensor.getName());
                break;
        }

        String csvLine = data.toString();
        Log.d(TAG, csvLine);
        new StoreStringTask().execute(csvLine);
    }


    public void handleLocation(Location location) {
        SensorData data = new SensorData(location.getElapsedRealtimeNanos());

        data.setLat(location.getLatitude());
        data.setLng(location.getLongitude());
        data.setBearing(location.getBearing());
        data.setAlt(location.getAltitude());
        data.setGPSError(location.getAccuracy());
        data.setSpeed(location.getSpeed());
        String dataString = data.toString();

        Log.d(TAG, "Location: " + dataString);

        new StoreStringTask().execute(dataString);
    }


    private class StoreStringTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... lines) {

            if (dataLogFile == null) return null;

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

    /**
     *
     * @param status
     */
    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.i(TAG, "Successfully connected to Activity Recognition");
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected void startActivityUpdates() {
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, ACTIVITY_RECOGNITION_INTERVAL, getActivityDetectionPendingIntent()).setResultCallback(this);
    }

    protected void stopActivityUpdates() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, getActivityDetectionPendingIntent());
    }

    private void handleDetectedActivitiesList(ArrayList<DetectedActivity> detectedActivities, long timestamp) {
        SensorData data = new SensorData(timestamp);
        for (DetectedActivity da : detectedActivities) {
            Float confidence = da.getConfidence() / 100f;
            switch (da.getType()) {
                case DetectedActivity.STILL:
                    data.setStation(confidence);
                    break;
                case DetectedActivity.RUNNING:
                    data.setRun(confidence);
                    break;
                case DetectedActivity.WALKING:
                    data.setWalk(confidence);
                    break;
                case DetectedActivity.IN_VEHICLE:
                    data.setAuto(confidence);
                    break;
                case DetectedActivity.ON_BICYCLE:
                    data.setCycling(confidence);
                    break;
                case DetectedActivity.UNKNOWN:
                    data.setUnknown(confidence);
                    break;
                default:
                    Log.d(TAG, "Unhandled activity: "  + confidence + " with type: " + da.getType());

            }
        }
        String dataString = data.toString();

        Log.d(TAG, "Activity: " + dataString);

        new StoreStringTask().execute(dataString);
    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(DetectedActivitiesIntentService.EXTRA_DETECTED_ACTIVITIES);
            long timestamp = intent.getLongExtra(DetectedActivitiesIntentService.EXTRA_TIME, System.currentTimeMillis());
            handleDetectedActivitiesList(updatedActivities, timestamp);
        }
    }


    /**
     * Checks if this app has the Permissions to access the device location
     * @return boolean telling if permission was granted
     */
    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Builds a GoogleApiClient
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
        createLocationRequest();
    }


    /**
     * Sets up the location request
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (hasLocationPermissions()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // get last known location for first data point
        if (initialLocation == null && hasLocationPermissions()) {
            initialLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (initialLocation != null) {
                handleLocation(initialLocation);
            }
        }

        startLocationUpdates();
        startActivityUpdates();
    }


    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        handleLocation(location);
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        mGoogleApiClient.connect();
    }
}
