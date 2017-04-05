package com.riseapps.xmusic.executor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by naimish on 5/4/17.
 */

public class ProximityDetector implements SensorEventListener{

    private OnProximityListener mListener;
    private int chop=1;
    private long time=0;
    Context c;

    public ProximityDetector(Context context){
        c=context;
    }

    public interface OnProximityListener {
        public void onProximity();
    }

    public void setOnProximityListener(OnProximityListener listener) {
        this.mListener = listener;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.values[0] <=1) {
                if(chop==1){
                    chop++;
                    time=System.currentTimeMillis();
                }
                else {
                    if(System.currentTimeMillis()-time<2000) {
                        mListener.onProximity();
                       // ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                    }
                    chop = 1;
                }

            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
