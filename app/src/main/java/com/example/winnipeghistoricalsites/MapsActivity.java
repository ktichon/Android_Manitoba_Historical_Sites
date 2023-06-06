package com.example.winnipeghistoricalsites;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private RequestQueue queue;
    private List<HistoricalSite> allHistoricalSites;
    private List<Marker> allMarkers;
    private LinearLayout llDisplayInfo;
    private LinearLayout llPlaceInfo;
    private HistoricalSite currentSite;
    private Button btnLong;
    private Button btnShort;
    private Button btnGoogle;
    private ImageButton btnDirections;

    //private Location currentLocation;
    //private FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    private HistoricalSiteDetailsViewModel viewModel;
    private FragmentManager fragmentManager;


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

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean trackingLocation;
    private boolean permissionDenied = false;


    public MapsActivity(){
        super(R.layout.activity_maps);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fcvMap, supportMapFragment, null)
                    .commit();

            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.CANADA);
            }
            queue = Volley.newRequestQueue(getApplicationContext());



            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



            viewModel = new ViewModelProvider(this).get(HistoricalSiteDetailsViewModel.class);
            viewModel.getCurrentSite().observe(this, new Observer<HistoricalSite>() {
                @Override
                public void onChanged(HistoricalSite changedSite) {
                    try {
                        currentSite = changedSite;
                        LatLng sitLocation = new LatLng(currentSite.location.getLatitude(), currentSite.location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(sitLocation));

                    } catch (Exception e) {
                        Log.e("Error", "UpdateCurrentPositonToBeCurrentSite: Error updating the map to reflect the viewmodel\n" + e.getMessage());
                    }


                }
            });

            //Set the default value of the details display height
            viewModel.setCurrentDisplayHeight(DisplayHeight.MEDIUM);


            fragmentManager = getSupportFragmentManager();
            try {
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getString(R.string.data_url), null, fetchHistoricalData, getJsonError);
                queue.add(request);
            } catch (Exception e) {
                Log.d("Error OnCreate Url", e.getMessage());
            }
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


        //mMap.addMarker(new MarkerOptions().position(winnipeg).title("Marker in Winnipeg"));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(winnipeg, 15));
        enableMyLocation();
        if (getUserLocation() != null) {
            LatLng current = new LatLng(getUserLocation().getLatitude(), getUserLocation().getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
        }

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
                    if (site.has("location") && site.has("historical_name")) {
                        try {
                            //Parsing fields
                            HistoricalSite newSite = new HistoricalSite(site.getString("historical_name"));
                            newSite.streetName = site.getString("street_name");
                            newSite.streetNumber = site.getString("street_number");
                            newSite.constructionDate = ((site.has("construction_date")) ? site.getString("construction_date") : null);

                            newSite.shortUrl = ((site.has("short_report_url")) ?  site.getString("short_report_url") : null);
                            newSite.longUrl = ((site.has("long_report_url")) ?  site.getString("long_report_url") : null);

                            //Location
                            JSONObject location = site.getJSONObject("location");
                            newSite.location = new Location("");
                            newSite.location.setLatitude(location.getDouble("latitude"));
                            newSite.location.setLongitude(location.getDouble("longitude"));
                            newSite.city = "winnipeg";
                            newSite.province = "MB";

                            int id = allHistoricalSites.size();
                            allHistoricalSites.add(newSite);
                            Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(newSite.location.getLatitude(), newSite.location.getLongitude())).title(newSite.name).snippet(newSite.address()));
                            newMarker.setTag(id);
                            allMarkers.add(newMarker);


                            //attachPlaceIdToSite(newSite,id);


                        } catch (Exception e) {
                            Log.e("Error", "fetchHistoricalData: Extract site from json\n" + e.getMessage() + "\n" + site.toString());
                        }


                    }

                } catch (Exception e) {
                    Log.e("Error", "fetchHistoricalData: Site\n" + e.getMessage());
                }

            }

            Toast.makeText(getApplicationContext(), "Found all " + allHistoricalSites.size() + " historic sites in Winnipeg", Toast.LENGTH_SHORT).show();

        }
    };

    //Error fetching api
    private Response.ErrorListener getJsonError = error -> {
        Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
        Log.e("Error", "getJsonError: Error fetching json\n" + error.getMessage());
    };


    //On marker click zoom to location and display data
    @Override
    public boolean onMarkerClick(Marker marker) {
        int currentSiteIndex = (int) marker.getTag();
        currentSite = allHistoricalSites.get(currentSiteIndex);
        viewModel.setCurrentSite(currentSite);
        viewModel.setCurrentLocation(getUserLocation());
        LatLng sitLocation = new LatLng(currentSite.location.getLatitude(), currentSite.location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sitLocation));
        if (currentSite.placeId == null)
            attachPlaceIdToSite(currentSite, currentSiteIndex);



       /* setLlDisplayInfo(currentSite);
        if (currentSite.placeId == null)
            attachPlaceIdToSite(currentSite, currentSiteIndex);
        else
            diplayPlaceInfo(currentSite);*/


        Fragment newFragment = HistoricalSiteDetailsFragment.newInstance(currentSite);

        fragmentManager.beginTransaction()
                //.replace(R.id.fcvDetails, HistoricalSiteDetailsFragment.class, null)
                .replace(R.id.fcvDetails, newFragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // name can be null
                .commit();


        return false;
    }


    // Attaches the place id to an historicalsite
    public void attachPlaceIdToSite(HistoricalSite site, int siteIndex) {
        String addressParam = "address=" + (site.address() + " " + site.city + " " + site.province).replace(" ", "%20").replace("+", "%2B");
        String keyParam = "&key=" + getString(R.string.google_maps_key);
        /*try {
            JSONObject idString = new JSONObject(Integer.toString(currentSiteIndex));
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.address_To_Place_Api) + addressParam + keyParam, null, fetchPlaceId, getJsonError);
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, getString(R.string.address_To_Place_Api_Link) + addressParam + keyParam, null, new Response.Listener<JSONObject>() {
                    int index = siteIndex;

                    @Override
                    public void onResponse(JSONObject response) {
                        String placeid = "";
                        String formatedAddress = "";


                        try {
                            JSONArray results = response.getJSONArray("results");
                            JSONObject resultsJSONObject = results.getJSONObject(0);
                            placeid = resultsJSONObject.getString("place_id");
                            formatedAddress = resultsJSONObject.getString("formatted_address");


                            if (placeid != null) {
                                allHistoricalSites.get(siteIndex).placeId = placeid;
                                allHistoricalSites.get(siteIndex).googleAddress = formatedAddress;


                                try {
                                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.BUSINESS_STATUS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.PHOTO_METADATAS, Place.Field.WEBSITE_URI);

                                    // Construct a request object, passing the place ID and fields array.
                                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(currentSite.placeId, placeFields);
                                    PlacesClient placesClient = Places.createClient(getApplicationContext());


                                    placesClient.fetchPlace(request).addOnSuccessListener((placeResponse) -> {
                                        allHistoricalSites.get(siteIndex).place = placeResponse.getPlace();


                                    }).addOnFailureListener((exception) -> {
                                        Log.e("Error", "attachPlaceIdToSite: Error fetching place API\n" + exception.getMessage());
                                    });
                                } catch (Exception e) {
                                    Log.e("Error", "attachPlaceIdToSite: Error with Place data\n" + e.getMessage());
                                }


                            }


                        } catch (Exception e) {
                            Log.e("Error", "attachPlaceIdToSite: Error with place id\n" + e.getMessage());
                        }

                    }
                }, getJsonError);
        queue.add(jsonObjectRequest);
    }


    //Gets direction infromation from the google directions api
    public void getDirectionsApi(HistoricalSite site) {
        Location userLocation = getUserLocation();
        if (userLocation != null) {
            String origin = "origin=" + userLocation.getLatitude() + "," + userLocation.getLongitude();
            String destination = "destination=";
            if (site.placeId != null)
                destination += "place_id:" + site.placeId;
            else
                destination += site.location.getLatitude() + "," + site.location.getLongitude();
            String alternatives = "alternatives=false";

            String departureTime = "departure_time=now";
            String mode = "mode=driving";
            String units = "units=metric";
            String directionUrl = getString(R.string.directions_Api_Link) + origin + "&" + destination + "&" + alternatives + "&" + departureTime + "&" + mode + "&" + units + "&key=" + getString(R.string.google_maps_key);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, directionUrl, null, new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject response) {


                            try {
                                String status = response.getString("status");
                                if (status.equals("OK")) {
                                    //JSONArray routesArray = response.getJSONArray("routes");
                                    JSONObject routesObject = response.getJSONArray("routes").getJSONObject(0);
                                    JSONObject overViewPolyLineJson = routesObject.getJSONObject("overview_polyline");
                                    String summary = routesObject.getString("summary");
                                    JSONObject legObject = routesObject.getJSONArray("legs").getJSONObject(0);
                                    JSONObject durationObject = legObject.getJSONObject("duration_in_traffic");
                                    String duration = durationObject.getString("text");


                                    Log.i("info", routesObject.toString());


                                    try {
                                        PolylineOptions directionLine = new PolylineOptions();


                                    } catch (Exception e) {
                                        Log.e("Error", "getDirectionsApi: Error parcing data\n" + e.getMessage());
                                    }
                                } else {
                                    Log.e("Error", "getDirectionsApi: Result Status = " + status);
                                    Toast.makeText(getApplicationContext(), "There was an issue getting directions", Toast.LENGTH_SHORT).show();
                                }


                            } catch (Exception e) {
                                Log.e("Error", "getDirectionsApi: Error extracting Direction data\n" + e.getMessage());
                            }

                        }
                    }, getJsonError);
            queue.add(jsonObjectRequest);
        } else {
            Toast.makeText(this, "Please make sure that you have enabled us to access your location.", Toast.LENGTH_LONG).show();
        }
    }


    //region User Location

    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (trackingLocation)
            enableMyLocation();
    }

    //Location Permissions
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // . . . . other initialization code
            locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(30000)
                    .setMaxUpdateDelayMillis(10000)
                    .build();

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            trackingLocation = true;


            return;
        }

        // 2. Otherwise, request location permissions from the user.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    //Fancy new way to get location
    private Location getUserLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        Location userLocation = null;
        try {
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
            userLocation = location;
        } catch (Exception e) {
            Toast.makeText(this, "Error fetching location. Please make sure to enable location in your settings.", Toast.LENGTH_LONG).show();
            Log.e("Error", "getUserLocation: Error fetching user location\n" + e.getMessage());
        }


        return userLocation;

    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location newLocation = locationResult.getLastLocation();
            if (newLocation != null && newLocation != viewModel.getCurrentLocation().getValue()) {
                  viewModel.getCurrentLocation().setValue(newLocation);
            }
        }
    };



    //endregion User Location


    //region Code moved into fragments

 /*   //Sets text view data if it isn't null, else hide the textview
    private void setTextView(int viewId, String viewText)
    {
        TextView textView = findViewById(viewId);
        if (viewText != null && !viewText.trim().isEmpty())
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText(viewText);
        }
        else
        {
            textView.setVisibility(View.GONE);
        }
    }


    //Opens the web view activity and display the short or long link
    public void openWebPage(String url) {
        //url = "https://developer.android.com/reference/android/webkit/WebView";
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "There is no addition information about the historic site " + currentSite.name + " in this app.", Toast.LENGTH_SHORT).show();
        } else {
            *//*Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);*//*

            *//*Intent intent = new Intent(getApplicationContext(), WebviewActivity.class);
            intent.putExtra(getString(R.string.webviewUrl), url);
            startActivity(intent);*//*

            //Better and easier to pull up link in browser than in my own webview
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);


        }
    }*/

/*    //Displays information from the Place google API
    public void diplayPlaceInfo(HistoricalSite site)
    {

        if (site.place != null && site == currentSite && llDisplayInfo.getVisibility() == View.VISIBLE)
        {
            try {
                Place sitePlace = site.place;
                setTextView(R.id.tvBusinessStatus, (sitePlace.getBusinessStatus() == null? null: sitePlace.getBusinessStatus().toString()));
                //setTextView(R.id.tvOpeningHours, (sitePlace.getOpeningHours() == null? null: sitePlace.getOpeningHours().toString()));
                setTextView(R.id.tvOpeningHours, (sitePlace.isOpen() == null? null: (sitePlace.isOpen()? "Open Now": "Closed") ));
                setTextView(R.id.tvPhoneNumber, sitePlace.getPhoneNumber());
                setTextView(R.id.tvBusinessUrl, (sitePlace.getWebsiteUri() == null? null: sitePlace.getWebsiteUri().toString()));
                List<PhotoMetadata> allPhotos = sitePlace.getPhotoMetadatas();
                if (allPhotos != null)
                    Toast.makeText(getApplicationContext(), allPhotos.size() + " photos found" , Toast.LENGTH_SHORT).show();
                //  PhotoMetadata firstPhoto = allPhotos.get(0);
                llPlaceInfo.setVisibility(View.VISIBLE);
            } catch (Exception e)
            {
                Log.e("Error", "diplayPlaceInfo: Error displaying place info\n" +  e.getMessage());
            }


        }
        else {
            llPlaceInfo.setVisibility(View.GONE);
        }
        // Specify the fields to return.

    }*/


//    //Set info
//    public void setLlDisplayInfo(HistoricalSite site) {
//        llDisplayInfo.setVisibility(View.VISIBLE);
//        TextView nameAndBuildDate = findViewById(R.id.tvNameAndBuild);
//        String buildDate = (TextUtils.isEmpty(site.constructionDate)? "": " (" + site.constructionDate + ")");
//        nameAndBuildDate.setText(site.name + buildDate);
//        //((TextView) findViewById(R.id.tvNameAndBuild)).setText(site.name);
//        //((TextView) findViewById(R.id.tvBuildDate)).setText(site.constructionDate);
//        ((TextView) findViewById(R.id.tvAddress)).setText(site.address());
//
//        //if links are null, hide more info button
//        btnShort.setVisibility((TextUtils.isEmpty(site.shortUrl)? View.GONE: View.VISIBLE));
//        btnLong.setVisibility((TextUtils.isEmpty(site.longUrl)? View.GONE: View.VISIBLE));
///*
//        if (TextUtils.isEmpty(site.shortUrl))
//            btnShort.setVisibility(View.GONE);
//        else
//            btnShort.setVisibility(View.VISIBLE);
//
//        if (TextUtils.isEmpty(site.shortUrl))
//            btnShort.setVisibility(View.GONE);
//        else
//            btnShort.setVisibility(View.VISIBLE);*/
//
//        Float distance = site.location.distanceTo(getUserLocation()) ;
//        String distanceText = (distance >= 1000? String.format("%.2f",distance/1000) + " km": String.format("%.2f",distance) + " m");
//        //String distanceText = String.format("%.2f",distance) + " m";
//
//
//
//
//        ((TextView)findViewById(R.id.tvDistance)).setText(distanceText + " away");

    //endregion Code moved into fragments






}