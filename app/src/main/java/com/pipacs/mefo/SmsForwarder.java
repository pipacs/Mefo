package com.pipacs.mefo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

/** Service to forward incoming SMS. */
public class SmsForwarder extends Service {
    private final String TAG = SmsForwarder.class.getSimpleName();
    private SmsReceiver smsReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: Registeting SMS receiver");
        smsReceiver = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (smsReceiver != null) {
            Log.i(TAG, "onDestroy: Unregistering SMS receiver");
            unregisterReceiver(smsReceiver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: " + intent.getAction());
        return START_STICKY;
    }

    /** Receives and forwards incoming SMS. */
    class SmsReceiver extends BroadcastReceiver {
        private final String TAG = SmsReceiver.class.getSimpleName();

        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "SmsReceiver.onReceive: " + intent.getAction());
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SmsForwarder.this);
            if (!settings.getBoolean(MainActivity.SETTINGS_KEY_ENABLE_FORWARDING, true)) {
                Log.d(TAG, "onReceive: Forwarding not enabled");
                return;
            }
            String destination = settings.getString(MainActivity.SETTINGS_KEY_DESTINATION, "");
            if (destination.equals("")) {
                Log.d(TAG, "onReceive: Destination not set");
                return;
            }
            SmsManager smsManager = SmsManager.getDefault();
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for (SmsMessage message : messages) {
                String phoneNumber = message.getDisplayOriginatingAddress();
                if (phoneNumber == null) {
                    phoneNumber = "(unknown)";
                }
                String body = message.getDisplayMessageBody();
                if (body == null) {
                    body = "(no text)";
                }
                Log.i(TAG, "onReceive: From: " + phoneNumber + "; To: " + destination + "; Message: " + body);
                String forwardedMessage = body + "\n>> From " + phoneNumber;
                ArrayList<String> forwardedParts = smsManager.divideMessage(forwardedMessage);
                try {
                    smsManager.sendMultipartTextMessage(destination, null, forwardedParts, null, null);
                } catch (Exception e) {
                    Log.e(TAG, "onReceive: " + e.toString());
                }
            }
        }
    }
}
