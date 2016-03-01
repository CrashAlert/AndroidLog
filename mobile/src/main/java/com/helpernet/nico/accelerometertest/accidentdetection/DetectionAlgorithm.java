package com.helpernet.nico.accelerometertest.accidentdetection;

import com.helpernet.nico.accelerometertest.SensorData;

import java.util.Iterator;

/**
 * Created by nico on 01/02/16.
 */
public interface DetectionAlgorithm {
    boolean isAccident(SensorData data);
    void cancelAccident();
}
