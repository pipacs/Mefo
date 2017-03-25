package com.pipacs.mefo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Intent startIntent = new Intent(context, Forwarder.class);
            startIntent.setAction(Forwarder.SMS_RECEIVED_ACTION);
            startIntent.putExtras(intent);
            context.startService(startIntent);
        }
    }
}
