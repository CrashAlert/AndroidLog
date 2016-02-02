package com.helpernet.nico.accelerometertest.accidentdetection;

import com.helpernet.nico.accelerometertest.SensorData;

import com.google.common.collect.EvictingQueue;

/**
 * Created by nico on 01/02/16.
 */
public class StateMachineAlgo implements DetectionAlgorithm {

    private static final int SPEED_THRESH = 10;
    private static final int ACC_THRESH = 10;

    private static final int BUFFER_SIZE = 100;

    private enum State { SLOW, FAST, ACCIDENT }
    private State state = State.SLOW;

    private EvictingQueue<SensorData> lastSensorData = EvictingQueue.create(BUFFER_SIZE);

    @Override
    public boolean isAccident(SensorData data) {

        boolean accident = false;
        lastSensorData.add(data);

        switch (state) {
            case SLOW:
                if (isFast()) {
                    state = State.FAST;
                }
                break;
            case FAST:
                if (!isFast()) {
                    if (hadHighAcc()) {
                        accident = true;
                        state = State.ACCIDENT;
                    } else {
                        state = State.SLOW;
                    }
                }
                break;
            case ACCIDENT:
                break;
        }

        return accident;
    }

    private boolean hadHighAcc() {
        Double maxAcc = Double.MIN_VALUE;
        for (SensorData data : lastSensorData) {
            double acc = data.getOverallAcc();
            if (acc != 0 && acc > maxAcc) {
                maxAcc = acc;
            }
        }
        return maxAcc > ACC_THRESH;
    }

    private boolean isFast() {
        Float speedSum = 0f;
        int count = 0;
        for (SensorData data : lastSensorData) {
            Float speed = data.getSpeed();
            if (speed != null) {
                speedSum += speed;
                count++;
            }
        }
        Float avgSpeed = speedSum / count;
        return avgSpeed > SPEED_THRESH;
    }
}
