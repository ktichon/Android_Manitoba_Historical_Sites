package com.example.winnipeghistoricalsites;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class HistoricalSiteDetailsViewModel extends ViewModel {

    private MutableLiveData<HistoricalSite> currentSite;
    private MutableLiveData<Location> currentLocation;
    private SavedStateHandle state;

    public HistoricalSiteDetailsViewModel(SavedStateHandle state)
    {
        this.state = state;
        currentSite = new MutableLiveData<HistoricalSite>();
        currentLocation = new MutableLiveData<Location>();
    }

    public MutableLiveData<HistoricalSite> getCurrentSite() {
        return currentSite;
    }

    public void setCurrentSite(HistoricalSite site)
    {
        this.currentSite.setValue(site);
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation .setValue(currentLocation);
    }

    public MutableLiveData<Location> getCurrentLocation() {
        return currentLocation;
    }
}