package com.pipacs.mefo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            handleSmsReceived(context, intent);
        } else {
            Log.i(TAG, "onReceive: Unknown intent: " + intent.toString());
        }
    }

    void handleSmsReceived(Context context, Intent intent) {
        Log.i(TAG, "handleSmsReceived");
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage message: messages) {
            String phoneNumber = message.getDisplayOriginatingAddress();
            String body = message.getDisplayMessageBody();
            Log.i(TAG, "handleSmsReceived: From: " + phoneNumber + "; Message: " + body);
        }

        // Wake up the forwarder service with the messages received
        Intent startIntent = new Intent(context, Forwarder.class);
        startIntent.setAction(Forwarder.SMS_RECEIVED_ACTION);
        startIntent.putExtras(intent);
        context.startService(startIntent);
    }
}
