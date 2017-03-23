package com.pipacs.mefo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by pipacs on 23/03/2017.
 */
public class IncomingSms extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        String format = intent.getStringExtra("format");
        for (int i = 0; i < messages.length; i++) {
            String phoneNumber = messages[i].getDisplayOriginatingAddress();
            String body = messages[i].getDisplayMessageBody();
            Log.i("SmsReceiver", "sender: " + phoneNumber + "; message: " + body);
            Toast toast = Toast.makeText(context, "Sender: " + phoneNumber + ", Message: " + body, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
