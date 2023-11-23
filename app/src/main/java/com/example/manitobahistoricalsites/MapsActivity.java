package com.example.manitobahistoricalsites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import androidx.preference.PreferenceManager;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
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

    private Toolbar mToolbar;
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

    private final int cameraZoom = 16;

    SharedPreferences prefs;

    //Stop app from loading data twice when app is launched
    boolean firstAppLoad = true;




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

        mToolbar = findViewById(R.id.tbMain);
        setSupportActionBar(mToolbar);

        //Setting Up Preference values
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        updateBackgroundColour(prefs.getString(getString(R.string.background_colour_key), "#FFFFFF"));
        updateTextColour(prefs.getString(getString(R.string.text_colour_key), "#000000"));
        //getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE).registerOnSharedPreferenceChangeListener(preferenceChangeListener);




        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar.setTitleTextColor(getColor(androidx.cardview.R.color.cardview_dark_background));
        }
        else
        {
            mToolbar.setTitleTextColor(Color.BLACK);
        }*/



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


        //Location stuff
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

        //Set up filters on view model
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





        //Load data
        loadManitobaHistoricalSiteData();
        //Then load map. I think it takes longer
        loadMap();
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
                viewModel.setDisplayMode(DisplayMode.FullSiteDetail);
                fragmentManager.popBackStack(getString(R.string.site_fragment), fragmentManager.POP_BACK_STACK_INCLUSIVE);


                fragmentManager.beginTransaction()
                        .replace(R.id.fcvDetails, FilterFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();
            } else if (item.getItemId() == R.id.itSettings) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fcvDetails, new SettingsFragment(), null)
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

    //region Load sites from room
    //These are broken up into 4 functions to make it easier on the database
    //Loads the data from the .db file
    public void loadManitobaHistoricalSiteData()
    {
        mDisposable.add(
                viewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadAllManitobaHistoricalSites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( manitobaHistoricalSites -> saveSitesToApp( manitobaHistoricalSites, false),
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
                        .subscribe( manitobaHistoricalSites -> saveSitesToApp( manitobaHistoricalSites, true),
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
                        .subscribe( manitobaHistoricalSites -> saveSitesToApp( manitobaHistoricalSites, false),
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
                        .subscribe( manitobaHistoricalSites -> saveSitesToApp( manitobaHistoricalSites, true),
                                throwable ->  Toast.makeText(getApplicationContext(), "Error fetching data with specified filters", Toast.LENGTH_SHORT).show()
                        )
        );
    }
    //endregion Load sites from room

    //Signals that the sites are loaded and stored in variable list
    public void saveSitesToApp (List<ManitobaHistoricalSite> sites, Boolean goToFirst )
    {
        try {
            allManitobaHistoricalSites.clear();

            allManitobaHistoricalSites = sites;

            int total = allManitobaHistoricalSites.size();
            Toast.makeText(getApplicationContext(), "Found all " + allManitobaHistoricalSites.size() + " historic sites in Manitoba", Toast.LENGTH_SHORT).show();
            if (mMap != null)
            {
                addSiteListToMap(allManitobaHistoricalSites);
                if (goToFirst && sites.size() > 0)
                {
                    ManitobaHistoricalSite firstSite = allManitobaHistoricalSites.get(0);
                    viewModel.setCurrentSite(firstSite);
                    //moveCameraToLocation(firstSite.getLocation());
                }

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

            //Removed the camera bounds, as it got annoying when looking at sites near the border
            /*LatLngBounds manitobaBounds = new LatLngBounds(
                    new LatLng(48, -102), // SW bounds
                    new LatLng(60, -89)  // NE bounds
            );

            mMap.setLatLngBoundsForCameraTarget(manitobaBounds);*/

            // Set Map colour from preference
            updateSetDisplayColours(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.night_mode_key), false));

            enableMyLocation();
            if (getUserLocation() != null) {
                LatLng current = new LatLng(getUserLocation().getLatitude(), getUserLocation().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, cameraZoom));
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
                Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(site.getLatitude(), site.getLongitude())).title(site.getName()).snippet(site.getAddress())
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.  	HUE_YELLOW 	 	))
                );
                newMarker.setTag(site.getSite_id());
                allMarkers.add(newMarker);

            }
            Toast.makeText(getApplicationContext(), "All sites have been added to the map", Toast.LENGTH_SHORT).show();

            //Stops app from loading data twice when opened
            firstAppLoad = false;

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
                        .addToBackStack(getString(R.string.site_fragment)) // name can be null
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

            FragmentContainerView  mapView = (FragmentContainerView) findViewById(R.id.fcvMap);
            FragmentContainerView detailView = (FragmentContainerView) findViewById(R.id.fcvDetails);

            //Sets the layout_weight for the active fragment container
            switch (displayMode)
            {
                case FullMap:
                    mapWeight = 1;
                    mapView.setVisibility(View.VISIBLE);
                    detailView.setVisibility(View.GONE);
                    break;
                case FullSiteDetail:
                    detailWeight = 1;
                    mapView.setVisibility(View.GONE);
                    detailView.setVisibility(View.VISIBLE);
                    break;

                case SiteAndMap:
                    mapWeight = Float.parseFloat(getString(R.string.display_both_map));
                    detailWeight = Float.parseFloat(getString(R.string.display_both_details));
                    mapView.setVisibility(View.VISIBLE);
                    detailView.setVisibility(View.VISIBLE);
                    break;

            }






            try {


                LinearLayout.LayoutParams mapViewLayoutParams =  (LinearLayout.LayoutParams) mapView.getLayoutParams();
                LinearLayout.LayoutParams detailViewParams =  (LinearLayout.LayoutParams) detailView.getLayoutParams();
                mapViewLayoutParams.weight = mapWeight;
                detailViewParams.weight = detailWeight;
                mapView.setLayoutParams(mapViewLayoutParams);
                detailView.setLayoutParams(detailViewParams);

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

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
            //Toast.makeText(getApplicationContext(), "Updating Display Settings", Toast.LENGTH_SHORT).show();
            String value = "";

            if (key.equals(getString(R.string.night_mode_key)))
            {
                value = String.valueOf( sharedPreferences.getBoolean(key, false));
                updateSetDisplayColours(sharedPreferences.getBoolean(key, false));
            } else if (key.equals(getString(R.string.background_colour_key))) {
                value = String.valueOf( sharedPreferences.getString(key, "#ffffff"));
                updateBackgroundColour(sharedPreferences.getString(key, "#ffffff"));
            } else if (key.equals(getString(R.string.text_colour_key))) {
                value = String.valueOf( sharedPreferences.getString(key, "#ffffff"));
                updateTextColour(sharedPreferences.getString(key, "#000000"));
            }
            Toast.makeText(getApplicationContext(), "Updating Display Settings. Key: " + key + ", Value: " + value, Toast.LENGTH_LONG).show();


        }
    };



    private void updateSetDisplayColours(Boolean nightMode)
    {

        try {
            //Default Style is day
            int mapStyleId = R.raw.day_mode_map_style_json;

            if (nightMode)
            {
                mapStyleId = R.raw.night_mode_map_style_json;
            }

            if (mMap != null)
            {
                boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapStyleId));
                if (!success)
                {
                    Log.e("Error", "onNightModeChange: Style parsing failed");
                }
            }

        }
        catch (Exception e)
        {
            Log.e("Error", "updateSetDisplayColours: error updating display colours" + e.getMessage());
        }
    }

    private void updateBackgroundColour(String colour)
    {
        try {
            int [] layoutIds = {R.id.root, R.id.tbMain, R.id.llDisplaySpacing, R.id.fcvDetails};
            for (int id: layoutIds ) {
                findViewById(id).setBackgroundColor(Color.parseColor(colour));

            }
        }catch (Exception e)
        {
            Log.e("Error", "updateBackgroundColour: error updating display colours" + e.getMessage());
        }


    }

    private void updateTextColour(String colour)
    {
        try {


            mToolbar.setTitleTextColor(Color.parseColor(colour));
            mToolbar.setSubtitleTextColor(Color.parseColor(colour));
            ((TextView) findViewById(R.id.tvAppbarTitle)).setTextColor(Color.parseColor(colour));
        }catch (Exception e)
        {
            Log.e("Error", "updateTextColour: error updating display colours" + e.getMessage());
        }


    }

    //region User Location
    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMap == null && !firstAppLoad)
            loadMap();
        if (trackingLocation)
            enableMyLocation();
        if (mMap != null && getUserLocation() != null)
        {
            LatLng current = new LatLng(getUserLocation().getLatitude(), getUserLocation().getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, cameraZoom));
        }
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

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