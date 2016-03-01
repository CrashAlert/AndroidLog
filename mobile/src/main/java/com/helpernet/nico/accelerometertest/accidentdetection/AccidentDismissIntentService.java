package com.helpernet.nico.accelerometertest.accidentdetection;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by nico on 01/03/16.
 */
public class AccidentDismissIntentService extends IntentService {

    protected static final String TAG = "AccidentDismissIS";
    public static final String INTENT_NAME = "BROADCAST_ACCIDENT_DISMISS";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AccidentDismissIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received Intent: " + intent.getAction());
        Intent localIntent = new Intent(INTENT_NAME);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
