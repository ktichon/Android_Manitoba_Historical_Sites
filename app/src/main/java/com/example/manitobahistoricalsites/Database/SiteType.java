package com.example.manitobahistoricalsites.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SiteType {
    @PrimaryKey
    public int siteType_id;
    public int site_id;
    public String type;
    public String import_date;

    public SiteType(int siteType_id, int site_id, String type, String import_date) {
        this.siteType_id = siteType_id;
        this.site_id = site_id;
        this.type = type;
        this.import_date = import_date;
    }
}
