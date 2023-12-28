package com.example.MHSmanitobahistoricalsites.Database;

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

    @Query("SELECT siteType.* FROM siteType " +
            "INNER JOIN siteWithType ON siteWithType.site_type_id = siteType.site_type_id" +
            " WHERE site_id = :site_id ")
    public Maybe<List<SiteType>> getAllSiteTypesForSite(int site_id);

    @Query("SELECT type FROM siteType " +
            " WHERE site_type_id in (:type_id) ")
    public Maybe<List<String>> getTypesFromSiteTypeIds(List<Integer> type_id);

}
