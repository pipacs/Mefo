package com.pipacs.mefo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.app.PendingIntent;

public class MainActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String SETTINGS_KEY_DESTINATION = "destination";
    public static final String SETTINGS_KEY_ENABLE_FORWARDING = "enable_forwarding";
    static final int MY_PERMISSION_REQUEST = 0;
    static final int MY_NOTIFICATION_ID = 65;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        toggleIndicator(this);
        if (!hasPermission(Manifest.permission.READ_SMS)
                || !hasPermission(Manifest.permission.SEND_SMS)
                || !hasPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
                || !hasPermission(Manifest.permission.RECEIVE_SMS)
                || !hasPermission(Manifest.permission.WAKE_LOCK)) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_BOOT_COMPLETED,
                            Manifest.permission.WAKE_LOCK
                    },
                    MY_PERMISSION_REQUEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        toggleIndicator(this);
    }

    /** Show/hide the status bar indicator. */
    public static void toggleIndicator(Context c) {
        NotificationManager notificationManager = (NotificationManager)c.getSystemService(NOTIFICATION_SERVICE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
        if (settings.getBoolean(MainActivity.SETTINGS_KEY_ENABLE_FORWARDING, true)) {
            Intent intent = new Intent(c, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(c)
                    .setContentTitle("Mefo")
                    .setContentText("SMS forwarding is enabled")
                    .setSmallIcon(R.mipmap.ic_indicator)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(MY_NOTIFICATION_ID, notification);
        } else {
            notificationManager.cancel(MY_NOTIFICATION_ID);
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
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
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
                    PreferenceManager.getDefaultSharedPreferences(
                            destinationPreference.getContext()).getString(destinationPreference.getKey(), ""));
        }
    }
}
