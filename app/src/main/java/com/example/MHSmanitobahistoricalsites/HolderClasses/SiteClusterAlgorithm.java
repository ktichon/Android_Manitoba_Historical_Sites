package com.example.MHSmanitobahistoricalsites.HolderClasses;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.algo.Algorithm;

import java.util.Collection;
import java.util.Set;

public class SiteClusterAlgorithm implements Algorithm {
    @Override
    public boolean addItem(ClusterItem item) {
        return false;
    }

    @Override
    public boolean addItems(Collection items) {
        return false;
    }

    @Override
    public void clearItems() {

    }

    @Override
    public boolean removeItem(ClusterItem item) {
        return false;
    }

    @Override
    public boolean updateItem(ClusterItem item) {
        return false;
    }

    @Override
    public boolean removeItems(Collection items) {
        return false;
    }

    @Override
    public Set<? extends Cluster> getClusters(float zoom) {
        return null;
    }

    @Override
    public Collection getItems() {
        return null;
    }

    @Override
    public void setMaxDistanceBetweenClusteredItems(int maxDistance) {

    }

    @Override
    public int getMaxDistanceBetweenClusteredItems() {
        return 0;
    }

    @Override
    public void lock() {

    }

    @Override
    public void unlock() {

    }
}
