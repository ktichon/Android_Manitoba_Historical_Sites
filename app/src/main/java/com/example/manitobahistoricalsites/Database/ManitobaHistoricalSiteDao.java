package com.example.manitobahistoricalsites.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface ManitobaHistoricalSiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertManitobaHistoricalSites(ManitobaHistoricalSite ... manitobaHistoricalSite);

    @Delete
    public Completable deleteManitobaHistoricalSites(ManitobaHistoricalSite ... manitobaHistoricalSite);

    @Update
    public Completable updateManitobaHistoricalSites(ManitobaHistoricalSite ... manitobaHistoricalSites);


    @Query("SELECT * FROM manitobahistoricalsite")
    public  Maybe<List<ManitobaHistoricalSite>> loadAllManitobaHistoricalSites();

    @Query("SELECT  * FROM manitobahistoricalsite WHERE site_id = :site_id LIMIT 1")
    public Maybe<ManitobaHistoricalSite> getManitobaHistoricalSite(int site_id);


    @Query(
            "SELECT * FROM manitobahistoricalsite"
            + " INNER JOIN siteType ON siteType.site_id = manitobahistoricalsite.site_id"
    )
    public Maybe<Map<ManitobaHistoricalSite, List<SiteType>>>  loadAllManitobaHistoricalSitesWithTypes();

    @Query(
            "SELECT * FROM manitobahistoricalsite"
            + " INNER JOIN siteType ON siteType.site_id = manitobahistoricalsite.site_id"
            + " WHERE siteType.type in (:types) "
    )
    public Maybe<Map<ManitobaHistoricalSite, List<SiteType>>>  loadManitobaHistoricalSitesWithTypes(List<String> types);

    @Query(
            "SELECT * FROM manitobahistoricalsite"
                    + " INNER JOIN siteType ON siteType.site_id = manitobahistoricalsite.site_id"
                    + " WHERE manitobahistoricalsite.municipality in (:municipality) "
    )
    public Maybe<Map<ManitobaHistoricalSite, List<SiteType>>> loadManitobaHistoricalSitesFromMunicipality (List<String> municipality);

    @Query(
            "SELECT * FROM manitobahistoricalsite"
                    + " INNER JOIN siteType ON siteType.site_id = manitobahistoricalsite.site_id"
                    + " WHERE manitobahistoricalsite.municipality in (:municipality) "
                    + " AND siteType.type in (:types)"
    )
    public Maybe<Map<ManitobaHistoricalSite, List<SiteType>>>  loadManitobaHistoricalSitesAllFilters (List<String> types, List<String> municipality);















}
