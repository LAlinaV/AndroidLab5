package com.example.task1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;

import android.net.Uri;
import android.os.Build;

import android.provider.MediaStore;

import android.widget.Toast;

import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {

    TextView tvOut;
    TextView tvLon;
    TextView tvLat;
    Button startButton;
    Button stopButton;

    LocationManager mlocManager;
    LocationListener mlocListener;



    private final List<Location> locationList = new ArrayList<>();


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        tvOut = findViewById(R.id.textView1);
        tvLon = findViewById(R.id.longitude);
        tvLat = findViewById(R.id.latitude);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mlocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                tvLat.setText("Latitude: " + location.getLatitude());
                tvLon.setText("Longitude: " + location.getLongitude());
                locationList.add(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdates();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
                saveToKMLUsingMediaStore(locationList);
            }
        });

        // Check for location permissions at runtime
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions granted, start requesting location updates
            requestLocationUpdates();
        }

        // Check for write external storage permission at runtime
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    private void stopLocationUpdates() {
        mlocManager.removeUpdates(mlocListener);
    }

    public void saveToKMLUsingMediaStore(List<Location> locationList) {
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "route.kml");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.google-earth.kml+xml");

        // Use the Download directory
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
        }

        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                if (outputStream != null) {
                    outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
                    outputStream.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n".getBytes());
                    outputStream.write("<Document>\n".getBytes());
                    outputStream.write("<name>Route</name>\n".getBytes());
                    outputStream.write("<Placemark>\n".getBytes());
                    outputStream.write("<LineString>\n".getBytes());
                    outputStream.write("<coordinates>\n".getBytes());

                    for (Location location : locationList) {
                        outputStream.write(String.format("%f,%f,0\n",
                                location.getLongitude(), location.getLatitude()).getBytes());
                    }

                    outputStream.write("</coordinates>\n".getBytes());
                    outputStream.write("</LineString>\n".getBytes());
                    outputStream.write("</Placemark>\n".getBytes());
                    outputStream.write("</Document>\n".getBytes());
                    outputStream.write("</kml>\n".getBytes());

                    // Show a toast message on success
                    Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("FileSave", "Error saving file", e);
                Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show a toast message if the URI is null
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start requesting location updates
                requestLocationUpdates();
            } else {
                // Permission denied, handle accordingly
                // For example, display a message or disable location-related functionality
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file operations
            } else {
                // Permission denied, handle accordingly
                // For example, display a message or disable file-related functionality
            }
        }
    }
}
