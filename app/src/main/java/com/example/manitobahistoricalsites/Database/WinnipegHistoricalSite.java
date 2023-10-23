package com.example.manitobahistoricalsites.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WinnipegHistoricalSite {
    @PrimaryKey
    public int site_id;
    public String name;
    public String streetName;
    public String streetNumber;
    public String constructionDate;
    public String shortUrl;
    public String longUrl;
    public double latitude;
    public double longitude;
    public String city;
    public String province;
    public String import_date;

    public WinnipegHistoricalSite(int site_id, String name, String streetName, String streetNumber, String constructionDate, String shortUrl, String longUrl, double latitude, double longitude, String city, String province, String import_date) {
        this.site_id = site_id;
        this.name = name;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.constructionDate = constructionDate;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.province = province;
        this.import_date = import_date;
    }
}
