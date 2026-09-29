package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CineVaultDao {

    // Watchlist
    @Query("SELECT contentId FROM watchlist ORDER BY addedTimestamp DESC")
    fun getWatchlistIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE contentId = :contentId")
    suspend fun removeFromWatchlist(contentId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE contentId = :contentId)")
    suspend fun isInWatchlist(contentId: String): Boolean

    // Continue Watching
    @Query("SELECT * FROM continue_watching ORDER BY lastWatchedDate DESC")
    fun getContinueWatching(): Flow<List<ContinueWatchingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContinueWatching(item: ContinueWatchingEntity)

    @Query("DELETE FROM continue_watching WHERE contentId = :contentId")
    suspend fun removeFromContinueWatching(contentId: String)

    @Query("DELETE FROM continue_watching")
    suspend fun clearContinueWatching()

    // Downloads
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :downloadId")
    suspend fun deleteDownload(downloadId: String)

    @Query("DELETE FROM downloads")
    suspend fun clearDownloads()

    // Custom Content (Admin)
    @Query("SELECT * FROM custom_content ORDER BY id DESC")
    fun getCustomContent(): Flow<List<CustomContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomContent(item: CustomContentEntity)

    @Query("DELETE FROM custom_content WHERE id = :contentId")
    suspend fun deleteCustomContent(contentId: String)
}
