package com.pipacs.mefo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.ArrayList;

public class Forwarder extends Service {
    static final String TAG = "Forwarder";
    static final String SMS_RECEIVED_ACTION = "SmsReceived";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: " + intent.getAction());
        String destination = ""; // FIXME: Get it from preferences
        SmsManager smsManager = SmsManager.getDefault();
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage message: messages) {
            String phoneNumber = message.getOriginatingAddress();
            String body = message.getDisplayMessageBody();
            if (body == null) {
                body = "(no text)";
            }
            Log.i(TAG, "onStartCommand: From: " + phoneNumber + "; Message: " + body);
            //Toast toast = Toast.makeText(context, "Sender: " + phoneNumber + ", Message: " + body, Toast.LENGTH_LONG);
            //toast.show();
            String forwardedMessage = ">> From " + phoneNumber + "\n" + body;
            ArrayList<String> forwardedParts = smsManager.divideMessage(forwardedMessage);
            try {
                smsManager.sendMultipartTextMessage(destination, phoneNumber, forwardedParts, null, null);
            } catch (Exception e) {
                Log.e(TAG, "onStartCommand: " + e.toString());
            }
        }
        return START_NOT_STICKY;
    }
}
