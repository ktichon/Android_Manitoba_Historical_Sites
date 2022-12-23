package com.example.winnipeghistoricalsites;

import android.location.Location;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;

public class HistoricalSite implements Serializable {
    public String name;
    public String streetName;
    public String streetNumber;
    public String constructionDate;
    public String shortUrl;
    public String longUrl;
    public Location location;
    public String city;
    public String province;
    public String placeId;
    public String googleAddress;
    public Place place;

    public HistoricalSite(String name, String streetName, String streetNumber, String constructionDate, String shortUrl, String longUrl, Location location, String city, String province) {
        this.name = name;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.constructionDate = constructionDate;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.location = location;
        this.city = city;
        this.province = province;
    }

    public HistoricalSite(String name) {
        this(name, null, null, null, null, null, null, null, null);
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
