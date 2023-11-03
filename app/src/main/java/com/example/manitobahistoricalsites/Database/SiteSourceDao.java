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
public interface SiteSourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertAllSiteSources(SiteSource... siteSource);

    @Delete
    public Completable deleteSiteSource(SiteSource siteSource);

    @Query("SELECT * FROM siteSource")
    public Maybe<List<SiteSource>> getAllSiteSources();

    @Query("SELECT * FROM siteSource WHERE site_id = :site_id ")
    public Maybe<List<SiteSource>> getAllSiteSourcesForSite(int site_id);
}
