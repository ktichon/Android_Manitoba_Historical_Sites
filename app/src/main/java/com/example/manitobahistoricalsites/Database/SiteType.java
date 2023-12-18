package com.example.manitobahistoricalsites.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SiteType {
    @PrimaryKey
    public int siteType_id;
    public String type;
    public String import_date;

    public SiteType(int siteType_id, String type, String import_date) {
        this.siteType_id = siteType_id;
        this.type = type;
        this.import_date = import_date;
    }

    public int getSiteType_id() {
        return siteType_id;
    }

    public void setSiteType_id(int siteType_id) {
        this.siteType_id = siteType_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImport_date() {
        return import_date;
    }

    public void setImport_date(String import_date) {
        this.import_date = import_date;
    }
}
