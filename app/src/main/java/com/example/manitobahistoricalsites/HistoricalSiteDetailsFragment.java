package com.example.manitobahistoricalsites;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manitobahistoricalsites.Database.ManitobaHistoricalSite;
import com.example.manitobahistoricalsites.Database.SitePhotos;
import com.example.manitobahistoricalsites.Database.SiteSource;
import com.example.manitobahistoricalsites.Database.SiteType;
import com.example.manitobahistoricalsites.HolderClasses.DisplayMode;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HistoricalSiteDetailsFragment extends Fragment {

    private HistoricalSiteDetailsViewModel mViewModel;
    private LinearLayout llDetails;
    private LinearLayout llDisplayInfo;


    //Declared as long click links to Manitoba Historical Society Site
    private TextView tvName;

    //Declared to allow user to click to expand/collapse more info
    private TextView tvShowMoreInfo;


    View mainView;
    private GestureDetector mDetector;

    private ManitobaHistoricalSite currentSite;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private ViewPager2 viewPager2;

    private AppCompatButton btnClose;

    SharedPreferences prefs;


    private static final String SITE_KEY = "current_historical_site_yehaw";

    //Allows historical site to be passed in away the allows back-button
    public static HistoricalSiteDetailsFragment newInstance(int site_id) {
        Bundle args = new Bundle();
        args.putInt(SITE_KEY, site_id);
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


        int site_id  =  getArguments().getInt(SITE_KEY);

        llDisplayInfo = mainView.findViewById(R.id.Details);
        llDisplayInfo.setVisibility(View.GONE);



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


        //Set button presses to link to Manitoba Historical Society Site
        tvName = (TextView) mainView.findViewById(R.id.tvName);
        tvName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                openWebPage(currentSite.getSite_url());
                return true;
            }
        });



        //Set up way to expand and collapse the more info (without swiping)
        tvShowMoreInfo = (TextView) mainView.findViewById(R.id.tvShowMoreInfo);
        tvShowMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMode oldDisplayMode = mViewModel.getDisplayMode().getValue();

                //If Display is Full Site, then set to Site and Map. Else the display must already be Site and Map, so set it to Full Site
                DisplayMode newDisplaymode = (oldDisplayMode == DisplayMode.FullSiteDetail? DisplayMode.SiteAndMap: DisplayMode.FullSiteDetail);

                mViewModel.setDisplayMode(newDisplaymode);
                setSmall(newDisplaymode);

            }
        });

        //Close button, on click sets the map fragment to full screen
        btnClose = (AppCompatButton)   mainView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setDisplayMode(DisplayMode.FullMap);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                //mViewModel.setCurrentSite(null);
            }
        });









        // Updates the "# away" textbox whenever the location changes
        mViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                displaySiteDistance(location);
            }
        } );


        //Get Site info
        mDisposable.add(
                mViewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().getManitobaHistoricalSite(site_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( manitobaHistoricalSites -> displayHistoricalSiteInfo( manitobaHistoricalSites),
                                throwable ->  Toast.makeText(getContext(), "Error retrieving site data", Toast.LENGTH_SHORT).show()
                        ));


        mDisposable.add(mViewModel.getHistoricalSiteDatabase().siteTypeDao().getAllSiteTypesForSite(site_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( siteTypes -> displaySiteType( siteTypes),
                        throwable ->  Toast.makeText(getContext(), "Error retrieving site types", Toast.LENGTH_SHORT).show()
                ));

        mDisposable.add(mViewModel.getHistoricalSiteDatabase().sitePhotosDao().getAllSitePhotosForSite(site_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( sitePhotos -> displaySitePhoto( sitePhotos),
                        throwable ->  Toast.makeText(getContext(), "Error retrieving site photos", Toast.LENGTH_SHORT).show()
                ));

        mDisposable.add(mViewModel.getHistoricalSiteDatabase().siteSourceDao().getAllSiteSourcesForSite(site_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( siteSources -> displaySiteSources( siteSources),
                        throwable ->  Toast.makeText(getContext(), "Error retrieving site sources", Toast.LENGTH_SHORT).show()
                ));

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        updateBackgroundColour(prefs.getString(getString(R.string.background_colour_key), "#FFFFFF"));
        updateSecondaryColour(prefs.getString(getString(R.string.secondary_colour_key), "#000000" ));
        updateTextColour(prefs.getString(getString(R.string.text_colour_key), "#000000"));

    }


    //Gets and displays info for Manitoba Historical Site
    public void displayHistoricalSiteInfo(ManitobaHistoricalSite site)
    {
        try {
            currentSite = site;
            mViewModel.setCurrentSite(currentSite);
            llDisplayInfo.setVisibility(View.VISIBLE);
            tvName.setText(site.getName());
            displaySiteDistance(mViewModel.getCurrentLocation().getValue());

            //Some sites don't have an address, only coordinates. This is to make sure that the  ", " only shows up if the site has an address
            String address = site.getAddress() == null || site.getAddress().trim().isEmpty()? "":  site.getAddress() + ", ";
            ((TextView) mainView.findViewById(R.id.tvAddress)).setText(address + site.getMunicipality());
            setSmall(mViewModel.getDisplayMode().getValue());

            //Hopefully makes the description more readable
            String formattedDescription = site.getDescription().replace("\n", "\n\n");
            ((TextView) mainView.findViewById(R.id.tvDescription)).setText(formattedDescription);
        }
        catch (Exception e)
        {
            Log.e("Error", "displayHistoricalSiteInfo: Error displaying site info\n" + e.getMessage());
        }



    }

    //Gets and displays info for Manitoba Historical Site
    public void displaySiteType(List<SiteType> siteTypes)
    {
        try {
            if (siteTypes != null && siteTypes.size() > 0)
            {
                String allTypes = "";
                for (SiteType type: siteTypes) {
                    allTypes = allTypes  + type.getType() + "/";
                }
                String displayTypes = allTypes.substring(0, allTypes.length() - 1).replace("%2F", " or ");

                ((TextView) mainView.findViewById(R.id.tvTypes)).setText(displayTypes);


            }
        }
        catch (Exception e)
        {
            Log.e("Error", "displaySiteType: Error displaying site types\n" + e.getMessage());
        }
    }

    //Sets up the ViewPager to use the site photos
    public void displaySitePhoto(List<SitePhotos> sitePhotos)
    {
        viewPager2 = mainView.findViewById(R.id.viewpager);
        try {
            viewPager2.setAdapter(new SiteImagesAdapter(sitePhotos, viewPager2, getContext()));
        }
        catch (Exception e)
        {
            Log.e("Error", "displaySitePhoto: Error displaying site photos\n" + e.getMessage());
        }
    }

    //Displays the site sources
    public void displaySiteSources(List<SiteSource> siteSources)
    {
        try {
            String displaySource = "";
            for (SiteSource source: siteSources)
            {
                displaySource  = displaySource + source.getInfo() + " \n\n";
            }
            ((TextView) mainView.findViewById(R.id.tvSourceInfo)).setText(displaySource);
        }
        catch (Exception e)
        {
            Log.e("Error", "displaySitePhoto: Error displaying site photos\n" + e.getMessage());
        }
    }


    //Opens the web view activity and display the short or long link
    public void openWebPage(String url) {
        //url = "https://developer.android.com/reference/android/webkit/WebView";
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(mainView.getContext(), "There is no addition information about the historic site " + currentSite.getName() + " in this app.", Toast.LENGTH_SHORT).show();
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);


        }
    }

    //if the display is smoll, display no links
    private void setSmall(DisplayMode displayMode)
    {
//
        NestedScrollView nsvMoreInfo =  (NestedScrollView) mainView.findViewById(R.id.nsvMoreInfo);
        nsvMoreInfo.setVisibility(displayMode == DisplayMode.FullSiteDetail? View.VISIBLE : View.GONE );
        tvShowMoreInfo.setText(displayMode == DisplayMode.FullSiteDetail?  R.string.show_less : R.string.show_more);
        int showInfoArrow = displayMode == DisplayMode.FullSiteDetail? R.drawable.arrow_down : R.drawable.arrow_up;
        tvShowMoreInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, showInfoArrow, 0);
    }

    //Displays site distance from the user location
    private void displaySiteDistance(Location userLocation)
    {
        try{
            if (userLocation != null && this.currentSite != null)
            {
                Float distance = this.currentSite.getLocation().distanceTo(userLocation) ;
                String distanceText = (distance >= 1000? String.format("%.2f",distance/1000) + " km": String.format("%.2f",distance) + " m");
                ((TextView) mainView.findViewById(R.id.tvDistance)).setText( distanceText + " away");
            }

        } catch (Exception e)
        {
            Log.e("Error", "displaySiteDistance: Error updating user distance from the site\n" + e.getMessage());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentSite != null)
        {
            mViewModel.setDisplayMode(DisplayMode.SiteAndMap);
            displayHistoricalSiteInfo(currentSite);
        }
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);


    }


    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
            //Toast.makeText(getApplicationContext(), "Updating Display Settings", Toast.LENGTH_SHORT).show();
             if (key.equals(getString(R.string.background_colour_key))) {
                updateBackgroundColour(sharedPreferences.getString(key, "#ffffff"));
            } else if (key.equals(getString(R.string.secondary_colour_key))) {
                 updateSecondaryColour(sharedPreferences.getString(key, "#000000"));
            } else if (key.equals(getString(R.string.text_colour_key))) {
                 updateTextColour(sharedPreferences.getString(key, "#000000"));
             }


        }
    };




    //Set background colour
    private void updateBackgroundColour(String colour)
    {
        try {
            int [] layoutIds = {R.id.Details, R.id.llInsideScrollView};
            for (int id: layoutIds ) {
                mainView.findViewById(id).setBackgroundColor(Color.parseColor(colour));
            }
        }catch (Exception e)
        {
            Log.e("Error", "updateBackgroundColour: error updating display colours" + e.getMessage());
        }


    }

    //Set secondary colour
    private void updateSecondaryColour(String colour)
    {
        try {
            int [] layoutIds = {R.id.llBar, R.id.nsvMoreInfo};
            for (int id: layoutIds ) {
                mainView.findViewById(id).setBackgroundColor(Color.parseColor(colour));
            }
        }catch (Exception e)
        {
            Log.e("Error", "updateBackgroundColour: error updating display colours" + e.getMessage());
        }


    }

    //Set text colour
    private void updateTextColour(String colour)
    {
        try {


            int [] layoutIds = {R.id.tvName, R.id.tvTypes, R.id.tvAddress, R.id.tvDistance, R.id.tvShowMoreInfo, R.id.tvDescription, R.id.tvSourceTitle, R.id.tvSourceInfo};
            for (int id: layoutIds ) {
                ((TextView) mainView.findViewById(id)).setTextColor(Color.parseColor(colour));

            }
        }catch (Exception e)
        {
            Log.e("Error", "updateTextColour: error updating display colours" + e.getMessage());
        }


    }




    class MyGestureListener extends GestureDetector.SimpleOnGestureListener

    {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        //Allows user to swipe up or down to expand/collapse the more info
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Boolean result = true;
            DisplayMode oldDisplayMode = mViewModel.getDisplayMode().getValue();
            try {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD &&  Math.abs(distanceY) > Math.abs(distanceX))
                {
                    //To make sure that the newHeight has a default value
                    DisplayMode newDisplayMode = oldDisplayMode;
                    if (distanceY > 0) {
                        newDisplayMode = DisplayMode.SiteAndMap;
                    } else {
                        newDisplayMode = DisplayMode.FullSiteDetail;
                    }

                    if(newDisplayMode != oldDisplayMode)
                    {
                        mViewModel.setDisplayMode(newDisplayMode);
                        setSmall(newDisplayMode);
                    }
                }
            } catch (Exception e) {
                Log.e("Error", "MyGestureListener: Error when implementing gestures\n" + e.getMessage());
                result = false;
            }

            return result;
        }
    }
}