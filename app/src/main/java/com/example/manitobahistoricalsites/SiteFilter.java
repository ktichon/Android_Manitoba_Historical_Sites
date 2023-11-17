package com.example.manitobahistoricalsites;

import java.util.ArrayList;
import java.util.List;

//Class that stores the active site filters
public class SiteFilter {

    boolean allMunicipalities;
    boolean allSiteTypes;

    List<String> municipalityFilter;
    List<String> siteTypeFilter;

    public SiteFilter(List<String> municipalityFilter, List<String> siteTypeFilter) {
        this.municipalityFilter = municipalityFilter;
        this.siteTypeFilter = siteTypeFilter;
        this.allMunicipalities = false;
        this.allSiteTypes = false;
    }

    public SiteFilter() {
        this.municipalityFilter = new ArrayList<>();
        this.siteTypeFilter = new ArrayList<>();
        this.allMunicipalities = true;
        this.allSiteTypes = true;
        
    }

    public boolean isAllMunicipalities() {
        return allMunicipalities;
    }

    public void setAllMunicipalities(boolean allMunicipalities) {
        this.allMunicipalities = allMunicipalities;
    }

    public boolean isAllSiteTypes() {
        return allSiteTypes;
    }

    public void setAllSiteTypes(boolean allSiteTypes) {
        this.allSiteTypes = allSiteTypes;
    }



    public List<String> getMunicipalityFilter() {
        return municipalityFilter;
    }

    public void setMunicipalityFilter(List<String> municipalityFilter) {
        this.municipalityFilter = municipalityFilter;
    }

    public List<String> getSiteTypeFilter() {
        return siteTypeFilter;
    }

    public void setSiteTypeFilter(List<String> siteTypeFilter) {
        this.siteTypeFilter = siteTypeFilter;
    }


}
