package com.example.manitobahistoricalsites.Database;

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
}
