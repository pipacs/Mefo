package com.pipacs.mefo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static com.pipacs.mefo.MainActivity.updateIndicator;

/** Sets the notification indicator and starts the SMS forwarding service upon boot completion. */
public class BootCompleteReceiver extends BroadcastReceiver {
    private final String TAG = BootCompleteReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(TAG, "onReceive: Boot completed");
            Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();
            updateIndicator(context);
            Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
            context.startService(new Intent(context, SmsForwarder.class));
            Toast.makeText(context, "3", Toast.LENGTH_SHORT).show();
        }
    }
}
