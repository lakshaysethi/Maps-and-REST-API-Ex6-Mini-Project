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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_REQUEST_CODE = 1234;
    //private static final String GOOGLEAPIKEY = "AIzaSyAqqXcZJ4J4fIkA8Kx4jUDuICjDgvGWdEI";
    private static final String ZOMATOAPIKEY = "f88bce1ea6d25bae45cf4e4e08219694";
    Location lastUserLocation;
    private GoogleMap mMap;
    Button findRestaurantsBtn;
    FusedLocationProviderClient locationClient;
     double lat;
     double  lng ;
     LatLng userLocation;
     String APICallString;
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
                mMap.clear();
                userLocation= getUserLocation();
                if (userLocation!=null){
                    findRestaurantsNearUserLocation(userLocation.latitude,userLocation.longitude);

                }
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

        if (permissionGranted()){
            Task<Location> userLocationTask = locationClient.getLastLocation();

            userLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    lastUserLocation = location;
                    if(lastUserLocation!=null){
                        if (lastUserLocation.getLatitude() !=0) {
                            lat = lastUserLocation.getLatitude();
                            lng = lastUserLocation.getLongitude();
                            userLocation = new LatLng(lat, lng);
                            placeMarkerOnMap(userLocation,"You");
                        }
                    }else{
                        Toast.makeText(MapsActivity.this, "Could Not get Location, Please wait... Is location ON?", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        return userLocation;
    }

    private void placeMarkerOnMap(LatLng userLocation,String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        mMap.addMarker(markerOptions.position(userLocation).title(title));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
    }

    private boolean permissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "LOCATION PERMISSION NOT GRANTED PLEASE GRANT LOCATION PERMISSION", Toast.LENGTH_SHORT).show();
            //ask for permission
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },LOCATION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.INTERNET },LOCATION_REQUEST_CODE+1);
            return false;
        }else{
            return true;
        }
    }

    private void findRestaurantsNearUserLocation(double lat,double lng) {
//        APICallString = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?location="+lat+","+lng+"&radius=500&type=restaurant&fields=geometry,name&key="+GOOGLEAPIKEY;
 //       APICallString ="https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=burger&inputtype=textquery&fields=name,geometry/location&locationbias=circle:1500@"+lat+","+lng+"&key="+GOOGLEAPIKEY;
        APICallString = "https://developers.zomato.com/api/v2.1/search?radius=500&lat="+lat+"&lon="+lng;
        RequestQueue requestQ  = Volley.newRequestQueue(this.getApplicationContext());
        Response.Listener repListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               Log.d("mytag","response from zomato"+response.toString());
                try {
                    JSONArray restaurantArray = (JSONArray) response.get("restaurants");
                    for(int i =0;i<restaurantArray.length(); i++){
                        JSONObject restaurant = restaurantArray.getJSONObject(i).getJSONObject("restaurant");
                        String restaurantname = restaurant.get("name").toString();
                        JSONObject restaurantLoc =  restaurant.getJSONObject("location");
                        Double restLat = Double.parseDouble(restaurantLoc.get("latitude").toString());
                        Double restLng = Double.parseDouble(restaurantLoc.get("longitude").toString());
                        LatLng restlatlng = new LatLng(restLat,restLng);
                        placeMarkerOnMap(restlatlng,restaurantname);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("err",error.toString());
            }
        };
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, APICallString, null, repListener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("user-key", "f88bce1ea6d25bae45cf4e4e08219694");
                return headers;
            }
        };
        requestQ.add(req);
    }

}
