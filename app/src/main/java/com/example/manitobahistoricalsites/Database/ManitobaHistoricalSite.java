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
}
