package com.hax3rzzz.safeconfirm;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.telephony.SmsManager;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LoadJSONTask.Listener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    // MITRE Weather Station, Miles Wilhelms-Tricarico's API Key
    public static final String URL = "http://api.wunderground.com/api/7ac454145bcfaa43/conditions/q/pws:KMABEDFO4.json";

    private JSONArray categoriesArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        new LoadJSONTask(this).execute(URL);
    }

    public void sendTXT(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("7813011976", null, "UR FRIEND DON GOOFED", null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.power_button);
        myFab.setOnClickListener(new View.OnClickListener() {
            boolean isGreen = true;
            public void onClick(View v) {
                if (isGreen) {
                    myFab.setBackgroundTintList(myFab.getResources().getColorStateList(R.color.colorRed));
                    isGreen = false;
                } else {
                    myFab.setBackgroundTintList(myFab.getResources().getColorStateList(R.color.colorGreen));
                    isGreen = true;

                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences: {
                Intent intent = new Intent();
                intent.setClassName(this, "com.hax3rzzz.safeconfirm.MyPreferenceActivity");
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onError() {
    }

    // WEATHER STUFF
    public void onLoaded(JSONObject weatherObject) {
        try {
            String weatherDescr = weatherObject.getJSONObject("current_observation").getString("weather");
            Log.d("weather", weatherDescr);
            String temperature = weatherObject.getJSONObject("current_observation").getString("temp_f");
            Log.d("weather", temperature);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
