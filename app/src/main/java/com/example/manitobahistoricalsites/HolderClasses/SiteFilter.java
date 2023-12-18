package com.example.manitobahistoricalsites.HolderClasses;

import java.util.ArrayList;
import java.util.List;

//Class that stores the active site filters
public class SiteFilter {



    List<String> municipalityFilter;
    List<Integer> siteTypeFilter;

    public SiteFilter(List<String> municipalityFilter, List<Integer> siteTypeFilter) {
        this.municipalityFilter = municipalityFilter;
        this.siteTypeFilter = siteTypeFilter;
    }

    public SiteFilter() {
        this.municipalityFilter = new ArrayList<>();
        this.siteTypeFilter = new ArrayList<>();
        
    }


    public List<String> getMunicipalityFilter() {
        return municipalityFilter;
    }

    public void setMunicipalityFilter(List<String> municipalityFilter) {
        this.municipalityFilter = municipalityFilter;
    }

    public List<Integer> getSiteTypeFilter() {
        return siteTypeFilter;
    }

    public void setSiteTypeFilter(List<Integer> siteTypeFilter) {
        this.siteTypeFilter = siteTypeFilter;
    }


}
