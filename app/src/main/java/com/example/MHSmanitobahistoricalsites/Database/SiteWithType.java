package com.example.MHSmanitobahistoricalsites.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SiteWithType {
    @PrimaryKey
    public int site_with_type_id;
    public int site_type_id;
    public int site_id;

    public String import_date;

    public SiteWithType(int site_with_type_id, int site_type_id, int site_id, String import_date) {
        this.site_with_type_id = site_with_type_id;
        this.site_type_id = site_type_id;
        this.site_id = site_id;
        this.import_date = import_date;
    }

    public int getSite_with_type_id() {
        return site_with_type_id;
    }

    public void setSite_with_type_id(int site_with_type_id) {
        this.site_with_type_id = site_with_type_id;
    }

    public int getSite_type_id() {
        return site_type_id;
    }

    public void setSite_type_id(int site_type_id) {
        this.site_type_id = site_type_id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public String getImport_date() {
        return import_date;
    }

    public void setImport_date(String import_date) {
        this.import_date = import_date;
    }
}
