package com.helpernet.nico.accelerometertest.accidentdetection;

import android.util.Log;

import com.helpernet.nico.accelerometertest.SensorData;

/**
 * Created by nico on 01/02/16.
 */
public class AccidentDetection {

    private final String TAG = "AccidentDetetecion";

    private DetectionAlgorithm mDetectionAlgorithm;

    private boolean isAccident = false;

    public boolean getIsAccident() {
        return isAccident;
    }

    public AccidentDetection(DetectionAlgorithm algorithm) {
        this.mDetectionAlgorithm = algorithm;
    }

    public void handleSensorData(SensorData data) {
        isAccident = mDetectionAlgorithm.isAccident(data);
        if (isAccident) {
            handleAccident();
        }
    }

    private void handleAccident() {
        Log.e(TAG, "ACCIDENT");
    }
}
