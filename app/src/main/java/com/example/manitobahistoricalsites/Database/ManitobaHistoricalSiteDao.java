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
            "SELECT DISTINCT manitobahistoricalsite.* FROM manitobahistoricalsite"
            + " INNER JOIN siteType ON siteType.site_id = manitobahistoricalsite.site_id"
            + " WHERE siteType.type in (:types) "
    )
    public Maybe<List<ManitobaHistoricalSite>>  loadManitobaHistoricalSitesFilterType(List<String> types);

    @Query(
            "SELECT DISTINCT manitobahistoricalsite.* FROM manitobahistoricalsite"
                    + " WHERE manitobahistoricalsite.municipality in (:municipality) "
    )
    public Maybe<List<ManitobaHistoricalSite>> loadManitobaHistoricalSitesFilterMunicipality (List<String> municipality);

    @Query(
            "SELECT DISTINCT manitobahistoricalsite.* FROM manitobahistoricalsite"
                    + " INNER JOIN siteType ON siteType.site_id = manitobahistoricalsite.site_id"
                    + " WHERE manitobahistoricalsite.municipality in (:municipality) "
                    + " AND siteType.type in (:types)"
    )
    public Maybe<List<ManitobaHistoricalSite>>  loadManitobaHistoricalSitesAllFilters (List<String> types, List<String> municipality);















}
