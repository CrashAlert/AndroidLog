package com.helpernet.nico.accelerometertest.accidentdetection;

/**
 * Created by nico on 02/02/16.
 */
public class TimedAcc {
    public final Double acc;
    public final long timestamp;
    public TimedAcc(Double acc, long timestamp) {
        this.acc = acc;
        this.timestamp = timestamp;
    }
}