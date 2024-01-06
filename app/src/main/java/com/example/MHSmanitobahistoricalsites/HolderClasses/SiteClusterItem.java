package com.example.MHSmanitobahistoricalsites.HolderClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.MHSmanitobahistoricalsites.Database.ManitobaHistoricalSite;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class SiteClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final ManitobaHistoricalSite historicalSite;
    private float colour;

    public SiteClusterItem( ManitobaHistoricalSite historicalSite, float colour) {
        this.position = new LatLng(historicalSite.getLatitude(), historicalSite.getLongitude());
        this.title = historicalSite.getName();
        //this.snippet = historicalSite.getAddress() + (historicalSite.getAddress() == null? " ": ", ") + historicalSite.getMunicipality();
        this.snippet = (historicalSite.getAddress() == null || historicalSite.getAddress().trim().isEmpty()? "":  historicalSite.getAddress() + ", ") + historicalSite.getMunicipality();
        this.historicalSite = historicalSite;
        this.colour = colour;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }


    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public ManitobaHistoricalSite getHistoricalSite() {
        return historicalSite;
    }

    public float getColour() {
        return colour;
    }

    public void setColour(float colour) {
        this.colour = colour;
    }

    @Nullable
    @Override
    public Float getZIndex() {
        return null;
    }
}
