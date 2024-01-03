package com.example.MHSmanitobahistoricalsites.HolderClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class SiteClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final int siteID;
    private float colour;

    public SiteClusterItem(LatLng position, String title, String snippet, int siteID, float colour) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.siteID = siteID;
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

    public int getSiteID() {
        return siteID;
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
