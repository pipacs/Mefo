package com.pipacs.mefo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class SmsReceiver extends WakefulBroadcastReceiver {
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
