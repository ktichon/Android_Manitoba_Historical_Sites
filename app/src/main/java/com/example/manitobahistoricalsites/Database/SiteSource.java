package com.example.manitobahistoricalsites.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SiteSource {
    @PrimaryKey
    public int source_id;
    public int site_id;
    public String info;
    public String import_date;

    public SiteSource(int source_id, int site_id, String info, String import_date) {
        this.source_id = source_id;
        this.site_id = site_id;
        this.info = info;
        this.import_date = import_date;
    }

    public int getSource_id() {
        return source_id;
    }

    public void setSource_id(int source_id) {
        this.source_id = source_id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
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
