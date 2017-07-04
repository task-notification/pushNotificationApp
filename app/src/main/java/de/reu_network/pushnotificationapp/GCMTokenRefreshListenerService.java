package de.reu_network.pushnotificationapp;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Maestro on 03.07.2017.
 */

public class GCMTokenRefreshListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);
    }
}
