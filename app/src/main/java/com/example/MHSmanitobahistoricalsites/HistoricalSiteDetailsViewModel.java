package com.example.MHSmanitobahistoricalsites;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.MHSmanitobahistoricalsites.Database.HistoricalSiteDatabase;
import com.example.MHSmanitobahistoricalsites.Database.ManitobaHistoricalSite;
import com.example.MHSmanitobahistoricalsites.HolderClasses.DisplayMode;
import com.example.MHSmanitobahistoricalsites.HolderClasses.SiteFilter;

import java.util.List;

public class HistoricalSiteDetailsViewModel extends ViewModel {

    private MutableLiveData<ManitobaHistoricalSite> currentSite;
    private MutableLiveData<Location> currentLocation;
    private MutableLiveData<DisplayMode>  displayMode;
    private MutableLiveData<SiteFilter> siteFiler;

    private SavedStateHandle state;

    private HistoricalSiteDatabase historicalSiteDatabase;
    private MutableLiveData<Boolean> searched;
    private MutableLiveData<List<Float>> markerColours;




    public HistoricalSiteDetailsViewModel(SavedStateHandle state)
    {
        this.state = state;
        this.currentSite = new MutableLiveData<ManitobaHistoricalSite>();
        this.currentLocation = new MutableLiveData<Location>();
        this.displayMode = new MutableLiveData<DisplayMode>();
        this.siteFiler = new MutableLiveData<SiteFilter>();
        this.searched = new MutableLiveData<Boolean>();
        this.markerColours = new MutableLiveData<List<Float>>();
    }



    public void setHistoricalSiteDatabase(Context context) {
        this.historicalSiteDatabase = HistoricalSiteDatabase.getInstance(context);
    }






    public HistoricalSiteDatabase getHistoricalSiteDatabase() {
        return historicalSiteDatabase;
    }

    public MutableLiveData<ManitobaHistoricalSite> getCurrentSite() {
        return currentSite;
    }

    public void setCurrentSite(ManitobaHistoricalSite site)
    {
        this.currentSite.setValue(site);
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation .setValue(currentLocation);
    }

    public MutableLiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public MutableLiveData< DisplayMode> getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode( DisplayMode  displayMode)
    {
        this.displayMode.setValue( displayMode);
    }

    public MutableLiveData<SiteFilter> getSiteFilters() {
        return siteFiler;
    }

    public void setSiteFilters(SiteFilter siteFiler)
    {
        this.siteFiler.setValue(siteFiler);
    }

    public MutableLiveData<Boolean> getSearched() {
        return searched;
    }

    public void setSearched(Boolean searched)
    {
        this.searched.setValue(searched);
    }
    public MutableLiveData<List<Float>> getMarkerColours() {
        return markerColours;
    }

    public void setMarkerColours(List<Float> markerColours)
    {
        this.markerColours.setValue(markerColours);
    }

}