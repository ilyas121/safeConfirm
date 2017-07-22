package com.hax3rzzz.safeconfirm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.telephony.SmsManager;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void sendTXT(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("7813011976", null, "UR FRIEND DON GOOFED", null, null);
    }

    public long map(long x, long in_min, long in_max, long out_min, long out_max)
    {
        if(x < in_max) {
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        }
        else{
            return 10000000;
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        float weightAC = 0;
        float weightGY = 0;
        float weightALT = 0;

        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float jerkX = x - last_x;
                float jerkY = y - last_y;
                float jerkZ = z - last_z;

                float jerk = (float)Math.sqrt(jerkX*jerkX + jerkY*jerkY + jerkZ*jerkZ);


                last_x = x;
                last_y = y;
                last_z = z;

                weightAC = map((long) jerk, 0, 10, 0, 10);
            }
        }

        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float omegaX = x - last_x;
                float omegaY = y - last_y;
                float omegaZ = z - last_z;

                float omega = (float)Math.sqrt(omegaX*omegaX + omegaY*omegaY + omegaZ*omegaZ);


                last_x = x;
                last_y = y;
                last_z = z;

                weightGY = map((long)omega, 0, (long)(4 * Math.PI), 0, 10);
            }
        }


        if(weightALT + weightAC +weightGY > 10){
            sendTXT();
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
