package com.hax3rzzz.safeconfirm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.telephony.SmsManager;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;
import android.util.Log;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Criteria;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import org.w3c.dom.Text;
import android.Manifest;
import android.net.Uri;
public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener{

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    LocationManager locationManager;
    String mprovider;
    boolean isGreen = true;
    private  double altitude = 0;
    float weightAC = 0;
    float weightGY = 0;
    float weightALT = 0;
    float weight = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        locationManager = (LocationManager)(getSystemService(Context.LOCATION_SERVICE));
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);
        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 15000, 1, this);
            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "No Location Provider Found CsetContentView(R.layout.activity_main);heck Your Code", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendTXT() {
        SmsManager smsManager = SmsManager.getDefault();
        Log.d("preTxt", "askdhfjkalsdhfkas");
//        smsManager.sendTextMessage("4132753002", null, "U", null, null);
        smsManager.sendTextMessage("9784139293", null, "U", null, null);

//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "7813011976"));
//        intent.putExtra("sms_body", "1234876178234");
//        startActivity(intent);


        Log.d("postTxt", "lkajsdnf");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.power_button);
        myFab.setOnClickListener(new View.OnClickListener() {

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

    public float map(float x, float in_min, float in_max, float out_min, float out_max) {
        if (x < in_max) {
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        } else {
            return 100;
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        weightAC = 0;
        weightGY = 0;
        weightALT = 0;
        weight = 0;

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

                float jerk = (float)(Math.sqrt(jerkX * jerkX + jerkY * jerkY + jerkZ * jerkZ));


                last_x = x;
                last_y = y;
                last_z = z;

                weightAC = jerk;

            }
        }

        if(mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
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

                float omega = (float)Math.sqrt(omegaX * omegaX + omegaY * omegaY + omegaZ * omegaZ);


                last_x = x;
                last_y = y;
                last_z = z;

                weightGY = map((long) omega, 0, (long) (4 * Math.PI), 0, 10);

            }
        }
        if (weightALT + weightAC + weightGY > 10) {
            Log.d("asdf", "" + (weightAC + weightALT + weightGY));
            displayPrompt();
            TextView status = (TextView) findViewById(R.id.textView);
            weight = weightAC + weightALT + weightGY;
            Log.d("hi", "hi");
            status.setText("" + (weight/(30.0) * 100.0));
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //@Override
    public void onLocationChanged(Location loc) {
       altitude = loc.getAltitude();
        weightALT = map((float)altitude, 0, 50, 0, 10);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
    }

    public void displayPrompt() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Ya good?").setMessage("Confirm that you are not in danger");
        dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
//                exitLauncher();
            }
        });
        dialog.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                sendTXT();
                Log.d("badSitch", "oh n0");
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();

// Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing() && isGreen) {
                    sendTXT();
                    Log.d("badSitch", "oh n0");
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        //ten seconds rn
        handler.postDelayed(runnable, 10000);
    }
}
