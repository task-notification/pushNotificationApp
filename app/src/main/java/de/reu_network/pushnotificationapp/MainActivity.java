package de.reu_network.pushnotificationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private ToggleButton btnToggle;

    private boolean getNotificationSettings()
    {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean("subscribe", false);
    }

    private void setNotificationSettings(boolean value)
    {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("subscribe", value);
        editor.commit();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                Toast.makeText(getApplicationContext(), "Google Play services are not installed/enabled", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            }
            else
            {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Services", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            // nur starten, wenn gewünscht

            if(value)
            {
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
                Log.d("MainActivity", "Receiver registriert");

                Log.d("MainActivity", "starte service....");
                Intent intent = new Intent(getApplicationContext(), GCMRegistrationIntentService.class);
                startService(intent);
            }
            else
            {
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
                Log.d("MainActivity", "Receiver unregister");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean enabledNotifications = getNotificationSettings();

        btnToggle = (ToggleButton) findViewById(R.id.btnToggle);
        btnToggle.setChecked(enabledNotifications);

        btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNotificationSettings(isChecked);
            }
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS))
                {
                    String token = intent.getStringExtra("token");
                    Toast.makeText(getApplicationContext(), "GCM Token: " + token, Toast.LENGTH_LONG).show();
                }else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR))
                {
                    Toast.makeText(getApplicationContext(), "GCM Registration error", Toast.LENGTH_LONG).show();
                }else {
                    // todo
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotificationSettings(getNotificationSettings());
    }
}
