package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val contentId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "continue_watching")
data class ContinueWatchingEntity(
    @PrimaryKey val contentId: String,
    val title: String,
    val backdropUrl: String,
    val posterUrl: String,
    val type: String, // "MOVIE" or "SERIES"
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val episodeTitle: String? = null,
    val positionMs: Long = 0L,
    val durationMs: Long = 1L,
    val lastWatchedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String,
    val contentId: String,
    val title: String,
    val contentType: String,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val episodeTitle: String? = null,
    val backdropUrl: String,
    val posterUrl: String,
    val quality: String,
    val sizeBytes: Long,
    val progress: Float,
    val isCompleted: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_content")
data class CustomContentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val tagline: String,
    val synopsis: String,
    val type: String, // "MOVIE" or "SERIES"
    val releaseYear: Int,
    val contentRating: String,
    val genresCsv: String,
    val durationMinutes: Int,
    val totalSeasons: Int,
    val rating: Float,
    val posterUrl: String,
    val backdropUrl: String,
    val videoUrl: String,
    val qualityBadge: String,
    val director: String,
    val studio: String,
    val isFeatured: Boolean = false
)
