package com.example.manitobahistoricalsites.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SitePhotos {
    @PrimaryKey
    public int photo_id;

    public int site_id;
    public String photo_name;
    public String photo_url;
    public String info;
    public String import_date;

    public SitePhotos(int photo_id, int site_id, String photo_name, String photo_url, String info, String import_date) {
        this.photo_id = photo_id;
        this.site_id = site_id;
        this.photo_name = photo_name;
        this.photo_url = photo_url;
        this.info = info;
        this.import_date = import_date;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImport_date() {
        return import_date;
    }

    public void setImport_date(String import_date) {
        this.import_date = import_date;
    }
}
