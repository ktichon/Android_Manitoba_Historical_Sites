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
public interface WinnipegHistoricalSiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertAllWinnipegHistoricalSites(WinnipegHistoricalSite... winnipegHistoricalSite);

    @Delete
    public Completable deleteWinnipegHistoricalSite(WinnipegHistoricalSite winnipegHistoricalSite);

    @Query("SELECT * FROM WinnipegHistoricalSite")
    public Maybe<List<WinnipegHistoricalSite>> getAllWinnipegHistoricalSites();

    @Query("SELECT * FROM WinnipegHistoricalSite WHERE site_id = :site_id ")
    public Maybe<List<WinnipegHistoricalSite>> getAllWinnipegHistoricalSitesForSite(int site_id);
}
