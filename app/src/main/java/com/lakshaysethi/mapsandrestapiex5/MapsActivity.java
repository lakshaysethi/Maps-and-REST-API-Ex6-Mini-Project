package com.lakshaysethi.mapsandrestapiex5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_REQUEST_CODE = 1234;
    Location lastUserLocation;
    private GoogleMap mMap;
    Button findRestaurantsBtn;
    FusedLocationProviderClient locationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }else{
            Toast.makeText(this, "Can not load map", Toast.LENGTH_SHORT).show();
        }
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        findRestaurantsBtn = findViewById(R.id.findRestaurantsNearMeBtn);
        findRestaurantsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserLocation();
                findRestaurantsNearUserLocation();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
        getUserLocation();

    }
    @NonNull
    private LatLng getUserLocation() {
        final double[] lat = new double[1];
        final double[] lng = new double[1];
        final LatLng[] userLocation = new LatLng[1];
        if (permissionGranted()){
            Task<Location> userLocationTask = locationClient.getLastLocation();
            userLocationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
//                if (lastUserLocation !=null){
                    lastUserLocation = task.getResult();
                    assert lastUserLocation.getLatitude() != 0;
                    lat[0] = lastUserLocation.getLatitude();
                    lng[0] = lastUserLocation.getLongitude();
                    userLocation[0] = new LatLng(lat[0], lng[0]);
                    placeMarkerOnMap(userLocation[0]);
//                }else{
//                    Toast.makeText(MapsActivity.this, "Could Not get Location! do you have permission? Is location ON?", Toast.LENGTH_SHORT).show();
//                }
                }
            });
        }else{
            Toast.makeText(this, "You have not granted Location Permission! please grant it :)", Toast.LENGTH_SHORT).show();
        }
        return userLocation[0];
    }

    private void placeMarkerOnMap(LatLng userLocation) {

        mMap.addMarker(new MarkerOptions().position(userLocation).title("user"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
    }

    private boolean permissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "LOCATION PERMISSION NOT GRANTED PLEASE GRANT LOCATION PERMISSION", Toast.LENGTH_SHORT).show();
            //ask for permission
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },LOCATION_REQUEST_CODE);
            return false;
        }else{
            return true;
        }
    }

    private void findRestaurantsNearUserLocation() {
    }

}
