package com.helpernet.nico.accelerometertest;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Main";

    private final String[] perms = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION"
    };

    private final String fileSuffix = "Android-" + android.os.Build.MODEL;

    private boolean isServiceRunning;
    private String isRunningString = "Stop Logging";
    private String isNotRunningString = "Start Logging";

    EditText textInput;
    Button startStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        isServiceRunning = isMyServiceRunning(SensorLoggerService.class);

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
            requestPermissions(permsToRequest, 200);
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
        return timePrefix + "-" + baseName + "-" + fileSuffix;
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
