package com.example.task1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView begin;
    TextView latitude;
    TextView longitude;
    TextView altitude;
    TextView end;
    Button start;
    Button stop;
    LocationManager mLocManager;
    Date startTime;

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitude.setText(String.valueOf(location.getLatitude()));
                longitude.setText(String.valueOf(location.getLongitude()));
            } else {
                latitude.setText("Sorry, location");
                longitude.setText("unavailable");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public void Start(View v){
        startTime = new Date();
        //begin.setBackgroundColor(getResources().getColor(R.color.black));
        begin.setText(startTime.toLocaleString());
        end.setText("");
    }

    public void Stop(View v){
        long dif = new Date().getTime() - startTime.getTime();
        end.setText(String.format(getString(R.string.elapsed), dif / 1000.));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        begin = (TextView) findViewById(R.id.startV);
        latitude = (TextView) findViewById(R.id.latV);
        longitude = (TextView) findViewById(R.id.lonV);
        altitude = (TextView) findViewById(R.id.altV);
        end = (TextView) findViewById(R.id.elTV);
        start = (Button) findViewById(R.id.startB);
        stop = (Button) findViewById(R.id.stopB);

        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            begin.setText("GPS is turned on...");
        } else {
            begin.setText("GPS is not turned on...");
        }
    }
}