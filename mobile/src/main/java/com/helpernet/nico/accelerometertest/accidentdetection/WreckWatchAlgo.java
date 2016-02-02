package com.helpernet.nico.accelerometertest.accidentdetection;

import android.location.Location;

import com.google.common.collect.EvictingQueue;
import com.helpernet.nico.accelerometertest.SensorData;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by nico on 02/02/16.
 */
public class WreckWatchAlgo implements DetectionAlgorithm {

    private static final int TIME_AFTER_HIGH_ACC = 10;
    private static final int TIME_AFTER_HIGH_PRESSURE = 10;
    private static final int TIME_AFTER_HIGH_SPEED = 10;

    private static final int MIN_PRESSURE_THRESH = 100;
    private static final int MIN_SPEED_TRESH = 50;
    private static final int MIN_ACC_THRESH = 100;
    private static final int MAX_DIST_THRESH = 10;

    private static final int ACCIDENT_THRESH = 1;
    private static final float PRESSURE_WEIGHT = 0.5f;

    private int hadHighPressure = 0;
    private long timeOfLastPressure;

    private int hadHighSpeed = 0;
    private long timeOfLastHighSpeed;
    private double latOfLastHighSpeed;
    private double lngOfLastHighSpeed;

    private TimedAcc lastMaxAcc = null;
    private List<TimedAcc> lastAccs = new LinkedList<>();


    @Override
    public boolean isAccident(SensorData data) {

        computeMaxAcc(data);
        computeHadHighSpeed(data);
        computeHadHighPressure(data);

        float distToLastHighSpeed = getDistance(latOfLastHighSpeed, lngOfLastHighSpeed, data.getLat(), data.getLng());
        boolean highAccAndPressure = (lastMaxAcc.acc / MIN_ACC_THRESH + PRESSURE_WEIGHT * hadHighPressure) > ACCIDENT_THRESH;
        return highAccAndPressure && (hadHighSpeed == 1 || distToLastHighSpeed < MAX_DIST_THRESH);
    }

    private float getDistance(double from_lat, double from_lng, double to_lat, double to_lng) {
        float[] results = new float[1];
        Location.distanceBetween(from_lat, from_lng, to_lat, to_lng, results);
        return results[0];
    }

    private void computeHadHighSpeed(SensorData data) {
        Float speed = data.getSpeed();
        if (speed == null) return;

        long timeSinceLastHighSpeed = timeOfLastHighSpeed - new Date().getTime();

        if (speed > MIN_SPEED_TRESH) {
            timeOfLastHighSpeed = data.getTimestamp();
            hadHighSpeed = 1;
            latOfLastHighSpeed = data.getLat();
            lngOfLastHighSpeed = data.getLng();
        } else if (hadHighSpeed == 1 && timeSinceLastHighSpeed > TIME_AFTER_HIGH_SPEED) {
            hadHighSpeed = 0;
        }
    }

    private void computeHadHighPressure(SensorData data) {
        Float pressure = data.getPressure();
        if (pressure == null) return;

        long timeSinceLastPressure = timeOfLastPressure- new Date().getTime();

        if (pressure > MIN_PRESSURE_THRESH) {
            timeOfLastPressure = data.getTimestamp();
            hadHighPressure = 1;
        } else if (hadHighPressure == 1 && timeSinceLastPressure > TIME_AFTER_HIGH_PRESSURE) {
            hadHighPressure = 0;
        }
    }

    private boolean outdatedAcc(long old, long now) {
        return (now - old) < TIME_AFTER_HIGH_ACC;
    }

    private void computeMaxAcc(SensorData data) {
        if (data.getOverallAcc() == null) return;

        TimedAcc currentAcc = new TimedAcc(data.getOverallAcc(), data.getTimestamp());
        lastAccs.add(currentAcc);

        long now = new Date().getTime();
        if (currentAcc.acc > lastMaxAcc.acc) {
            lastMaxAcc = currentAcc;
        } else if (outdatedAcc(currentAcc.timestamp, now)) {

            TimedAcc maxAcc = new TimedAcc(Double.MIN_VALUE,0);

            for (TimedAcc timedAcc : lastAccs) {
                if (outdatedAcc(timedAcc.timestamp, now)) {
                    lastAccs.remove(timedAcc);
                } else if (timedAcc.acc > maxAcc.acc) {
                    maxAcc = timedAcc;
                }
            }
            lastMaxAcc = maxAcc;
        }
    }
}
