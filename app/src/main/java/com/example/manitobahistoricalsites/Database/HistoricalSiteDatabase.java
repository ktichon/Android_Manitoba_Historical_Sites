package com.example.manitobahistoricalsites.Database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ManitobaHistoricalSite.class, SitePhotos.class, SiteSource.class, SiteType.class, WinnipegHistoricalSite.class }, version = 1)
public abstract class HistoricalSiteDatabase extends RoomDatabase {
    public abstract ManitobaHistoricalSiteDao manitobaHistoricalSiteDao();
    public abstract SitePhotosDao sitePhotosDao ();
    public abstract SiteSourceDao siteSourceDao ();
    public abstract SiteType siteType ();
    public abstract WinnipegHistoricalSiteDao winnipegHistoricalSiteDao ();

}
