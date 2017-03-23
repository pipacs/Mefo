package com.pipacs.mefo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;

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
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage message: messages) {
            String phoneNumber = message.getDisplayOriginatingAddress();
            String body = message.getDisplayMessageBody();
            Log.i(TAG, "handleSmsReceived: From: " + phoneNumber + "; Message: " + body);
            //Toast toast = Toast.makeText(context, "Sender: " + phoneNumber + ", Message: " + body, Toast.LENGTH_LONG);
            //toast.show();
        }
        return START_NOT_STICKY;
    }
}
