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
public interface SiteWithTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertAllSiteWithTypes(SiteWithType... SiteWithType);

    @Delete
    public Completable deleteSiteWithType(SiteWithType SiteWithType);

    @Query("SELECT * FROM SiteWithType")
    public Maybe<List<SiteWithType>> getAllSiteWithTypes();

    @Query("SELECT * FROM SiteWithType WHERE site_id = :site_id ")
    public Maybe<List<SiteWithType>> getAllSiteWithTypesForSite(int site_id);
    
}
