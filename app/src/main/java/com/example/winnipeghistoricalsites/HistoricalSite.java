package com.example.winnipeghistoricalsites;

import android.location.Location;

public class HistoricalSite {
    public String name;
    public String streetName;
    public String streetNumber;
    public String constructionDate;
    public String shortUrl;
    public String longUrl;
    public Location location;

    public HistoricalSite(String name, String streetName, String streetNumber, String constructionDate, String dateListed, String shortUrl, String longUrl, Location location) {
        this.name = name;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.constructionDate = constructionDate;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.location = location;
    }

    public HistoricalSite(String name) {
        this(name, null, null, null, null, null, null, null);
    }
    public String address()
    {
        return this.streetNumber + " " + this.streetName;
    }
    /*public String displayInfo()
    {
        return address() + "\nConstruction Date: " + this.constructionDate + "\nMore info" + this.shortUrl;
    }
    */

}
