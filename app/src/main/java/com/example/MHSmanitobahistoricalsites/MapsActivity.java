package com.example.MHSmanitobahistoricalsites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MHSmanitobahistoricalsites.Database.ManitobaHistoricalSite;
import com.example.MHSmanitobahistoricalsites.HolderClasses.DisplayMode;
import com.example.MHSmanitobahistoricalsites.HolderClasses.SiteClusterItem;
import com.example.MHSmanitobahistoricalsites.HolderClasses.SiteClusterRenderer;
import com.example.MHSmanitobahistoricalsites.HolderClasses.SiteFilter;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;



public class MapsActivity extends AppCompatActivity
        implements  OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback
{

    private GoogleMap mMap;

    private List<ManitobaHistoricalSite> allManitobaHistoricalSites;


    private Toolbar mToolbar;
    private boolean cameraFollow = false;


    LocationManager locationManager;
    private HistoricalSiteDetailsViewModel viewModel;
    private FragmentManager fragmentManager;


    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private boolean trackingLocation;
    private boolean permissionDenied = false;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private final float cameraZoom = 16;
    private final float searchZoom = 18;

    SharedPreferences prefs;

    //Stop app from loading data twice when app is launched
    boolean firstAppLoad = true;

    //HashMap<Integer, Float> markerColoursPerType;
    //List<Float> markerColours;
    private ClusterManager<SiteClusterItem> mClusterManager;


    FragmentContainerView  mapFragmentContainerView;
    FragmentContainerView detailFragmentContainerView;
    FragmentContainerView otherFragmentContainerView;
    LinearLayout llLegend;









    public MapsActivity(){
        super(R.layout.activity_maps);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fragmentManager = getSupportFragmentManager();




        allManitobaHistoricalSites = new ArrayList<>();



        mToolbar = findViewById(R.id.tbMain);
        setSupportActionBar(mToolbar);


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
        viewModel.setSearched(false);
        viewModel.setHistoricalSiteDatabase(getApplicationContext());
        viewModel.getCurrentSite().observe(this, changedSite -> {
            try {
                if (changedSite == null)
                {
                    viewModel.setDisplayMode(DisplayMode.FullMap);
                }
                else
                {
                    moveCameraToLocation(changedSite.getLocation(), viewModel.getSearched().getValue(), changedSite);
                    viewModel.setCurrentLocation(getUserLocation());
                    viewModel.setDisplayMode(DisplayMode.SiteAndMap);



                    //displayMarkerInfo(currentSite.getSite_id());
                }


            } catch (Exception e) {
                Log.e("Error", "UpdateCurrentPositonToBeCurrentSite: Error updating the map to reflect the viewmodel\n" + e.getMessage());
            }


        });


        viewModel.getDisplayMode().observe(this, displayMode -> updateDisplayHeight(displayMode));


        //Set up filters on view model
        viewModel.setSiteFilters(new SiteFilter());
        viewModel.getSiteFilters().observe(this, siteFilter -> {
            try {
                //No filter
                //Needed to check to make sure it doesn't run when app is first loaded
                if (siteFilter.getMunicipalityFilter().size() == 0 && siteFilter.getSiteTypeFilter().size() == 0 && !firstAppLoad )
                {
                    loadManitobaHistoricalSiteData();
                }
                //Only municipality filter
                else if (siteFilter.getMunicipalityFilter().size() > 0 && siteFilter.getSiteTypeFilter().size() == 0){
                    loadManitobaHistoricalSiteDataMunicipalityFilter(siteFilter.getMunicipalityFilter());
                }
                //Only type filter
                else if (siteFilter.getMunicipalityFilter().size() == 0 && siteFilter.getSiteTypeFilter().size() > 0) {
                    loadManitobaHistoricalSiteDataTypeFilter(siteFilter.getSiteTypeFilter());
                }
                // Get both municipality and type filter
                else if (siteFilter.getMunicipalityFilter().size() > 0 && siteFilter.getSiteTypeFilter().size() > 0) {
                    loadManitobaHistoricalAllFilter(siteFilter.getMunicipalityFilter(), siteFilter.getSiteTypeFilter());
                }
            } catch (Exception e) {
                Log.e("Error", "updateDataToComplyWithNewFilters: Error updating the map to reflect the viewmodel\n" + e.getMessage());
            }
        });

        llLegend = (LinearLayout) findViewById(R.id.llLegend);
        mapFragmentContainerView = (FragmentContainerView) findViewById(R.id.fcvMap);
        detailFragmentContainerView = (FragmentContainerView) findViewById(R.id.fcvDetails);
        otherFragmentContainerView = (FragmentContainerView) findViewById(R.id.fcvOther);
        fragmentManager.beginTransaction()
                .replace(R.id.fcvDetails, HistoricalSiteDetailsFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
        viewModel.setDisplayMode(DisplayMode.FullMap);
        viewModel.setMarkerColours(new ArrayList<>());

        //Setting Up Preference values
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        updateBackgroundColour(prefs.getString(getString(R.string.background_colour_key), "#FFFFFF"));
        updateTextColour(prefs.getString(getString(R.string.text_colour_key), "#000000"));
        updateMarkerColoursPerType(prefs);

        llLegend.setOnClickListener(v -> fragmentManager.beginTransaction()

                .replace(R.id.fcvOther, LegendFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // name can be null
                .commit());





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
                    moveCameraToLocation(getUserLocation(), false, null);
                }
            } else if (item.getItemId() == R.id.itAbout) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fcvOther, AboutFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();


            } else if (item.getItemId() == R.id.itFilters) {
                fragmentManager.popBackStack(getString(R.string.site_fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.fcvOther, FilterFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();
            } else if (item.getItemId() == R.id.itSearch) {
                fragmentManager.beginTransaction()

                        .replace(R.id.fcvOther, SearchFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();
            }

            else if (item.getItemId() == R.id.itSettings) {
                fragmentManager.beginTransaction()

                        .replace(R.id.fcvOther, new SettingsFragment(), null)
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
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
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
                                throwable ->   Log.e("Error", "loadManitobaHistoricalSiteData: Error fetching data\n" + throwable.getMessage())  //Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
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
    public void loadManitobaHistoricalSiteDataTypeFilter(List<Integer> siteTypes)
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
    public void loadManitobaHistoricalAllFilter( List<String> municipalities, List<Integer> siteTypes)
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

            Toast.makeText(getApplicationContext(), "Found all " + allManitobaHistoricalSites.size() + " historic sites in Manitoba", Toast.LENGTH_SHORT).show();
            if (mMap != null)
            {
                addSiteListToMap(allManitobaHistoricalSites);
                if (goToFirst && sites.size() > 0)
                {
                    ManitobaHistoricalSite firstSite = allManitobaHistoricalSites.get(0);
                    moveCameraToLocation(firstSite.getLocation(),  false, null);
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
           // mMap.setOnMarkerClickListener(this);
            //mMap.getUiSettings().setMapToolbarEnabled(false);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);

            //Removed the camera bounds, as it got annoying when looking at sites near the border
            /*LatLngBounds manitobaBounds = new LatLngBounds(
                    new LatLng(48, -102), // SW bounds
                    new LatLng(60, -89)  // NE bounds
            );

            mMap.setLatLngBoundsForCameraTarget(manitobaBounds);*/

            // Set Map colour from preference
            updateMapDisplayColour(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.night_mode_key), false));

            //Enable location. If user location isn't null, move camera to user
            enableMyLocation();
            if (getUserLocation() != null) {
                LatLng current = new LatLng(getUserLocation().getLatitude(), getUserLocation().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, cameraZoom));
                viewModel.getCurrentLocation().setValue(getUserLocation());

            }

            //Sets up Cluster Manager
            mClusterManager = new ClusterManager<SiteClusterItem>(getApplicationContext(), mMap);

            //Need to set the marker onclick listener in the cluster manager
            mClusterManager.setOnClusterItemClickListener(item -> {
                try {
                    //If it is a new site, update current site
                    if (viewModel.getCurrentSite().getValue() != item.getHistoricalSite())
                        viewModel.setCurrentSite(item.getHistoricalSite());
                }
                catch (Exception e)
                {
                    Log.e("Error", "onClusterItemClick: Error getting new site\n" + e.getMessage());
                }
                return false;
            });



            //mClusterManager.setAnimation(false);
            updateClusterManagerRender(Integer.parseInt(prefs.getString(getString(R.string.min_cluster_key), "20")));
            mClusterManager.setAnimation(prefs.getBoolean(getString(R.string.cluster_animation_key), false));


            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mClusterManager.setAlgorithm(new NonHierarchicalViewBasedAlgorithm<SiteClusterItem>(metrics.widthPixels, metrics.heightPixels));


            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);

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
            mClusterManager.clearItems();
            List<Float> markerColours = viewModel.getMarkerColours().getValue();



            for (ManitobaHistoricalSite site: sitesToAdd) {
                Float markerColour = 0f;
//                if (markerColoursPerType.get(site.getMain_type()) != null)
//                    markerColour = markerColoursPerType.get(site.getMain_type());
                if (site.getMain_type() > 0 && site.getMain_type() <= markerColours.size())
                    markerColour = markerColours.get((site.getMain_type() -1));
                /*Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(site.getLatitude(), site.getLongitude())).title(site.getName()).snippet(site.getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColour))
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.  	HUE_YELLOW 	 	))
                );
                newMarker.setTag(site.getSite_id());
                allMarkers.add(newMarker);*/


                SiteClusterItem newItem = new SiteClusterItem(site, markerColour );
                mClusterManager.addItem(newItem);


            }
            mClusterManager.cluster();

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
    //No longer works, as clustering means only a few markers are created at a time
    //Now works, as I implemented a delay after the camera moves to allow time for markers to load
    private void displayMarkerInfo(ManitobaHistoricalSite searchedSite)
    {
        try {


            if (mClusterManager.getMarkerCollection().getMarkers().size() > 0)
           {

               //Collection<Marker> markers = mClusterManager.getMarkerCollection().getMarkers();

               for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
                   /*String makerName = marker.getTitle();
                   String siteName = searchedSite.getName();
                   String markerAddress = marker.getSnippet();
                   LatLng testPosition = marker.getPosition();
                   double markerLatitude = testPosition.latitude;
                   double siteLatitude = searchedSite.getLatitude();
                   double markerLongitude = testPosition.longitude;
                   double siteLongitude = searchedSite.getLongitude();*/

                   if (marker.getTitle().equals(searchedSite.getName()) && marker.getPosition().latitude == searchedSite.getLatitude() && marker.getPosition().longitude == searchedSite.getLongitude()) { //if a marker has desired tag
                       marker.showInfoWindow();
                       break;

                    }
               }
            }
       }
      catch (Exception e)
        {
            Log.e("Error", "Error getting marker info\n" + e.getMessage());
            //Toast.makeText(getApplicationContext(), "Error getting marker info", Toast.LENGTH_SHORT).show();
       }

   }





    //Moves camera to new location
    private void moveCameraToLocation(Location newLocation, boolean searched, ManitobaHistoricalSite searchedSite)
    {
        try {
            LatLng userLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
            if (searched)
            {
                viewModel.setSearched(false);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, searchZoom));
                Handler handler = new Handler();
                handler.postDelayed(() -> displayMarkerInfo(searchedSite), 1500);

            }
            else
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
            float otherWeight = 0;
            otherFragmentContainerView.setVisibility(View.GONE);



            //Sets the layout_weight for the active fragment container
            switch (displayMode)
            {
                case FullMap:
                    mapWeight = 1;
                    mapFragmentContainerView.setVisibility(View.VISIBLE);
                    detailFragmentContainerView.setVisibility(View.GONE);
                   // llLegend.setVisibility(View.VISIBLE);
                    break;
                case FullDetail:
                    detailWeight = 1;
                    mapFragmentContainerView.setVisibility(View.GONE);
                    detailFragmentContainerView.setVisibility(View.VISIBLE);
                    llLegend.setVisibility(View.GONE);
                    break;

                case SiteAndMap:
                    mapWeight = Float.parseFloat(prefs.getString(getString(R.string.map_weight_key), getString(R.string.display_both_map)));
                    detailWeight =  Float.parseFloat(prefs.getString(getString(R.string.details_weight_key), getString(R.string.display_both_details)));
                    mapFragmentContainerView.setVisibility(View.VISIBLE);
                    detailFragmentContainerView.setVisibility(View.VISIBLE);
                    llLegend.setVisibility(View.GONE);
                    break;

                case Other:
                    otherWeight = 1;
                    mapFragmentContainerView.setVisibility(View.GONE);
                    detailFragmentContainerView.setVisibility(View.GONE);
                    otherFragmentContainerView.setVisibility(View.VISIBLE);
                    llLegend.setVisibility(View.GONE);

            }






            try {


                LinearLayout.LayoutParams mapViewLayoutParams =   (LinearLayout.LayoutParams) mapFragmentContainerView.getLayoutParams();
                LinearLayout.LayoutParams detailViewParams =  (LinearLayout.LayoutParams) detailFragmentContainerView.getLayoutParams();
                LinearLayout.LayoutParams otherViewParams =  (LinearLayout.LayoutParams) otherFragmentContainerView.getLayoutParams();
                mapViewLayoutParams.weight = mapWeight;
                detailViewParams.weight = detailWeight;
                otherViewParams.weight = otherWeight;
                mapFragmentContainerView.setLayoutParams(mapViewLayoutParams);
                detailFragmentContainerView.setLayoutParams(detailViewParams);
                otherFragmentContainerView.setLayoutParams(otherViewParams);

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

    //Shared Preference on change listener, allows user to customize the site colours. All of it will be removed when I get a colour scheme I'm happy with
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
            //Toast.makeText(getApplicationContext(), "Updating Display Settings", Toast.LENGTH_SHORT).show();
            if (key != null)
            {
                if (key.equals(getString(R.string.night_mode_key)))
                {
                    updateMapDisplayColour(sharedPreferences.getBoolean(key, false));
                } else if (key.equals(getString(R.string.background_colour_key))) {
                    updateBackgroundColour(sharedPreferences.getString(key, "#ffffff"));
                } else if (key.equals(getString(R.string.secondary_colour_key))) {
                    updateSecondaryColour(sharedPreferences.getString(key, "#000000"));
                } else if (key.equals(getString(R.string.text_colour_key))) {
                    updateTextColour(sharedPreferences.getString(key, "#000000"));
                } else if (key.equals(getString(R.string.update_marker_key))) {
                    if (sharedPreferences.getBoolean(getString(R.string.update_marker_key), false))
                    {
                        updateMarkerColoursPerType(sharedPreferences);
                        if (mMap != null)
                        {
                            addSiteListToMap(allManitobaHistoricalSites);
                        }


                    }

                } else if (key.equals(getString(R.string.min_cluster_key))) {
                    updateClusterManagerRender(Integer.parseInt(sharedPreferences.getString(key, "20")));
                } else if (key.equals(getString(R.string.cluster_animation_key))) {
                    if (mClusterManager != null)
                        mClusterManager.setAnimation(prefs.getBoolean(getString(R.string.cluster_animation_key), false));
                }
            }
        }
    };

    //Updates the minimum cluster size
    public void updateClusterManagerRender(int min)
    {
        if (mClusterManager != null)
        {
            SiteClusterRenderer customRender = new SiteClusterRenderer(this, mMap, mClusterManager);
            customRender.setMinClusterSize(min);
            mClusterManager.setRenderer(customRender);
        }
    }



    private void updateMapDisplayColour(Boolean nightMode)
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
            Log.e("Error", "updateMapDisplayColour: error updating display colours" + e.getMessage());
        }
    }

    //Sets Background olour
    private void updateBackgroundColour(String colour)
    {
        try {
            int [] layoutIds = {R.id.root, R.id.tbMain, R.id.llDisplaySpacing, R.id.fcvOther, R.id.llLegendBackground};
            for (int id: layoutIds ) {
                findViewById(id).setBackgroundColor(Color.parseColor(colour));

            }
        }catch (Exception e)
        {
            Log.e("Error", "updateBackgroundColour: error updating display colours" + e.getMessage());
        }


    }

    //Sets secondary colour
    private void updateSecondaryColour(String colour)
    {
        try {
            int [] layoutIds = {R.id.llLegend};
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
            ((TextView) findViewById(R.id.tvLegend)).setTextColor(Color.parseColor(colour));
        }catch (Exception e)
        {
            Log.e("Error", "updateTextColour: error updating display colours" + e.getMessage());
        }


    }

    //Updates the hashmap markerColoursPerType
    private void updateMarkerColoursPerType(SharedPreferences sharedPreferences)
    {

        try {
            String [] siteTypes = getResources().getStringArray(R.array.Site_Types);
            String [] defaultColour = getResources().getStringArray(R.array.Default_Colour_Values);
            List<Float> markerColours = new ArrayList<>();
            //markerColours.clear();
            //markerColoursPerType.clear();

            for (String type : siteTypes) {
                //Default value for listPreference doesn't work on first install, so I made a work around using an array resource
                String colour = sharedPreferences.getString(type, defaultColour[markerColours.size()]);
                Float colourValue = Float.valueOf(colour);
                //markerColoursPerType.put(type, colourValue);
                markerColours.add(colourValue);

            }
            viewModel.setMarkerColours(markerColours);
        }catch (Exception e)
        {
            Log.e("Error", "updateMarkerColoursPerType: error updating display colours" + e.getMessage());
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

        //Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //Sets how often the app checks if the user location has changed
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
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


        //The default location is the Manitoba Museum, which is returned if there is an error getting location
        Location userLocation = new Location("");
        userLocation.setLatitude(49.9000253);
        userLocation.setLongitude(-97.1386276);

        //If user location is enabled, get the user location. Else get default location (which is The Manitoba Museum)
        if(trackingLocation) {
            try {
                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();

                // Getting the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);
                if (provider != null)
                {
                    // Getting Current Location
                    @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
                    userLocation = location;
                }


            } catch (Exception e) {
                Toast.makeText(this, "Error fetching location. Please make sure to enable location in your settings.", Toast.LENGTH_LONG).show();
                Log.e("Error", "getUserLocation: Error fetching user location\n" + e.getMessage());
            }
        }



        return userLocation;

    }

    private final LocationCallback locationCallback = new LocationCallback() {
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
                            moveCameraToLocation(newLocation, false, null);
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