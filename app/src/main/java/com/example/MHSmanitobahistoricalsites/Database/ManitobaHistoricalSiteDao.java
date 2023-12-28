package com.example.MHSmanitobahistoricalsites.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

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


    @Query("SELECT * FROM manitobahistoricalsite ORDER BY name ASC")
    public  Maybe<List<ManitobaHistoricalSite>> loadAllManitobaHistoricalSites();

    @Query("SELECT  * FROM manitobahistoricalsite WHERE site_id = :site_id LIMIT 1 ")
    public Maybe<ManitobaHistoricalSite> getManitobaHistoricalSite(int site_id);




    @Query(
            "SELECT DISTINCT manitobahistoricalsite.*  FROM manitobahistoricalsite"
            + " INNER JOIN sitewithtype ON sitewithtype.site_id = manitobahistoricalsite.site_id"
            + " WHERE sitewithtype.site_type_id in (:type_id) ORDER BY name ASC "
    )
    public Maybe<List<ManitobaHistoricalSite>>  loadManitobaHistoricalSitesFilterType(List<Integer> type_id);

    @Query(
            "SELECT DISTINCT manitobahistoricalsite.* FROM manitobahistoricalsite"
                    + " WHERE manitobahistoricalsite.municipality in (:municipality) ORDER BY name ASC "
    )
    public Maybe<List<ManitobaHistoricalSite>> loadManitobaHistoricalSitesFilterMunicipality (List<String> municipality);

    @Query(
            "SELECT DISTINCT manitobahistoricalsite.* FROM manitobahistoricalsite"
                    + " INNER JOIN sitewithtype ON sitewithtype.site_id = manitobahistoricalsite.site_id"
                    + " WHERE manitobahistoricalsite.municipality in (:municipality) "
                    + " AND sitewithtype.site_type_id in (:type_id) ORDER BY name ASC"
    )
    public Maybe<List<ManitobaHistoricalSite>>  loadManitobaHistoricalSitesAllFilters (List<Integer> type_id, List<String> municipality);















}
