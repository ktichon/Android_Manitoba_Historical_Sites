package com.example.manitobahistoricalsites;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.manitobahistoricalsites.Database.HistoricalSiteDatabase;
import com.example.manitobahistoricalsites.Database.ManitobaHistoricalSite;

import java.util.List;

import io.reactivex.Maybe;

public class HistoricalSiteDetailsViewModel extends ViewModel {

    private MutableLiveData<ManitobaHistoricalSite> currentSite;
    private MutableLiveData<Location> currentLocation;
    private MutableLiveData<Boolean> fullScreen;
    private SavedStateHandle state;

    private HistoricalSiteDatabase historicalSiteDatabase;


    public HistoricalSiteDetailsViewModel(SavedStateHandle state)
    {
        this.state = state;
        this.currentSite = new MutableLiveData<ManitobaHistoricalSite>();
        this.currentLocation = new MutableLiveData<Location>();
        this.fullScreen = new MutableLiveData<Boolean>();
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

    public MutableLiveData<Boolean> getFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(Boolean fullScreen)
    {
        this.fullScreen.setValue(fullScreen);
    }
}