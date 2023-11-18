package com.example.manitobahistoricalsites;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.manitobahistoricalsites.Database.ManitobaHistoricalSite;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.android.volley.Response;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;



public class MapsActivity extends AppCompatActivity
        implements  OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerClickListener
{

    private GoogleMap mMap;
    //private RequestQueue queue;
    private List<HistoricalSite> allHistoricalSites;
    private List<ManitobaHistoricalSite> allManitobaHistoricalSites;
    private List<Marker> allMarkers;
    //private LinearLayout llDisplayInfo;
   // private LinearLayout llPlaceInfo;
    private ManitobaHistoricalSite currentSite;
    /*private Button btnLong;
    private Button btnShort;
    private Button btnGoogle;
    private ImageButton btnDirections;*/
    private SupportMapFragment supportMapFragment;
    private Menu menu;
    private ArrayAdapter<ManitobaHistoricalSite> searchAdapter;
    private AutoCompleteTextView searchSites;



    private boolean cameraFollow = false;


    //To make sure we don't add sites to the map till the map is properly loaded
    /*private boolean allSitesLoaded = false;
    private boolean mapLoaded = false;*/

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

    private final CompositeDisposable mDisposable = new CompositeDisposable();


    public MapsActivity(){
        super(R.layout.activity_maps);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fragmentManager = getSupportFragmentManager();




        allManitobaHistoricalSites = new ArrayList<>();
        allMarkers = new ArrayList<>();

//        supportMapFragment =  SupportMapFragment.newInstance();
//        supportMapFragment.getMapAsync(this);
//        getSupportFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .add(R.id.fcvMap, supportMapFragment, null)
//                .commit();



        /*if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.CANADA);
        }
*/
        Toolbar mToolbar = findViewById(R.id.tbMain);
        //mToolbar.setTitle("");

        setSupportActionBar(mToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar.setTitleTextColor(getColor(androidx.cardview.R.color.cardview_dark_background));
        }
        else
        {
            mToolbar.setTitleTextColor(Color.BLACK);
        }



        /*
        searchSites = (AutoCompleteTextView) findViewById(R.id.atvSearch);
        searchSites.setVisibility(View.INVISIBLE);

        searchAdapter = new ArrayAdapter<ManitobaHistoricalSite>( this, R.layout.search_item_layout, allManitobaHistoricalSites);
        searchSites.setAdapter(searchAdapter);

        searchSites.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> sites, View view, int pos,
                                    long id) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                try {
                    ManitobaHistoricalSite foundSite = (ManitobaHistoricalSite) sites.getItemAtPosition(pos);
                    siteSelected(foundSite.getSite_id(), foundSite.getLocation());
                    displayMarkerInfo(foundSite.getSite_id());

                } catch (Exception e) {
                    Log.e("Error", "SearchSiteAutoAdapter: Error searching for specific site\n" + e.getMessage());
                }




            }
        });*/



        //queue = Volley.newRequestQueue(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        viewModel = new ViewModelProvider(this).get(HistoricalSiteDetailsViewModel.class);
        viewModel.setHistoricalSiteDatabase(getApplicationContext());
        viewModel.getCurrentSite().observe(this, new Observer<ManitobaHistoricalSite>() {
            @Override
            public void onChanged(ManitobaHistoricalSite changedSite) {
                try {
                    currentSite = changedSite;
                    moveCameraToLocation(currentSite.getLocation());
                    displayMarkerInfo(currentSite.getSite_id());
                   /* LatLng sitLocation = new LatLng(currentSite.getLocation().getLatitude(), currentSite.getLocation().getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(sitLocation));*/

                } catch (Exception e) {
                    Log.e("Error", "UpdateCurrentPositonToBeCurrentSite: Error updating the map to reflect the viewmodel\n" + e.getMessage());
                }


            }
        });



        //Set the default value of the details display height
        viewModel.setDisplayMode(DisplayMode.FullMap);
        viewModel.getDisplayMode().observe(this, new Observer<DisplayMode>() {
            @Override
            public void onChanged(DisplayMode displayMode) {
                updateDisplayHeight(displayMode);

            }
        });





        viewModel.setSiteFilters(new SiteFilter());
        viewModel.getSiteFilters().observe(this, new Observer<SiteFilter>() {
            @Override
            public void onChanged(SiteFilter siteFilter) {
                try {
                    //No filter
                    if (siteFilter.isAllMunicipalities() && siteFilter.isAllSiteTypes())
                    {
                        loadManitobaHistoricalSiteData();
                    }
                    //Only municipality filter
                    else if (!siteFilter.isAllMunicipalities() && siteFilter.isAllSiteTypes()){
                        loadManitobaHistoricalSiteDataMunicipalityFilter(siteFilter.getMunicipalityFilter());
                    }
                    //Only type filter
                    else if (siteFilter.isAllMunicipalities() && !siteFilter.isAllSiteTypes()) {
                        loadManitobaHistoricalSiteDataTypeFilter(siteFilter.getSiteTypeFilter());
                    }
                    // Get both municipality and type filter
                    else if (!siteFilter.isAllMunicipalities() && !siteFilter.isAllSiteTypes()) {
                        loadManitobaHistoricalAllFilter(siteFilter.getMunicipalityFilter(), siteFilter.getSiteTypeFilter());
                    }
                } catch (Exception e) {
                    Log.e("Error", "updateDataToComplyWithNewFilters: Error updating the map to reflect the viewmodel\n" + e.getMessage());
                }



            }
        });
        loadManitobaHistoricalSiteData();
        loadMap();





        /*if(allHistoricalSites == null || allHistoricalSites.size() == 0)
        {
            try {
                int test = 0;
               //JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getString(R.string.data_url), null, fetchHistoricalData, getJsonError);
               // queue.add(request);
            } catch (Exception e) {
                Log.e("Error", "onCreate: Fetching city of winnipeg data from url \n" + e.getMessage());
            }
        }
        else
        {
            allSitesLoaded = true;
            addSiteListToMap(allHistoricalSites);
        }*/






    }

    //Set up menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    //Resolve menu select
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {

            if (item.getItemId() == R.id.itFollowCamera) {
                if (this.cameraFollow) {
                    this.cameraFollow = false;
                    item.setIcon(R.drawable.ic_camera_not_follow);
                } else {
                    this.cameraFollow = true;
                    item.setIcon(R.drawable.ic_camera_follow);
                    moveCameraToLocation(getUserLocation());
                }
            } else if (item.getItemId() == R.id.itFilters) {
                ((FragmentContainerView) findViewById(R.id.fcvOtherPages)).setVisibility(View.VISIBLE);
                viewModel.setDisplayMode(DisplayMode.OtherPages);

                fragmentManager.beginTransaction()
                        //.replace(R.id.fcvDetails, HistoricalSiteDetailsFragment.class, null)
                        .replace(R.id.fcvOtherPages, FilterFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();


            }
        }
        catch (Exception e)
        {
            Log.e("Error", "MenuItemSelected: Error selecting menu item\n" + e.getMessage() );
        }
        return super.onOptionsItemSelected(item);
    }

    //Loads map if not already loaded
    public void loadMap()
    {
        if (mMap == null) {
            supportMapFragment =  SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
            getSupportFragmentManager().beginTransaction()
               .setReorderingAllowed(true)
               .add(R.id.fcvMap, supportMapFragment, null)
               .commit();
        }

    }

    //These are broken up into 4 functions to make it easier on the database
    //Loads the data from the .db file
    public void loadManitobaHistoricalSiteData()
    {
        mDisposable.add(
                viewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadAllManitobaHistoricalSites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( manitobaHistoricalSites -> saveSitesToApp( manitobaHistoricalSites),
                                throwable ->  Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                        )
        );
    }


    //Loads only by municipality
    public void loadManitobaHistoricalSiteDataMunicipalityFilter( List<String> municipalities)
    {
        mDisposable.add(
                viewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadManitobaHistoricalSitesFilterMunicipality(municipalities)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( manitobaHistoricalSites -> saveSitesToApp( manitobaHistoricalSites),
                                throwable ->  Toast.makeText(getApplicationContext(), "Error fetching data from specified municipalities", Toast.LENGTH_SHORT).show()
                        )
        );
    }

    //Loads only by type
    public void loadManitobaHistoricalSiteDataTypeFilter(List<String> siteTypes)
    {
        mDisposable.add(
                viewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadManitobaHistoricalSitesFilterType(siteTypes)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( manitobaHistoricalSites -> saveSitesToApp( manitobaHistoricalSites),
                                throwable ->  Toast.makeText(getApplicationContext(), "Error fetching data from specified Sites", Toast.LENGTH_SHORT).show()
                        )
        );
    }

    //Loads by municipality and type
    public void loadManitobaHistoricalAllFilter( List<String> municipalities, List<String> siteTypes)
    {
        mDisposable.add(
                viewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadManitobaHistoricalSitesAllFilters(siteTypes, municipalities)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( manitobaHistoricalSites -> saveSitesToApp( manitobaHistoricalSites),
                                throwable ->  Toast.makeText(getApplicationContext(), "Error fetching data with specified filters", Toast.LENGTH_SHORT).show()
                        )
        );
    }

    //Signals that the sites are loaded and stored in variable list
    public void saveSitesToApp (List<ManitobaHistoricalSite> sites )
    {
        try {

            allManitobaHistoricalSites = sites;

            int total = allManitobaHistoricalSites.size();
            Toast.makeText(getApplicationContext(), "Found all " + allManitobaHistoricalSites.size() + " historic sites in Manitoba", Toast.LENGTH_SHORT).show();
            if (mMap != null)
            {
                addSiteListToMap(allManitobaHistoricalSites);

            }
        }
       catch (Exception e)
       {
           Toast.makeText(getApplicationContext(), "Error storing data", Toast.LENGTH_SHORT).show();
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
        try {
            mMap = googleMap;
            mMap.setOnMarkerClickListener(this);
            //mMap.getUiSettings().setMapToolbarEnabled(false);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
            LatLngBounds manitobaBounds = new LatLngBounds(
                    new LatLng(48, -102), // SW bounds
                    new LatLng(60, -89)  // NE bounds
            );

            mMap.setLatLngBoundsForCameraTarget(manitobaBounds);




            //mMap.addMarker(new MarkerOptions().position(winnipeg).title("Marker in Winnipeg"));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(winnipeg, 15));
            enableMyLocation();
            if (getUserLocation() != null) {
                LatLng current = new LatLng(getUserLocation().getLatitude(), getUserLocation().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                viewModel.getCurrentLocation().setValue(getUserLocation());

            }

            if (allManitobaHistoricalSites != null && allManitobaHistoricalSites.size() > 0)
            {
                addSiteListToMap(allManitobaHistoricalSites);
            }
        }

        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Error displaying map", Toast.LENGTH_SHORT).show();
        }



    }

    //Adds the markers to the map
    private void addSiteListToMap (List<ManitobaHistoricalSite> sitesToAdd)
    {
        try {
            //Removes all markers from map before adding new ones
            mMap.clear();
            allMarkers.clear();

            for (ManitobaHistoricalSite site: sitesToAdd) {
                Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(site.getLatitude(), site.getLongitude())).title(site.getName()).snippet(site.getAddress()));
                newMarker.setTag(site.getSite_id());
                allMarkers.add(newMarker);

            }
            Toast.makeText(getApplicationContext(), "All sites have been added to the map", Toast.LENGTH_SHORT).show();

            //Will add search bar back in later
                /*searchAdapter.notifyDataSetChanged();
                searchSites.setVisibility(View.VISIBLE);*/

        }
        catch (Exception e)
        {
            Log.e("Error", "addSiteListToMap: Error attaching site to map\n" + e.getMessage());
        }



    }





    //Displays marker title specific historical site, used in 'Search' and on current site fragment backspace
    // used in onCreate
    private void displayMarkerInfo(int displayId)
    {
        try {
            if (allMarkers!= null)
            {
                for (Marker marker : allMarkers) {
                    if ((int)marker.getTag() == displayId ) { //if a marker has desired tag
                        marker.showInfoWindow();
                    }
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Error getting marker info", Toast.LENGTH_SHORT).show();
        }

    }





    /**
     * Fetches all the data from the Winnipeg Open Data Historical Resources and populates the markers and sites with the data
     */
    /*private Response.Listener<JSONArray> fetchHistoricalData = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            allHistoricalSites.clear();
            allMarkers = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject site = (JSONObject) response.get(i);
                    if (site.has("location") && site.has("historical_name")) {
                        try {
                            //Parsing fields
                            HistoricalSite newSite = new HistoricalSite(site.getString("historical_name"));
                            newSite.setStreetName(site.getString("street_name"));
                            newSite.setStreetNumber(site.getString("street_number"));
                            newSite.setConstructionDate(((site.has("construction_date")) ? site.getString("construction_date") : null));
                            newSite.setShortUrl(((site.has("short_report_url")) ?  site.getString("short_report_url") : null));
                            newSite.setLongUrl(((site.has("long_report_url")) ?  site.getString("long_report_url") : null));


                            //Location
                            JSONObject location = site.getJSONObject("location");
                            Location newLocation = new Location("");
                            newLocation.setLatitude(location.getDouble("latitude"));
                            newLocation.setLongitude(location.getDouble("longitude"));
                            newSite.setLocation(newLocation);
                            newSite.setCity("winnipeg");
                            newSite.setProvince("MB");


                            //add site to list
                            allHistoricalSites.add(newSite);

                            *//*newSite.streetName = site.getString("street_name");
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
                            allMarkers.add(newMarker);*//*


                            //attachPlaceIdToSite(newSite,id);


                        } catch (Exception e) {
                            Log.e("Error", "fetchHistoricalData: Extract site from json\n" + e.getMessage() + "\n" + site.toString());
                        }


                    }

                } catch (Exception e) {
                    Log.e("Error", "fetchHistoricalData: Site\n" + e.getMessage());
                }

            }

            allSitesLoaded = true;
            addSiteListToMap(allHistoricalSites);


            Toast.makeText(getApplicationContext(), "Found all " + allHistoricalSites.size() + " historic sites in Winnipeg", Toast.LENGTH_SHORT).show();

        }
    };*/

    //Error fetching api
    private Response.ErrorListener getJsonError = error -> {
        Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
        Log.e("Error", "getJsonError: Error fetching json from url " + getString(R.string.data_url)+ "\n" + error.getMessage());
    };



    //On marker click zoom to location and display data
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            int nextSiteIndex = (int) marker.getTag();
            Location siteLocation = new Location("");
            siteLocation.setLatitude(marker.getPosition().latitude);
            siteLocation.setLongitude(marker.getPosition().longitude);

            //ManitobaHistoricalSite newCurrentSite = allManitobaHistoricalSites.stream().filter(site -> site.getSite_id() == currentSiteIndex).findFirst().orElse(null);
            siteSelected(nextSiteIndex,siteLocation);
        }
        catch (Exception e)
        {
            Log.e("Error", "onMarkerClick: Error getting new site\n" + e.getMessage());
        }




        return false;
    }

    private void siteSelected(int nextSiteId, Location newLocation)
    {
        try {
            //removed for testing

                /*currentSite = nextSite;
                viewModel.setCurrentSite(currentSite);*/
                viewModel.setCurrentLocation(getUserLocation());
                viewModel.setDisplayMode(DisplayMode.SiteAndMap);
                Fragment newFragment = HistoricalSiteDetailsFragment.newInstance(nextSiteId);

                fragmentManager.beginTransaction()
                        //.replace(R.id.fcvDetails, HistoricalSiteDetailsFragment.class, null)
                        .replace(R.id.fcvDetails, newFragment, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();

            moveCameraToLocation(newLocation);
        }
        catch (Exception e)
        {
            Log.e("Error", "siteSelected: Error getting details of site id" + nextSiteId +"\n" + e.getMessage());
        }


    }

    //Moves camera to new location
    private void moveCameraToLocation(Location newLocation)
    {
        try {
            LatLng userLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(userLatLng));
        }
        catch (Exception e)
        {
            Log.e("Error", "moveCameraToLocation: Error moving camera to a new location\n" + e.getMessage());
        }

    }


    //Updates the display height when the details is swiped on HistoricSitesDetailsFragment
    private void updateDisplayHeight(DisplayMode displayMode)
    {
        try {
            float mapWeight = 0;
            float detailWeight = 0;
            float otherPagesWeight = 0;

            //Sets the layout_weight for the active fragment container
            switch (displayMode)
            {
                case FullMap:
                    mapWeight = 1;
                    break;
                case FullSiteDetail:
                    detailWeight = 1;
                    break;
                case OtherPages:
                    otherPagesWeight = 1;
                    break;

                case SiteAndMap:
                    mapWeight = Float.parseFloat(getString(R.string.display_both_map));
                    detailWeight = Float.parseFloat(getString(R.string.display_both_details));
                    break;

            }






            try {
                FragmentContainerView  mapView = (FragmentContainerView) findViewById(R.id.fcvMap);
                FragmentContainerView detailView = (FragmentContainerView) findViewById(R.id.fcvDetails);
                FragmentContainerView otherPagesView = (FragmentContainerView) findViewById(R.id.fcvOtherPages) ;
                LinearLayout.LayoutParams mapViewLayoutParams =  (LinearLayout.LayoutParams) mapView.getLayoutParams();
                LinearLayout.LayoutParams detailViewParams =  (LinearLayout.LayoutParams) detailView.getLayoutParams();
                LinearLayout.LayoutParams otherPagesViewParams =  (LinearLayout.LayoutParams) otherPagesView.getLayoutParams();
                mapViewLayoutParams.weight = mapWeight;
                detailViewParams.weight = detailWeight;
                otherPagesViewParams.weight = otherPagesWeight;
                mapView.setLayoutParams(mapViewLayoutParams);
                detailView.setLayoutParams(detailViewParams);
                otherPagesView.setLayoutParams(otherPagesViewParams);


             }
            catch (Exception e)
            {
                Log.e("Error", "updateDisplayHeight: error updating the fragmentContainers to new height" + e.getMessage());
            }


        }
        catch (Exception e)
        {
            Log.e("Error", "updateDisplayHeight: error getting new fragmentContainers weight values" + e.getMessage());
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

        if (mMap == null)
            loadMap();
        if (trackingLocation)
            enableMyLocation();
        if (mMap != null && getUserLocation() != null)
        {
            LatLng current = new LatLng(getUserLocation().getLatitude(), getUserLocation().getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
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

        //If user location is enabled, get the user location. Else get default location (which is The Manitoba Museum)
        if(trackingLocation) {
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
        }
        else
        {
            //The Manitoba Museum lat/long
            userLocation = new Location("");
            userLocation.setLatitude(49.9000253);
            userLocation.setLongitude(-97.1386276);
        }


        return userLocation;

    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            try {
                if (trackingLocation)
                {
                    Location newLocation = locationResult.getLastLocation();
                    if (newLocation != null && newLocation != viewModel.getCurrentLocation().getValue()) {
                        viewModel.getCurrentLocation().setValue(newLocation);
                        if (cameraFollow && mMap != null)
                        {
                            moveCameraToLocation(newLocation);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("Error", "locationCallback: Error updating user location\n" + e.getMessage());
            }

            
        }
    };

    //endregion User Location


}