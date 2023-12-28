package com.example.MHSmanitobahistoricalsites.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SiteType {
    @PrimaryKey
    public int site_type_id;
    public String type;
    public String import_date;

    public SiteType(int site_type_id, String type, String import_date) {
        this.site_type_id = site_type_id;
        this.type = type;
        this.import_date = import_date;
    }

    public int getSite_type_id() {
        return site_type_id;
    }

    public void setSite_type_id(int site_type_id) {
        this.site_type_id = site_type_id;
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
