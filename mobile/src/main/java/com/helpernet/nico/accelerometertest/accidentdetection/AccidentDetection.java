package com.helpernet.nico.accelerometertest.accidentdetection;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.helpernet.nico.accelerometertest.DetectedActivitiesIntentService;
import com.helpernet.nico.accelerometertest.MainActivity;
import com.helpernet.nico.accelerometertest.R;
import com.helpernet.nico.accelerometertest.SensorData;

/**
 * Created by nico on 01/02/16.
 */
public class AccidentDetection {

    private final String TAG = "AccidentDetetecion";

    private DetectionAlgorithm mDetectionAlgorithm;
    private int mNotificationId = 0;
    private Service mService;

    private boolean isAccident = false;

    public boolean getIsAccident() {
        return isAccident;
    }

    public AccidentDetection(Service mainActivity, DetectionAlgorithm algorithm) {
        this.mDetectionAlgorithm = algorithm;
        this.mService = mainActivity;
    }

    public void handleSensorData(SensorData data) {
        boolean accidentDetected = mDetectionAlgorithm.isAccident(data);
        if (!isAccident && accidentDetected) {
            isAccident = true;
            handleAccident();
        }
    }

    public void cancelAccident() {
        isAccident = false;
        mDetectionAlgorithm.cancelAccident();
        NotificationManager mNotifyMgr = (NotificationManager) this.mService
                .getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(mNotificationId);
        mNotificationId++;
    }

    private void handleAccident() {
        Log.e(TAG, "ACCIDENT");
        pushNotification();
    }

    private void pushNotification() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent dismissIntent = new Intent(mService, AccidentDismissIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(mService, 0, dismissIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mService)
                .setSmallIcon(R.drawable.ic_warning_black_48dp)
                .setSound(alarmSound)
                .setColor(Color.rgb(250, 0, 0))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{0, 900, 500, 900, 500})
                .setContentTitle("Crash detected")
                .setContentText("Please tell if this was correct")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_clear_black_48dp, "Dismiss", pendingIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) this.mService
                .getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
