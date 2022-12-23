package com.example.winnipeghistoricalsites;

import static android.content.Context.LOCATION_SERVICE;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

public class HistoricalSiteDetailsFragment extends Fragment {

    private HistoricalSiteDetailsViewModel mViewModel;
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
    private RequestQueue queue;
    View mainView;

    private static final String SITE_KEY = "current_historical_site_yehaw";

    //Allows historical site to be passed in away the allows back-button
    public static HistoricalSiteDetailsFragment newInstance(HistoricalSite site) {
        Bundle args = new Bundle();
        args.putSerializable(SITE_KEY, site);
        HistoricalSiteDetailsFragment fragment = new HistoricalSiteDetailsFragment();
        fragment.setArguments(args);
        return fragment;

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.historical_site_details_fragment, container, false);
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoricalSiteDetailsViewModel.class);
        currentSite = mViewModel.getCurrentSite().getValue();

    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        mViewModel = new ViewModelProvider(getActivity()).get(HistoricalSiteDetailsViewModel.class);

        //currentSite = mViewModel.getCurrentSite().getValue();
        currentSite  = (HistoricalSite) getArguments().getSerializable(SITE_KEY);
        if (mViewModel.getCurrentSite().getValue() != currentSite)
        {
            mViewModel.setCurrentSite(currentSite);
        }

        queue = Volley.newRequestQueue(mainView.getContext());
        llDisplayInfo = mainView.findViewById(R.id.Details);
        llDisplayInfo.setVisibility(View.GONE);
        llPlaceInfo = mainView.findViewById(R.id.llPlaceInformation);
        llPlaceInfo.setVisibility(View.GONE);

       /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager = (LocationManager) mainView.getContext().getSystemService(LOCATION_SERVICE);*/


        //Set button presses

        btnShort = (Button) mainView.findViewById(R.id.btnShortLink);
        btnShort.setVisibility(View.VISIBLE);
        btnShort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openWebPage(currentSite.shortUrl);
            }
        });


        btnLong = (Button) mainView.findViewById(R.id.btnLongLink);
        btnLong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openWebPage(currentSite.longUrl);
            }
        });

        btnGoogle = (Button) mainView.findViewById(R.id.btnGoogleLink);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String googleSearch  = "https://www.google.com/search?q=" + currentSite.city + "+" + currentSite.address().replace(" ", "+") + "+" + currentSite.name.replace(" ", "+");
                openWebPage(googleSearch);
            }
        });

        btnDirections = (ImageButton) mainView.findViewById(R.id.btnDirections);
        btnDirections.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               //getDirectionsApi(currentSite);
            }
        });


        // Updates the "# away" textbox whenever the location changes
        mViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                try{
                    Float distance = currentSite.location.distanceTo(location) ;
                    String distanceText = (distance >= 1000? String.format("%.2f",distance/1000) + " km": String.format("%.2f",distance) + " m");
                    //String distanceText = String.format("%.2f",distance) + " m";
                    ((TextView) mainView.findViewById(R.id.tvDistance)).setText(distanceText + " away");
                } catch (Exception e)
                {
                    Log.e("Error", "updateDistanceAway: Error updating user distance from the site\n" + e.getMessage());
                }


            }
        } );
        /*mViewModel.getCurrentSite().observe(getViewLifecycleOwner(), display -> {
            // Update the list UI
        });*/
        setLlDisplayInfo(currentSite);
    }


    //Opens the web view activity and display the short or long link
    public void openWebPage(String url) {
        //url = "https://developer.android.com/reference/android/webkit/WebView";
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(mainView.getContext(), "There is no addition information about the historic site " + currentSite.name + " in this app.", Toast.LENGTH_SHORT).show();
        } else {
            /*Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);*/

            /*Intent intent = new Intent(getApplicationContext(), WebviewActivity.class);
            intent.putExtra(getString(R.string.webviewUrl), url);
            startActivity(intent);*/

            //Better and easier to pull up link in browser than in my own webview
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);


        }
    }

    public void setLlDisplayInfo(HistoricalSite site) {
        llDisplayInfo.setVisibility(View.VISIBLE);
        TextView nameAndBuildDate = mainView.findViewById(R.id.tvNameAndBuild);
        String buildDate = (TextUtils.isEmpty(site.constructionDate)? "": " (" + site.constructionDate + ")");
        nameAndBuildDate.setText(site.name + buildDate);
        ((TextView) mainView.findViewById(R.id.tvAddress)).setText(site.address());

        //if links are null, hide more info button
        btnShort.setVisibility((TextUtils.isEmpty(site.shortUrl)? View.GONE: View.VISIBLE));
        btnLong.setVisibility((TextUtils.isEmpty(site.longUrl)? View.GONE: View.VISIBLE));
    }


    /*//Gets direction infromation from the google directions api
    public void getDirectionsApi(HistoricalSite site)
    {
        Location userLocation = mViewModel.getCurrentLocation();
        if (userLocation != null)
        {
            String origin = "origin=" + userLocation.getLatitude()+","+ userLocation.getLongitude();
            String destination = "destination=";
            if (site.placeId != null)
                destination += "place_id:" + site.placeId;
            else
                destination += site.location.getLatitude() + "," + site.location.getLongitude();
            String alternatives = "alternatives=false";

            String departureTime = "departure_time=now";
            String mode = "mode=driving";
            String units = "units=metric";
            String directionUrl = getString(R.string.directions_Api_Link) + origin + "&" + destination + "&" + alternatives + "&" + departureTime + "&" + mode + "&" + units + "&key=" + getString(R.string.google_maps_additions_key);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, directionUrl, null, new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject response) {



                            try {
                                String status = response.getString("status");
                                if (status.equals( "OK"))
                                {
                                    //JSONArray routesArray = response.getJSONArray("routes");
                                    JSONObject routesObject = response.getJSONArray("routes").getJSONObject(0);
                                    JSONObject overViewPolyLineJson = routesObject.getJSONObject("overview_polyline");
                                    String summary = routesObject.getString("summary");
                                    JSONObject legObject = routesObject.getJSONArray("legs").getJSONObject(0);
                                    JSONObject durationObject = legObject.getJSONObject("duration_in_traffic");
                                    String duration = durationObject.getString("text");



                                    Log.i("info", routesObject.toString());


                                    try{
                                        PolylineOptions directionLine = new PolylineOptions();


                                    }catch (Exception e)
                                    {
                                        Log.e("Error", "getDirectionsApi: Error parcing data\n" + e.getMessage());
                                    }
                                }
                                else {
                                    Log.e("Error", "getDirectionsApi: Result Status = " + status);
                                    Toast.makeText(mainView.getContext(), "There was an issue getting directions", Toast.LENGTH_SHORT).show();
                                }





                            }catch (Exception e) {
                                Log.e("Error", "getDirectionsApi: Error extracting Direction data\n" + e.getMessage());
                            }

                        }
                    }, getJsonError);
            queue.add(jsonObjectRequest);}
        else {
            Toast.makeText(mainView.getContext(), "Please make sure that you have enabled us to access your location.", Toast.LENGTH_LONG).show();
        }
    }*/
}