package com.example.manitobahistoricalsites.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ManitobaHistoricalSite.class, SitePhotos.class, SiteSource.class, SiteType.class, WinnipegHistoricalSite.class }, version = 1)
public abstract class HistoricalSiteDatabase extends RoomDatabase {

    private static volatile HistoricalSiteDatabase INSTANCE;
    public abstract ManitobaHistoricalSiteDao manitobaHistoricalSiteDao();
    public abstract SitePhotosDao sitePhotosDao ();
    public abstract SiteSourceDao siteSourceDao ();
    public abstract SiteTypeDao siteTypeDao ();
    public abstract WinnipegHistoricalSiteDao winnipegHistoricalSiteDao ();

    public static HistoricalSiteDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HistoricalSiteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HistoricalSiteDatabase.class, "historicalSiteDatabase.db")
                            .fallbackToDestructiveMigration()
                            .createFromAsset("historicalSiteData.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
