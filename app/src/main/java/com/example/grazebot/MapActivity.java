package com.example.grazebot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import com.example.grazebot.HttpHandler.OnResponseReceived;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, HttpHandler.OnResponseReceived {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;

    // Variables
    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private String IP_ADDRESS = null;
    private Marker marker;
    private Marker grazeBot;
    private double grazeBotLat;
    private double grazeBotLong;
    private final int interval = 1000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            getGPSData();
            handler.postDelayed(runnable, interval);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle bundle = getIntent().getBundleExtra("data");
        IP_ADDRESS = bundle.getString("ip_address");
        Log.d(TAG, "onCreate: IP:" + IP_ADDRESS);
        getGPSData();
        getLocationPermission();
    }

    public void onClickUpdateGPS(View view) {
        getGPSData();
    }

    private void init() {
        Log.d(TAG, "init: Initialising");
    }

    private void getGPSData() {
        HttpHandler httpHandler = new HttpHandler(this);
        JsonDataTemplate dataTemplate = new JsonDataTemplate(new HashMap<String, String>() {{
            put("command", "test");
        }});
        httpHandler.makeRequest(IP_ADDRESS, dataTemplate.getJsonData());
    }

    @Override
    public void onResponseReceived(JsonParser response) {
        Log.d(TAG, "onResponseReceived: Response: " + response);

        grazeBotLat = Double.parseDouble(response.getMap().get("robot_latitude"));
        grazeBotLong = Double.parseDouble(response.getMap().get("robot_longitude"));
        getFixOnMap();
        Log.d(TAG, "onResponseReceived: " + grazeBotLat + ", " + grazeBotLong);
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting the devices current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "getDeviceLocation: Found location");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");
                        } else {
                            Log.d(TAG, "getDeviceLocation: Current location is null");
                            Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: Initialising Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment
                .getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called");

        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: Permission Failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    mLocationPermissionsGranted = true;

                    // Initialise the map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        mMap = googleMap;
    }

    private void getFixOnMap()
    {
        LatLng GRAZEBOT = new LatLng(grazeBotLat, grazeBotLong);
        Log.d(TAG, "onMapReady: " + grazeBotLat + ", " + grazeBotLong);
        grazeBot = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(grazeBotLat, grazeBotLong))
                //        .position(new LatLng(53.883886667, -9.247278333))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //Toast.makeText(getApplicationContext(), "GrazeBot Location\nLat: " + grazeBotLat + ",\nLong : " + grazeBotLong, Toast.LENGTH_LONG).show();

        if (mLocationPermissionsGranted) {
            //getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);

            init();
        }

        if (mMap != null) {
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Geocoder geocoder = new Geocoder(MapActivity.this);
                    List<Address> list;
                    try {
                        list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        return;
                    }
                    Address address = list.get(0);
                    if (marker != null) {
                        marker.remove();
                    }
                    MarkerOptions options = new MarkerOptions()
                            .title(address.getLocality())
                            .position(new LatLng(latLng.latitude, latLng.longitude));
                    marker = mMap.addMarker(options);
                    Toast.makeText(getApplicationContext(), "Lat: " + latLng.latitude + ",\nLong : " + latLng.longitude, Toast.LENGTH_LONG).show();
                }
            });
        }
        moveCamera(GRAZEBOT, 4, "robot");
    }
}

