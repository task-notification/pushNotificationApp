package de.reu_network.pushnotificationapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Maestro on 03.07.2017.
 */

public class GCMRegistrationIntentService extends IntentService {

    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";

    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        registerGCM();
    }

    private void registerGCM()
    {
        Intent registrationComplete = null;
        String token = null;

        try
        {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d("GCMRegIntentService", "token: " + token);

            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);
        }
        catch (Exception e)
        {
            Log.d("GCMRegIntentService", "Registration error");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }

        // send token to server
        HttpURLConnection client = null;
        try
        {
            Log.d("GCMRegIntentService", "try to send token to app server....");
            URL url = new URL("http://localhost/push/device");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("subscription_id", token);

            int response = client.getResponseCode();
            Log.d("GCMRegIntentService", "app server response code: "+response);
        }
        catch(Exception e)
        {
            Log.d("GCMRegIntentService", "app server error: " + e.getMessage());
        }
        finally {
            client.disconnect();
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}
