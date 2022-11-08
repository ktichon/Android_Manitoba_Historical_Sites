package com.example.winnipeghistoricalsites;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.winnipeghistoricalsites.databinding.ActivityMapsBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;



public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private RequestQueue queue;
    private List<HistoricalSite> allHistoricalSites;
    private List<Marker> allMarkers;
    private LinearLayout displayInfo;
    private HistoricalSite currentSite;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in {@link
     * #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //binding = ActivityMapsBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        queue = Volley.newRequestQueue(getApplicationContext());
        //String baseUrl = "https://data.winnipeg.ca/resource/ptpx-kgiu.json?";
        //Attempts to filter location not null straight from the source
        //String testUrl = Uri.parse(baseUrl).buildUpon().appendQueryParameter("location", )
       /* String testUrl2 = "test";
        try {
            testUrl2 = URLEncoder.encode("$WHERE=( $location IS NULL)","UTF-8" );
            testUrl2 = testUrl2.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        //String url = baseUrl;
        displayInfo = findViewById(R.id.Details);
        displayInfo.setVisibility(View.GONE);


        try {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,getString(R.string.data_url),null, fetchHistoricalData, getJsonError);
            queue.add(request);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        mMap.setOnMarkerClickListener(this);


        LatLng winnipeg = new LatLng(49.895077, -97.138451);
        //mMap.addMarker(new MarkerOptions().position(winnipeg).title("Marker in Winnipeg"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(winnipeg, 18));
        enableMyLocation();
    }

    /**
     * Fetches all the data from the Winnipeg Open Data Historical Resources and populates the markers and sites with the data
     */
    private Response.Listener<JSONArray> fetchHistoricalData = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            allHistoricalSites = new ArrayList<>();
            allMarkers = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject site = (JSONObject) response.get(i);
                    if (site.has("location"))
                    {
                        try {
                            //Parsing fields
                            HistoricalSite newSite = new HistoricalSite(site.getString("historical_name"));
                            newSite.streetName = site.getString("street_name");
                            newSite.streetNumber = site.getString("street_number");
                            newSite.constructionDate = ((site.has("construction_date")) ? site.getString("construction_date") : null);

                            newSite.shortUrl =((site.has("short_report_url")) ? site.getString("short_report_url") : null);
                            newSite.longUrl = ((site.has("long_report_url")) ? site.getString("long_report_url") : null);

                            //Location
                            JSONObject location = site.getJSONObject("location");
                            newSite.location = new Location("");
                            newSite.location.setLatitude(location.getDouble("latitude"));
                            newSite.location.setLongitude(location.getDouble("longitude"));

                            allHistoricalSites.add(newSite);
                            Marker newMarcker = mMap.addMarker(new MarkerOptions().position(new LatLng(newSite.location.getLatitude(), newSite.location.getLongitude())).title(newSite.name).snippet(newSite.address()));
                            newMarcker.setTag(newSite);
                            allMarkers.add(newMarcker);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }

                    int test = site.length();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            Toast.makeText(getApplicationContext(), "Found all " + allHistoricalSites.size() + " historic sites in Winnipeg", Toast.LENGTH_SHORT).show();

        }
    };

    //Error fetching api
    private Response.ErrorListener getJsonError = error -> {
        Toast.makeText(getApplicationContext(),"Error fetching data from City of Winnipeg Historic Resources API", Toast.LENGTH_SHORT).show();
    };



    //Location Permissions
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
//        if (ActivityCompat.isPermissionGranted(permissions, grantResults,
//                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
//                .isPermissionGranted(permissions, grantResults,
//                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            Toast.makeText(this, "App does not have access to location services", Toast.LENGTH_LONG).show();
            permissionDenied = false;
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        currentSite = (HistoricalSite) marker.getTag();
        setDisplayInfo(currentSite);


        return false;
    }

    //Set info
    public void setDisplayInfo(HistoricalSite site)
    {
        displayInfo.setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.tvName)).setText(site.name);
        ((TextView)findViewById(R.id.tvBuildDate)).setText(site.constructionDate);
        ((TextView)findViewById(R.id.tvAddress)).setText(site.address());
        ((TextView)findViewById(R.id.tvDistance)).setText((int) site.location.distanceTo(mMap.getMyLocation()));



    }
}