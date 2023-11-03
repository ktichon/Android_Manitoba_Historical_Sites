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
    private MutableLiveData<DisplayHeight> currentDisplayHeight;
    private SavedStateHandle state;

    private HistoricalSiteDatabase historicalSiteDatabase;


    public HistoricalSiteDetailsViewModel(SavedStateHandle state)
    {
        this.state = state;
        currentSite = new MutableLiveData<ManitobaHistoricalSite>();
        currentLocation = new MutableLiveData<Location>();
        currentDisplayHeight = new MutableLiveData<DisplayHeight>();
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

    public MutableLiveData<DisplayHeight> getCurrentDisplayHeight() {
        return currentDisplayHeight;
    }

    public void setCurrentDisplayHeight(DisplayHeight height)
    {
        this.currentDisplayHeight.setValue(height);
    }
}