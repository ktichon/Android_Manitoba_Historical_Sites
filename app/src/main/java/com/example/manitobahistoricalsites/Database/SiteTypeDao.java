package com.example.manitobahistoricalsites.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.rxjava3.core.Completable;


@Dao
public interface SiteTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertAllSiteTypes(SiteType... siteType);

    @Delete
    public Completable deleteSiteTypes(SiteType siteType);

    @Query("SELECT * FROM siteType")
    public Maybe<List<SiteType>> getAllSiteTypes();

    @Query("SELECT * FROM siteType WHERE site_id = :site_id ")
    public Maybe<List<SiteType>> getAllSiteTypesForSite(int site_id);

}
