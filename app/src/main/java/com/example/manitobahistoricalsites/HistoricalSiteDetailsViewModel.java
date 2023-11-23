package com.example.manitobahistoricalsites;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.manitobahistoricalsites.Database.HistoricalSiteDatabase;
import com.example.manitobahistoricalsites.Database.ManitobaHistoricalSite;
import com.example.manitobahistoricalsites.HolderClasses.DisplayMode;
import com.example.manitobahistoricalsites.HolderClasses.SiteFilter;

public class HistoricalSiteDetailsViewModel extends ViewModel {

    private MutableLiveData<ManitobaHistoricalSite> currentSite;
    private MutableLiveData<Location> currentLocation;
    private MutableLiveData<DisplayMode>  displayMode;
    private MutableLiveData<SiteFilter> siteFiler;

    private SavedStateHandle state;

    private HistoricalSiteDatabase historicalSiteDatabase;




    public HistoricalSiteDetailsViewModel(SavedStateHandle state)
    {
        this.state = state;
        this.currentSite = new MutableLiveData<ManitobaHistoricalSite>();
        this.currentLocation = new MutableLiveData<Location>();
        this.displayMode = new MutableLiveData<DisplayMode>();
        this.siteFiler = new MutableLiveData<SiteFilter>();
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
}