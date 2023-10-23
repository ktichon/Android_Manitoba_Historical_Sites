package com.example.manitobahistoricalsites.Database;

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
}
