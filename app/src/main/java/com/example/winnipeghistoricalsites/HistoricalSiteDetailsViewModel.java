package com.example.winnipeghistoricalsites;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HistoricalSiteDetailsViewModel extends ViewModel {

    private MutableLiveData<HistoricalSite> currentSite;

    public MutableLiveData<HistoricalSite> getCurrentSite() {
        if (currentSite == null) {
            currentSite = new MutableLiveData<HistoricalSite>();
        }
        return currentSite;
    }

}