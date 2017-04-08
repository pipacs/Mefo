package com.pipacs.mefo;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.ArrayList;

public class Forwarder extends Service {
    static final String TAG = "Forwarder";
    public static final String SMS_RECEIVED_ACTION = "SmsReceived";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: " + intent.getAction());
        if (!intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            return START_NOT_STICKY;
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (!settings.getBoolean(MainActivity.SETTINGS_KEY_ENABLE_FORWARDING, true)) {
            return START_NOT_STICKY;
        }
        String destination = settings.getString(MainActivity.SETTINGS_KEY_DESTINATION, "");
        SmsManager smsManager = SmsManager.getDefault();
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage message: messages) {
            String phoneNumber = message.getOriginatingAddress();
            String body = message.getDisplayMessageBody();
            if (body == null) {
                body = "(no text)";
            }
            Log.i(TAG, "onStartCommand: From: " + phoneNumber + "; To: " + destination + "; Message: " + body);
            String forwardedMessage = body + "\n>> From " + phoneNumber;
            ArrayList<String> forwardedParts = smsManager.divideMessage(forwardedMessage);
            try {
                smsManager.sendMultipartTextMessage(destination, null, forwardedParts, null, null);
            } catch (Exception e) {
                Log.e(TAG, "onStartCommand: " + e.toString());
            }
        }
        return START_NOT_STICKY;
    }
}
