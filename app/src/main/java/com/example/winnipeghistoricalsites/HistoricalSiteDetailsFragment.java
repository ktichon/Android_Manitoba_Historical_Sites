package com.example.winnipeghistoricalsites;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.model.Place;

import kotlin.contracts.Returns;

public class HistoricalSiteDetailsFragment extends Fragment {

    private HistoricalSiteDetailsViewModel mViewModel;
    private LinearLayout llDetails;
    private LinearLayout llDisplayInfo;
    //private LinearLayout llPlaceInfo;
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

    private LinearLayout llWebView;
    private GestureDetector mDetector;

    private static final String SITE_KEY = "current_historical_site_yehaw";

    //Allows historical site to be passed in away the allows back-button
    public static HistoricalSiteDetailsFragment newInstance(HistoricalSite site) {
        Bundle args = new Bundle();
        args.putParcelable(SITE_KEY, site);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        mViewModel = new ViewModelProvider(getActivity()).get(HistoricalSiteDetailsViewModel.class);

        //currentSite = mViewModel.getCurrentSite().getValue();
        currentSite  = (HistoricalSite) getArguments().getParcelable(SITE_KEY);
        if (mViewModel.getCurrentSite().getValue() != currentSite)
        {
            mViewModel.setCurrentSite(currentSite);
        }

        queue = Volley.newRequestQueue(mainView.getContext());
        llDisplayInfo = mainView.findViewById(R.id.Details);
        llDisplayInfo.setVisibility(View.GONE);





        /*llPlaceInfo = mainView.findViewById(R.id.llPlaceInformation);
        llPlaceInfo.setVisibility(View.GONE);*/

       /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager = (LocationManager) mainView.getContext().getSystemService(LOCATION_SERVICE);*/


        //Set up Gesture listener

        mDetector = new GestureDetector(mainView.getContext(), new MyGestureListener());
        llDetails = mainView.findViewById(R.id.Details);
        llDetails.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return mDetector.onTouchEvent(motionEvent);
            }
        });


        //Set button presses

        btnShort = (Button) mainView.findViewById(R.id.btnShortLink);
        btnShort.setVisibility(View.VISIBLE);
        btnShort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //openWebPage(currentSite.shortUrl);
                setWebViewContent(currentSite.getShortUrl());
            }
        });


        btnLong = (Button) mainView.findViewById(R.id.btnLongLink);
        btnLong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //openWebPage(currentSite.longUrl);
                setWebViewContent(currentSite.getLongUrl());
            }
        });

        btnGoogle = (Button) mainView.findViewById(R.id.btnGoogleLink);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String googleSearch  = "https://www.google.com/search?q=" + currentSite.getCity() + "+" + currentSite.getAddress().replace(" ", "+") + "+" + currentSite.getName().replace(" ", "+");
                openWebPage(googleSearch);
            }
        });

        btnDirections = (ImageButton) mainView.findViewById(R.id.btnDirections);
        btnDirections.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //String origin = "origin=" + currentLocation.getLatitude()+","+ currentLocation.getLongitude();
                String destination = "destination=";

                destination += currentSite.getLocation().getLatitude() + "," + currentSite.getLocation().getLongitude();
                String alternatives = "alternatives=false";

                String departureTime = "departure_time=now";
                String mode = "mode=driving";
                String units = "units=metric";
                String directionUrl = "https://www.google.com/maps/dir/?api=1&" + destination + "&" + alternatives + "&" + departureTime + "&" + mode + "&" + units;
                //String directionUrl = "google.navigation:q=" + currentSite.location.getLatitude() + "," + currentSite.location.getLongitude();
                Uri gmmIntentUri = Uri.parse(directionUrl);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

               /* FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fcvDetails, HistoricalSiteDirectionsFragment.class, null);
                ft.setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();*/
               //getDirectionsApi(currentSite);
            }
        });


        // Updates the "# away" textbox whenever the location changes
        mViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                try{
                    Float distance = currentSite.getLocation().distanceTo(location) ;
                    String distanceText = (distance >= 1000? String.format("%.2f",distance/1000) + " km": String.format("%.2f",distance) + " m");
                    //String distanceText = String.format("%.2f",distance) + " m";
                    //((TextView) mainView.findViewById(R.id.tvDistance)).setText(distanceText + " away");
                    ((TextView) mainView.findViewById(R.id.tvAddress)).setText(currentSite.getAddress() + ", " + distanceText + " away");
                } catch (Exception e)
                {
                    Log.e("Error", "updateDistanceAway: Error updating user distance from the site\n" + e.getMessage());
                }


            }
        } );
        /*mViewModel.getCurrentSite().observe(getViewLifecycleOwner(), display -> {
            // Update the list UI
        });*/
        setLlDisplayInfo(currentSite, mViewModel.getCurrentDisplayHeight().getValue());






    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        try {
            super.onPause();

        } catch (Exception e)
        {
            Log.e("Error", "Display Details On Pause:" + e.getMessage());
            super.onPause();
        }

    }

    //Opens the web view activity and display the short or long link
    public void openWebPage(String url) {
        //url = "https://developer.android.com/reference/android/webkit/WebView";
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(mainView.getContext(), "There is no addition information about the historic site " + currentSite.getName() + " in this app.", Toast.LENGTH_SHORT).show();
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

    public void setLlDisplayInfo(HistoricalSite site, DisplayHeight displayHeight) {
        //llPlaceInfo.setVisibility(View.GONE);
        llDisplayInfo.setVisibility(View.VISIBLE);
        TextView nameAndBuildDate = mainView.findViewById(R.id.tvNameAndBuild);
        String buildDate = (TextUtils.isEmpty(site.getConstructionDate())? "": " (" + site.getConstructionDate() + ")");
        nameAndBuildDate.setText(site.getName() + buildDate);
        ((TextView) mainView.findViewById(R.id.tvAddress)).setText(site.getAddress());
        /*if(displayHeight == DisplayHeight.SMALL)
        {
            ((LinearLayout) mainView.findViewById(R.id.llWebView)).setVisibility(View.GONE);
            ((LinearLayout) mainView.findViewById(R.id.llMoreInfo)).setVisibility(View.GONE);

        }
        else*/
        setSmall(displayHeight);




            //if links are null, hide more info button
            btnShort.setVisibility((TextUtils.isEmpty(site.getShortUrl())? View.GONE: View.VISIBLE));
            btnLong.setVisibility((TextUtils.isEmpty(site.getLongUrl())? View.GONE: View.VISIBLE));


            llWebView = mainView.findViewById(R.id.llWebView);
            if(TextUtils.isEmpty(site.getShortUrl()))
            {
                llWebView.setVisibility(View.GONE);
            }
            else
            {
                setWebViewHeight(displayHeight);
                setWebViewContent(site.getShortUrl());
                /*int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                Double maxHeightPercent = Double.parseDouble(displayHeight == DisplayHeight.MEDIUM? getString(R.string.medium_max_height_of_webview_percent): getString(R.string.full_max_height_of_webview_percent) );


                //int maxHeight = (int)(screenHeight * Double.parseDouble( getString(R.string.medium_max_height_of_webview_percent)));
                int maxHeight = (int)(screenHeight * maxHeightPercent);
                ViewGroup.LayoutParams params = llWebView.getLayoutParams();
                params.height = maxHeight;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                llWebView.setLayoutParams(params);

                WebView webView = (WebView) mainView.findViewById(R.id.wvInfo);
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setSupportZoom(true);
                webView.setInitialScale(200);
                //String pdf = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";

                try {
                    //webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + site.shortUrl);
                    https://docs.google.com/gview?embedded=true&url=
                    webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + site.shortUrl);
                    llWebView.setVisibility(View.VISIBLE);
                    //webView.loadUrl(site.shortUrl);
                } catch (Error e)
                {
                    Toast.makeText(mainView.getContext(), "Error fetching more data:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    llWebView.setVisibility(View.GONE);
                }*/


            //diplayPlaceInfo(site);
        }


    }

    //Not displaying the place info anymore, as it doesn't provide enough information
    //Displays information from the Place google API
   /* public void diplayPlaceInfo(HistoricalSite site)
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
                *//*List<PhotoMetadata> allPhotos = sitePlace.getPhotoMetadatas();
                if (allPhotos != null)
                    Toast.makeText(mainView.getContext(), allPhotos.size() + " photos found" , Toast.LENGTH_SHORT).show();*//*
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


    //Sets text view data if it isn't null, else hide the textview
    private void setTextView(int viewId, String viewText)
    {
        TextView textView = mainView.findViewById(viewId);
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

    //if the display is smoll, display no links
    private void setSmall(DisplayHeight displayHeight)
    {
//        Boolean result = false;
//       // ((LinearLayout) mainView.findViewById(R.id.llWebView)).setVisibility(View.VISIBLE);
//        ((LinearLayout) mainView.findViewById(R.id.llExtendedInfo)).setVisibility(View.VISIBLE);
//        if(displayHeight == DisplayHeight.SMALL)
//        {
//            //((LinearLayout) mainView.findViewById(R.id.llWebView)).setVisibility(View.GONE);
//            ((LinearLayout) mainView.findViewById(R.id.llExtendedInfo)).setVisibility(View.GONE);
//            result = true;
//        }
        LinearLayout llExtendedInfo =  (LinearLayout) mainView.findViewById(R.id.llExtendedInfo);
        llExtendedInfo.setVisibility(displayHeight == DisplayHeight.SMALL? View.GONE: View.VISIBLE);



    }

    private void setWebViewHeight(DisplayHeight displayHeight)
    {
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        Double maxHeightPercent = Double.parseDouble(displayHeight == DisplayHeight.MEDIUM? getString(R.string.medium_max_height_of_webview_percent): getString(R.string.full_max_height_of_webview_percent) );


        //int maxHeight = (int)(screenHeight * Double.parseDouble( getString(R.string.medium_max_height_of_webview_percent)));
        int maxHeight = (int)(screenHeight * maxHeightPercent);
        llWebView = mainView.findViewById(R.id.llWebView);
        ViewGroup.LayoutParams params = llWebView.getLayoutParams();
        params.height = maxHeight;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        llWebView.setLayoutParams(params);
    }

    private void setWebViewContent(String siteURL)
    {
        llWebView = mainView.findViewById(R.id.llWebView);
        WebView webView = (WebView) mainView.findViewById(R.id.wvInfo);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.setInitialScale(200);
        //String pdf = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";

        try {
            llWebView.setVisibility(View.VISIBLE);
            //webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + site.shortUrl);
            //   https://docs.google.com/gview?embedded=true&url=
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + siteURL);

            //webView.loadUrl(site.shortUrl);
        } catch (Error e)
        {
            Toast.makeText(mainView.getContext(), "Error fetching more data:" + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Error", "setWebViewContent: Error fetching  url into webView \n" +  e.getMessage());
            llWebView.setVisibility(View.GONE);
        }
    }

    /*private void updateDisplaySize(DisplayHeight displayHeight, HistoricalSite site)
    {

        if(!setSmall(displayHeight) && !TextUtils.isEmpty(site.getShortUrl()) && site.getShortUrl() != null)
            setWebViewHeight(displayHeight);
    }*/


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener

    {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {



            return true;

        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Boolean result = true;
            DisplayHeight displayHeight = mViewModel.getCurrentDisplayHeight().getValue();
            try {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD &&  Math.abs(distanceY) > Math.abs(distanceX))
                {
                    //To make sure that the newHeight has a default value
                    DisplayHeight newHeight = displayHeight;
                    if (distanceY > 0) {
                        newHeight = (displayHeight == DisplayHeight.FULL? DisplayHeight.MEDIUM: DisplayHeight.SMALL);
                    } else {
                        newHeight = (displayHeight == DisplayHeight.SMALL? DisplayHeight.MEDIUM: DisplayHeight.FULL);
                    }
                    if(displayHeight != newHeight)
                    {
                        mViewModel.setCurrentDisplayHeight(newHeight);
                        setSmall(newHeight);
                        if (newHeight != DisplayHeight.SMALL)
                            setWebViewHeight(newHeight);
                    }


                    //setLlDisplayInfo(currentSite,newHeight);
                    /*if (distanceY > 0) {
                        setLlDisplayInfo(currentSite,false);
                    } else {
                        setLlDisplayInfo(currentSite,true);
                    }*/



                }

            } catch (Exception e) {
                Log.e("Error", "MyGestureListener: Error when implementing gestures\n" + e.getMessage());
                result = false;
            }

            return result;
        }
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
            String directionUrl = getString(R.string.directions_Api_Link) + origin + "&" + destination + "&" + alternatives + "&" + departureTime + "&" + mode + "&" + units + "&key=" + getString(R.string.google_maps_key);

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