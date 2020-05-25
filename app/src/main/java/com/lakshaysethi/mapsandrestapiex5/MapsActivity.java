package com.lakshaysethi.mapsandrestapiex5;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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
        LatLng userLocation = getUserLocation();
        placeMarkerOnMap(userLocation);

    }
    @NonNull
    private LatLng getUserLocation() {

        return null;
    }

    private void placeMarkerOnMap(LatLng userLocation) {

        mMap.addMarker(new MarkerOptions().position(userLocation).title("user"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
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
}
