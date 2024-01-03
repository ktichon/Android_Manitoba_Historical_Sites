package com.example.MHSmanitobahistoricalsites.HolderClasses;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class SiteClusterRenderer extends DefaultClusterRenderer<SiteClusterItem> {
    public SiteClusterRenderer(Context context, GoogleMap map, ClusterManager<SiteClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull SiteClusterItem item, @NonNull MarkerOptions markerOptions) {

        markerOptions.title(item.getTitle()).snippet(item.getSnippet())
                .icon(BitmapDescriptorFactory.defaultMarker(item.getColour()));
        super.onBeforeClusterItemRendered(item, markerOptions);


    }

    @Override
    protected void onClusterItemUpdated(@NonNull SiteClusterItem item, @NonNull Marker marker) {

        marker.setTitle(item.getTitle());
        marker.setSnippet(item.getSnippet());
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(item.getColour()));
        marker.setTag(item.getSiteID());
        super.onClusterItemUpdated(item, marker);
    }

    @Override
    protected int getBucket(@NonNull Cluster<SiteClusterItem> cluster) {
        return cluster.getSize();
    }

    @NonNull
    @Override
    protected String getClusterText(int bucket) {
        return String.valueOf(bucket);
    }
}
