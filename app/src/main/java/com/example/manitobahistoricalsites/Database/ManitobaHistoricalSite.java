package com.example.manitobahistoricalsites.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ManitobaHistoricalSite {
    @PrimaryKey
    public int site_id;
    public String address;
    public double latitude;
    public double longitude;
    public String province;
    public String municipality;
    public String description;
    public String site_url;
    public String import_date;

    public ManitobaHistoricalSite(int site_id, String address, double latitude, double longitude, String province, String municipality, String description, String site_url, String import_date) {
        this.site_id = site_id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
        this.municipality = municipality;
        this.description = description;
        this.site_url = site_url;
        this.import_date = import_date;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSite_url() {
        return site_url;
    }

    public void setSite_url(String site_url) {
        this.site_url = site_url;
    }

    public String getImport_date() {
        return import_date;
    }

    public void setImport_date(String import_date) {
        this.import_date = import_date;
    }
}
