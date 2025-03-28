package com.example.task31;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (location != null) {
            Log.d(TAG, "Новое местоположение: " + location.getLatitude() + ", " + location.getLongitude());
            // Отправляем данные в MainActivity
            Intent updateIntent = new Intent("LOCATION_UPDATE");
            updateIntent.putExtra("latitude", location.getLatitude());
            updateIntent.putExtra("longitude", location.getLongitude());
            updateIntent.putExtra("speed", location.getSpeed());
            updateIntent.putExtra("accuracy", location.getAccuracy());
            updateIntent.putExtra("time", location.getTime());
            context.sendBroadcast(updateIntent);
        }
    }
}