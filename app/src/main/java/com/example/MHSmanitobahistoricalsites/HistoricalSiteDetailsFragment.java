package com.example.MHSmanitobahistoricalsites;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MHSmanitobahistoricalsites.Database.ManitobaHistoricalSite;
import com.example.MHSmanitobahistoricalsites.Database.SitePhotos;
import com.example.MHSmanitobahistoricalsites.Database.SiteSource;
import com.example.MHSmanitobahistoricalsites.Database.SiteType;
import com.example.MHSmanitobahistoricalsites.HolderClasses.DisplayMode;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HistoricalSiteDetailsFragment extends Fragment {

    private HistoricalSiteDetailsViewModel mViewModel;
    private LinearLayout llDetailsContainer;


    View mainView;
    private GestureDetector mDetector;



    private final CompositeDisposable mDisposable = new CompositeDisposable();

    SharedPreferences prefs;

    DisplayMode currentSiteDisplayMode = DisplayMode.FullMap;
    Boolean firstLoad = true;


    TextView tvName;
    TextView tvAddress;
    TextView tvDescription;
    TextView tvHistoricalSocietyLink;
    TextView tvTypes;
    ViewPager2 photoViewPager;
    TextView tvSourceInfo;
    NestedScrollView nsvMoreInfo;
    TextView tvShowMoreInfo;
    TextView tvDistance;

    LinearLayout llDetails;
    LinearLayout llShowMoreInfo;
    AppCompatButton btnClose;

    TextView tvNoPhotos;





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
        mViewModel = new ViewModelProvider(requireActivity()).get(HistoricalSiteDetailsViewModel.class);


        llDetailsContainer = mainView.findViewById(R.id.llDetailsContainer);
        llDetailsContainer.setVisibility(View.GONE);



        //Set up Gesture listener

        mDetector = new GestureDetector(mainView.getContext(), new MyGestureListener());
        llDetails = mainView.findViewById(R.id.llDetails);
        llDetails.setOnTouchListener((view1, motionEvent) -> mDetector.onTouchEvent(motionEvent));









        //Set up way to expand and collapse the more info (without swiping)
        llShowMoreInfo = mainView.findViewById(R.id.llShowMore);

        llShowMoreInfo.setOnClickListener(v -> {
            DisplayMode oldDisplayMode = mViewModel.getDisplayMode().getValue();

            //If Display is Full Site, then set to Site and Map. Else the display must already be Site and Map, so set it to Full Site
            DisplayMode newDisplaymode = (oldDisplayMode == DisplayMode.FullDetail ? DisplayMode.SiteAndMap: DisplayMode.FullDetail);

            setCurrentSiteDisplayMode(newDisplaymode);
            setSmall(newDisplaymode);

        });


        //Close button, on click sets the map fragment to full screen
        btnClose = mainView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            mViewModel.setCurrentSite(null);
            /*FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack(getString(R.string.site_fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);*/

            //mViewModel.setCurrentSite(null);
        });



        //Might as well get these text fields once, instead of every time a new site is loaded
        tvName = mainView.findViewById(R.id.tvName);
        tvAddress = mainView.findViewById(R.id.tvAddress);
        tvDescription = mainView.findViewById(R.id.tvDescription);
        tvHistoricalSocietyLink = mainView.findViewById(R.id.tvManitobaHistoricalSociety);
        tvTypes = mainView.findViewById(R.id.tvTypes);
        photoViewPager = mainView.findViewById(R.id.viewpager);
        tvSourceInfo = mainView.findViewById(R.id.tvSourceInfo);
        nsvMoreInfo = mainView.findViewById(R.id.nsvMoreInfo);
        tvShowMoreInfo = mainView.findViewById(R.id.tvShowMoreInfo);
        tvDistance = mainView.findViewById(R.id.tvDistance);
        tvNoPhotos = mainView.findViewById(R.id.tvNoPhotos);
        tvNoPhotos.setVisibility(View.GONE);











        // Updates the "# away" textbox whenever the location changes
        mViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> displaySiteDistance(location));

        mViewModel.getCurrentSite().observe(getViewLifecycleOwner(), site -> historicalSiteChanged(site));


        //Get Site info
        /*mDisposable.add(
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
                ));*/

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        updateBackgroundColour(prefs.getString(getString(R.string.background_colour_key), "#FFFFFF"));
        updateSecondaryColour(prefs.getString(getString(R.string.secondary_colour_key), "#000000" ));
        updateTextColour(prefs.getString(getString(R.string.text_colour_key), "#000000"));

    }

    //There is no reason to create a new fragment to the stack when a marker is clicked. It causes a lot of problems in navigation, all for the ability to press the back button and get the previous site.
    //A feature that will not even work on apple devices. Instead, there will only be one details fragment, and it will be updated when the new current site is selected.
    public void historicalSiteChanged(ManitobaHistoricalSite site){
        try {

            mDisposable.clear();
            if (site != null)
            {
                try {
                    displayHistoricalSiteInfo(site);
                    mDisposable.add(mViewModel.getHistoricalSiteDatabase().siteTypeDao().getAllSiteTypesForSite(site.getSite_id())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe( siteTypes -> displaySiteType( siteTypes),
                                    throwable ->  Toast.makeText(getContext(), "Error retrieving site types", Toast.LENGTH_SHORT).show()
                            ));

                    mDisposable.add(mViewModel.getHistoricalSiteDatabase().sitePhotosDao().getAllSitePhotosForSite(site.getSite_id())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe( sitePhotos -> displaySitePhoto( sitePhotos),
                                    throwable ->  Toast.makeText(getContext(), "Error retrieving site photos", Toast.LENGTH_SHORT).show()
                            ));

                    mDisposable.add(mViewModel.getHistoricalSiteDatabase().siteSourceDao().getAllSiteSourcesForSite(site.getSite_id())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe( siteSources -> displaySiteSources( siteSources),
                                    throwable ->  Toast.makeText(getContext(), "Error retrieving site sources", Toast.LENGTH_SHORT).show()
                            ));

                } catch (Exception e)
                {
                    Log.e("Error", "historicalSiteChanged GettingInfo: Error updating info for new site\n" + e.getMessage());
                }
            }


        }
        catch (Exception e)
        {
            Log.e("Error", "historicalSiteChanged: Error updating info for new site\n" + e.getMessage());
        }
    }


    //Gets and displays info for Manitoba Historical Site
    public void displayHistoricalSiteInfo(ManitobaHistoricalSite site)
    {
        try {

            llDetailsContainer.setVisibility(View.VISIBLE);
            tvName.setText(site.getName());
            displaySiteDistance(mViewModel.getCurrentLocation().getValue());

            //Some sites don't have an address, only coordinates. This is to make sure that the  ", " only shows up if the site has an address
            String address = site.getAddress() == null || site.getAddress().trim().isEmpty()? "":  site.getAddress() + ", ";
            address += site.getMunicipality();
            tvAddress.setText(address);
            setSmall(mViewModel.getDisplayMode().getValue());

            //Hopefully makes the description more readable
            String formattedDescription = site.getDescription().replace("\n", "\n\n"); //.replace("\n", "<br>");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvDescription.setText(Html.fromHtml(formattedDescription, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
            } else {
                tvDescription.setText(Html.fromHtml(formattedDescription));
            }

            tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
            tvHistoricalSocietyLink.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(site.getSite_url()))));
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
                StringBuilder allTypes = new StringBuilder();
                for (SiteType type: siteTypes) {
                    allTypes.append(type.getType()).append("/");
                }
                String displayTypes = allTypes.substring(0, allTypes.length() - 1).replace("%2F", " or ");

                tvTypes.setText(displayTypes);


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
        try {
            if (sitePhotos.size()> 0)
            {
                tvNoPhotos.setVisibility(View.GONE);
                photoViewPager.setVisibility(View.VISIBLE);
                SiteImagesAdapter adapter = new SiteImagesAdapter(sitePhotos, photoViewPager, getContext());
                photoViewPager.setAdapter(adapter);

                //Page height can vary, this re updates the height when a new image is shown
                photoViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        if (firstLoad)
                            firstLoad = false;
                        else
                            updateViewPagerHeight(page, photoViewPager);
                    }
                });
            }
            else
            {
                tvNoPhotos.setVisibility(View.VISIBLE);
                photoViewPager.setVisibility(View.GONE);
            }



        }
        catch (Exception e)
        {
            Log.e("Error", "displaySitePhoto: Error displaying site photos\n" + e.getMessage());
        }
    }

    //Updates the pager height to always fit the new photo
    private void updateViewPagerHeight(View view, ViewPager2 pager) {
        try {
            view.post(new Runnable() {
                @Override
                public void run() {
                    int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
                    int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    view.measure(wMeasureSpec, hMeasureSpec);
                    if (pager.getLayoutParams().height != view.getMeasuredHeight()) {
                        ViewGroup.LayoutParams layout = pager.getLayoutParams();
                        layout.height = view.getMeasuredHeight();
                        pager.setLayoutParams(layout);

                    }
                }

            });
        }
        catch (Exception e)
        {
            Log.e("Error", "updateViewPagerHeight: Error updating the height\n" + e.getMessage());
        }

    }



    //Displays the site sources
    public void displaySiteSources(List<SiteSource> siteSources)
    {
        try {
            StringBuilder displaySource = new StringBuilder();
            for (SiteSource source: siteSources)
            {
                displaySource.append(source.getInfo()).append("<br><br>");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvSourceInfo.setText(Html.fromHtml(displaySource.toString(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvSourceInfo.setText(Html.fromHtml(displaySource.toString()));
            }
            tvSourceInfo.setMovementMethod(LinkMovementMethod.getInstance());
        }
        catch (Exception e)
        {
            Log.e("Error", "displaySitePhoto: Error displaying site photos\n" + e.getMessage());
        }
    }

    //if the display is smoll, hide the nested scroll view (that contains photos, description, and sources)
    private void setSmall(DisplayMode displayMode)
    {
        nsvMoreInfo.setVisibility(displayMode == DisplayMode.FullDetail ? View.VISIBLE : View.GONE );
        nsvMoreInfo.scrollTo(0,0);
        tvShowMoreInfo.setText(displayMode == DisplayMode.FullDetail ?  R.string.show_less : R.string.show_more);
        int showInfoArrow = displayMode == DisplayMode.FullDetail ? R.drawable.arrow_down : R.drawable.arrow_up;
        tvShowMoreInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, showInfoArrow, 0);
    }

    //Displays site distance from the user location
    private void displaySiteDistance(Location userLocation)
    {
        try{
            ManitobaHistoricalSite currentSite = mViewModel.getCurrentSite().getValue();
            if (userLocation != null && currentSite != null)
            {
                float distance = currentSite.getLocation().distanceTo(userLocation) ;
                //If distance is >= 1000, display kilometers. Else display meters.
                String distanceText = (distance >= 1000? String.format( Locale.CANADA, "%.2f",distance/1000) + " km": String.format(Locale.CANADA,"%.2f",distance) + " m") + " away";
                tvDistance.setText( distanceText );
            }

        } catch (Exception e)
        {
            Log.e("Error", "displaySiteDistance: Error updating user distance from the site\n" + e.getMessage());
        }
    }


    //Allows resume to resume to correct size
    private void setCurrentSiteDisplayMode(DisplayMode newMode)
    {
        this.currentSiteDisplayMode = newMode;
        mViewModel.setDisplayMode(newMode);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //mViewModel.setDisplayMode(previousDisplayMode);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (currentSite != null)
        {
            mViewModel.setDisplayMode(currentSiteDisplayMode);
            displayHistoricalSiteInfo(currentSite);
        }*/
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);


    }


    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = (sharedPreferences, key) -> {
        //Toast.makeText(getApplicationContext(), "Updating Display Settings", Toast.LENGTH_SHORT).show();
        if (key != null)
        {
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
            int [] layoutIds = {R.id.llDetails, R.id.llInsideScrollView};
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


            int [] layoutIds = {R.id.tvName, R.id.tvTypes, R.id.tvAddress, R.id.tvDistance, R.id.tvShowMoreInfo, R.id.tvDescription, R.id.tvSourceTitle, R.id.tvSourceInfo, R.id.tvNoPhotos};
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
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        //Allows user to swipe up or down to expand/collapse the more info
        @Override
        public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            boolean result = true;
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
                        newDisplayMode = DisplayMode.FullDetail;
                    }

                    if(newDisplayMode != oldDisplayMode)
                    {
                        setCurrentSiteDisplayMode(newDisplayMode);
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