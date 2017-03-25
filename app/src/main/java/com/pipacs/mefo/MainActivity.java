package com.pipacs.mefo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.util.Log;

public class MainActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = "MainActivity";
    public static final String SETTINGS_KEY_DESTINATION = "destination";
    public static final String SETTINGS_KEY_ENABLE_FORWARDING = "enable_forwarding";
    static final int MY_PERMISSIONS_REQUEST_READ_SEND_SMS = 0;
    static final int MY_NOTIFICATION_ID = 65;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        toggleIndicator();
        if (!hasPermission(Manifest.permission.READ_SMS) || !hasPermission(Manifest.permission.SEND_SMS)) {
            Log.i(TAG, "onCreate: No permission to read or send SMS");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SEND_SMS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        toggleIndicator();
    }

    /** Show/hide the status bar indicator. */
    private void toggleIndicator() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean(MainActivity.SETTINGS_KEY_ENABLE_FORWARDING, true)) {
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Mefo")
                    .setContentText("SMS forwarding is enabled")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            notificationManager.notify(MY_NOTIFICATION_ID, notification);
        } else {
            notificationManager.cancel(MY_NOTIFICATION_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: Permission to read SMS granted");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: Permission to read SMS denied");
            }
            if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: Permission to send SMS granted");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: Permission to send SMS denied");
            }
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /** This method stops fragment injection in malicious applications. */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName) ||
                GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            Preference.OnPreferenceChangeListener destinationListener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    preference.setSummary(value.toString());
                    return true;
                }
            };
            Preference destinationPreference = findPreference(SETTINGS_KEY_DESTINATION);
            destinationPreference.setOnPreferenceChangeListener(destinationListener);
            destinationListener.onPreferenceChange(
                    destinationPreference,
                    PreferenceManager
                            .getDefaultSharedPreferences(destinationPreference.getContext())
                            .getString(destinationPreference.getKey(), ""));
        }
    }
}
