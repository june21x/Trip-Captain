package com.june.tripcaptain;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.june.tripcaptain.DataClass.Place;
import com.june.tripcaptain.Helper.PermissionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private Task locationResult;
    private static final String TAG = MapActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 18;
    //mDefaultLocation set as Kampar
    private final LatLng mDefaultLocation = new LatLng(4.2509284,101.0585763);

    private static final LatLng LOCATION_A = new LatLng(4.329377, 101.136374);
    private static final LatLng LOCATION_B = new LatLng(4.329332, 101.135703);
    private static final LatLng LOCATION_C = new LatLng(4.329597, 101.135443);



    private static final String directionsTag = "Directions API Request";
    private JSONObject directionsJSON;
    private JSONObject detailsJSON;
    private ArrayList<String> mPlaceIDList;
    private ArrayList<LatLng> mLatLngList;
    private ArrayList<LatLng> mWaypointList;
    private RequestQueue queue;
    private String APIkey;
    private static String detailsBaseURL = "https://maps.googleapis.com/maps/api/place/details/json?";

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        queue = Volley.newRequestQueue(this);
        mPlaceIDList = getIntent().getStringArrayListExtra("place_id");
        mLatLngList = new ArrayList<>();

        for(String placeID : mPlaceIDList) {
            addPlaceMarkers(placeID);
            Log.d("Marker Place ID: ", placeID);
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

        mMap.addMarker(new MarkerOptions().position(mDefaultLocation).title("Welcome to Kampar"));
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        mMap.setBuildingsEnabled(true);

        getDeviceLocation();

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (!mPermissionDenied) {
                locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location)task.getResult();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            getDirections();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: " + task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            showMissingPermissionError();
                        }
                    }
                });
            }
            else {
                showMissingPermissionError();
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void addPlaceMarkers(String placeID) {

        // Get PlaceDetails URL
        String detailsURL = getPlaceDetailsURL(placeID);

        // Request a json response from the provided URL.
        final JsonObjectRequest detailsJSONRequest = new JsonObjectRequest
                (Request.Method.GET, detailsURL, null, response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    detailsJSON = response;
                    try {
                        JSONObject result = detailsJSON.getJSONObject("result");
                        JSONObject geometry = result.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");

                        Double lat = location.getDouble("lat");
                        Double lng = location.getDouble("lng");
                        String placeName = result.getString("name");

                        addMarker(lat, lng, placeName);
                        mLatLngList.add(new LatLng(lat, lng));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.d(TAG, error.toString()));

        // Set the tag on the request.
        detailsJSONRequest.setTag("Details");

        // Add the request to the RequestQueue.
        queue.add(detailsJSONRequest);
    }

    public String getPlaceDetailsURL(String placeID) {
        APIkey = getResources().getString(R.string.google_maps_key);

        String detailsURL = detailsBaseURL
                + "place_id=" + placeID
                + "&fields=name,geometry"
                + "&key=" + APIkey;

        return detailsURL;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        Toast.makeText(this,
                marker.getTitle(),
                Toast.LENGTH_SHORT).show();


        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    public void addMarker(Double lat, Double lng, String name) {
        // Add some markers to the map, and add a data object to each marker.
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(name));
        marker.setTag(0);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }


    public void getDirections() {
        String directionsBaseURL = "https://maps.googleapis.com/maps/api/directions/json?";
        String placeIDPrefix = "place_id:";
        String APIkey = getResources().getString(R.string.google_maps_key);
        String origin = mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
        String destination = placeIDPrefix + mPlaceIDList.get(mPlaceIDList.size()-1);
        mWaypointList = mLatLngList;
        mWaypointList.remove(mWaypointList.size()-1);
        String mode = "walking";

        StringBuilder strWaypoints = new StringBuilder();

        for (int i = 0; i < mWaypointList.size(); i++) {
            strWaypoints.append(mWaypointList.get(i).latitude + "," + mWaypointList.get(i).longitude);

            if (i < mWaypointList.size() - 1) {
                strWaypoints.append("|");
            }
        }

        String directionsURL = directionsBaseURL
                + "origin=" + origin
                + "&destination=" + destination
                + "&waypoints=" + strWaypoints.toString()
                + "&mode=" + mode
                + "&key=" + APIkey;

        Log.d("directionsURL", directionsURL);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a json response from the provided URL.
        final JsonObjectRequest directionsJSONRequest = new JsonObjectRequest
                (Request.Method.GET, directionsURL, null, response -> {
                    Log.d(directionsTag, "Response: " + response.toString());
                    directionsJSON = response;
                    try {
                        JSONArray routes = directionsJSON.getJSONArray("routes");
                        JSONObject route = routes.getJSONObject(0);
                        JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                        String encodedPoints = overviewPolyline.getString("points");
                        List<LatLng> decodedPoints = decodePoly(encodedPoints);

                        //Create PolylineOptions
                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.width(POLYLINE_STROKE_WIDTH_PX);
                        polylineOptions.pattern(PATTERN_POLYLINE_DOTTED);

                        for (int i = 0; i < decodedPoints.size(); i++) {
                            polylineOptions.add(decodedPoints.get(i));
                        }

                        // Add polylines to the map.
                        // Polylines are useful to show a route or some other connection between points.
                        Polyline polyline1 = mMap.addPolyline(polylineOptions);

                        //Store a data object with the polyline, used here to indicate an arbitrary type.
                        polyline1.setTag("Route A");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.d(directionsTag, error.toString()));


        // Set the tag on the request.
        directionsJSONRequest.setTag(directionsTag);

        // Add the request to the RequestQueue.
        queue.add(directionsJSONRequest);


    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
