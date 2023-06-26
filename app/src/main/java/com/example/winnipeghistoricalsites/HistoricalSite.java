package com.example.winnipeghistoricalsites;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class HistoricalSite implements Parcelable {
    private static final AtomicInteger count = new AtomicInteger(0);
    private int id;

    private String name;
    private String streetName;
    private String streetNumber;
    private String constructionDate;
    private String shortUrl;
    private String longUrl;
    private Location location;
    private String city;
    private String province;
    private String placeId;
    private String googleAddress;
    private Place place;



    public HistoricalSite(String name, String streetName, String streetNumber, String constructionDate, String shortUrl, String longUrl, Location location, String city, String province) {
        this.id = count.incrementAndGet();
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



    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getConstructionDate() {
        return constructionDate;
    }

    public void setConstructionDate(String constructionDate) {
        this.constructionDate = constructionDate;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public HistoricalSite(String name) {
        this(name, null, null, null, null, null, null, null, null);
    }
    public String getAddress()
    {
        return getStreetNumber() + " " + this.getStreetName();
    }


    //For the search array adapter
    @NonNull
    @Override
    public String toString() {
        return getName() + ", " + getAddress();
    }


    /*public String displayInfo()
    {
        return address() + "\nConstruction Date: " + this.constructionDate + "\nMore info" + this.shortUrl;
    }
    */


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.streetName);
        dest.writeString(this.streetNumber);
        dest.writeString(this.constructionDate);
        dest.writeString(this.shortUrl);
        dest.writeString(this.longUrl);
        dest.writeParcelable(this.location, flags);
        dest.writeString(this.city);
        dest.writeString(this.province);
        dest.writeString(this.placeId);
        dest.writeString(this.googleAddress);
        dest.writeParcelable(this.place, flags);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.streetName = source.readString();
        this.streetNumber = source.readString();
        this.constructionDate = source.readString();
        this.shortUrl = source.readString();
        this.longUrl = source.readString();
        this.location = source.readParcelable(Location.class.getClassLoader());
        this.city = source.readString();
        this.province = source.readString();
        this.placeId = source.readString();
        this.googleAddress = source.readString();
        this.place = source.readParcelable(Place.class.getClassLoader());
    }

    protected HistoricalSite(Parcel in) {
        this.name = in.readString();
        this.streetName = in.readString();
        this.streetNumber = in.readString();
        this.constructionDate = in.readString();
        this.shortUrl = in.readString();
        this.longUrl = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.city = in.readString();
        this.province = in.readString();
        this.placeId = in.readString();
        this.googleAddress = in.readString();
        this.place = in.readParcelable(Place.class.getClassLoader());
    }

    public static final Parcelable.Creator<HistoricalSite> CREATOR = new Parcelable.Creator<HistoricalSite>() {
        @Override
        public HistoricalSite createFromParcel(Parcel source) {
            return new HistoricalSite(source);
        }

        @Override
        public HistoricalSite[] newArray(int size) {
            return new HistoricalSite[size];
        }
    };
}
