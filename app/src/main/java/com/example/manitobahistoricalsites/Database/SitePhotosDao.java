package com.example.manitobahistoricalsites.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.Maybe;

@Dao
public interface SitePhotosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable  insertAllSitePhotos(SitePhotos... sitePhotos);

    @Delete
    public Completable deleteSitePhoto(SitePhotos sitePhotos);

    @Query("SELECT * FROM sitePhotos")
    public Maybe<List<SitePhotos>> getAllSitePhotos();

    @Query("SELECT * FROM sitePhotos WHERE site_id = :site_id ")
    public Maybe<List<SitePhotos>> getAllSitePhotosForSite(int site_id);


}
