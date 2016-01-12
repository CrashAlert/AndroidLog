package com.helpernet.nico.accelerometertest;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Main";

    private final String[] perms = {
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private final int PERMISSION_REQUEST_ID = 1;

    private final String fileSuffix = "Android-" + android.os.Build.MODEL;

    private boolean isServiceRunning;
    private final String isRunningString = "Stop Logging";
    private final String isNotRunningString = "Start Logging";

    private EditText textInput;
    private Button startStopButton;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isServiceRunning = isMyServiceRunning(SensorLoggerService.class);

        submitButton = (Button) findViewById(R.id.submit_button);
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uploader.sendAllStoredRides();
            }
        });

        checkPermissions();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermissions() {
        ArrayList<String> notGrantedPerms = new ArrayList<>();
        for (String perm : perms) {
            if (!checkIsPermissionGranted(perm)) {
                notGrantedPerms.add(perm);
            }
        }
        if (!notGrantedPerms.isEmpty()) {
            String[] permsToRequest = notGrantedPerms.toArray(new String[notGrantedPerms.size()]);
            requestPermissions(permsToRequest, PERMISSION_REQUEST_ID);
        }
    }

    public boolean checkIsPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startLoggingService(String fileName) {
        isServiceRunning = true;
        Log.i(TAG, "Starting logging background Service");
        Intent serviceIntent = new Intent(getApplicationContext(), SensorLoggerService.class);
        fileName = createFileName(fileName);
        serviceIntent.putExtra("fileName", fileName);
        startService(serviceIntent);
    }

    public String createFileName(String baseName) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String timePrefix =  df.format(new Date());
        return (timePrefix + "-" + baseName + "-" + fileSuffix).replace(" ", "");
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
}
