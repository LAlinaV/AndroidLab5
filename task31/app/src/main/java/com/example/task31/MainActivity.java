package com.example.task31;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.task31.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private LocationManager locationManager;
    private PendingIntent pendingIntent;
    private TextView tvLatitude, tvLongitude, tvSpeed, tvAccuracy, tvTime;

    private BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);
            float speed = intent.getFloatExtra("speed", 0);
            float accuracy = intent.getFloatExtra("accuracy", 0);
            long time = intent.getLongExtra("time", 0);

            tvLatitude.setText(String.valueOf(latitude));
            tvLongitude.setText(String.valueOf(longitude));
            tvSpeed.setText(String.valueOf(speed));
            tvAccuracy.setText(String.valueOf(accuracy));
            tvTime.setText(formatTime(time));
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude = findViewById(R.id.latitude);
        tvLongitude = findViewById(R.id.longitude);
        tvSpeed = findViewById(R.id.speed);
        tvAccuracy = findViewById(R.id.accuracy);
        tvTime = findViewById(R.id.time);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Создаем PendingIntent для BroadcastReceiver
        Intent intent = new Intent(this, LocationReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);       startButton.setOnClickListener(v -> startLocationUpdates());
        stopButton.setOnClickListener(v -> stopLocationUpdates());

        // Регистрируем BroadcastReceiver для обновлений
        registerReceiver(locationUpdateReceiver, new IntentFilter("LOCATION_UPDATE"));
    }
    private String formatTime(long unixTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault()); // Используем текущий часовой пояс устройства
        return sdf.format(new Date(unixTime));
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, pendingIntent);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, pendingIntent);
            Toast.makeText(this, "Отслеживание начато", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates(pendingIntent);
        Toast.makeText(this, "Отслеживание остановлено", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationUpdateReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Разрешение на доступ к местоположению отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}