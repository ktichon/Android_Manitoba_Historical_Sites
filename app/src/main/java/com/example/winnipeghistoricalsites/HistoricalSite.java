package com.example.winnipeghistoricalsites;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;

public class HistoricalSite implements Parcelable {
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
