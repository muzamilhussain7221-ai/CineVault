package com.example.data.repository

import com.example.data.analytics.CineVaultAnalytics
import com.example.data.local.CineVaultDao
import com.example.data.local.ContinueWatchingEntity
import com.example.data.local.CustomContentEntity
import com.example.data.local.DownloadEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.ContentItem
import com.example.data.model.ContentType
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.DownloadedItem
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CineVaultRepository(
    private val dao: CineVaultDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val analytics = CineVaultAnalytics.instance

    // Profile Management
    private val defaultProfiles = listOf(
        UserProfile("p1", "Alex (Primary)", 0xFF8B5CF6, isKids = false, maturityRating = "TV-MA"),
        UserProfile("p2", "Sarah", 0xFF06B6D4, isKids = false, maturityRating = "16+"),
        UserProfile("p3", "Kids Vault", 0xFF10B981, isKids = true, maturityRating = "PG")
    )
    private val _profiles = MutableStateFlow(defaultProfiles)
    val profiles: StateFlow<List<UserProfile>> = _profiles.asStateFlow()

    private val _currentProfile = MutableStateFlow(defaultProfiles[0])
    val currentProfile: StateFlow<UserProfile> = _currentProfile.asStateFlow()

    // Subscription Management
    private val _subscriptionTier = MutableStateFlow(SubscriptionTier.PREMIUM)
    val subscriptionTier: StateFlow<SubscriptionTier> = _subscriptionTier.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("n1", "New Episode Alert", "Episode 4 of Last Signal: Blackout Protocol is now available in 4K UHD.", "2h ago", NotificationType.EPISODE_DROP, targetContentId = "cv-series-01"),
            NotificationItem("n2", "Premiere Arrival", "Neon Horizon (Director's Cut) is now streaming with Dolby Atmos audio.", "5h ago", NotificationType.NEW_RELEASE, targetContentId = "cv-movie-01"),
            NotificationItem("n3", "Continue Watching", "You have 34 minutes left in Beyond the Orbit. Jump right back in.", "Yesterday", NotificationType.CONTINUE_WATCHING, targetContentId = "cv-movie-03"),
            NotificationItem("n4", "VIP Premium Active", "Your CineVault Premium subscription renewed successfully.", "3d ago", NotificationType.SUBSCRIPTION)
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Unified Master Catalog combining seed items + Room DB custom admin additions
    val allContent: StateFlow<List<ContentItem>> = dao.getCustomContent()
        .map { customEntities ->
            val customItems = customEntities.map { entity ->
                ContentItem(
                    id = entity.id,
                    title = entity.title,
                    tagline = entity.tagline,
                    synopsis = entity.synopsis,
                    type = if (entity.type == "SERIES") ContentType.SERIES else ContentType.MOVIE,
                    releaseYear = entity.releaseYear,
                    contentRating = entity.contentRating,
                    genres = entity.genresCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    durationMinutes = entity.durationMinutes,
                    totalSeasons = entity.totalSeasons,
                    rating = entity.rating,
                    posterUrl = entity.posterUrl,
                    backdropUrl = entity.backdropUrl,
                    videoUrl = entity.videoUrl,
                    qualityBadge = entity.qualityBadge,
                    director = entity.director,
                    studio = entity.studio,
                    isFeatured = entity.isFeatured,
                    isNewRelease = true
                )
            }
            CineVaultCatalog.initialItems + customItems
        }
        .stateIn(scope, SharingStarted.Eagerly, CineVaultCatalog.initialItems)

    // Watchlist
    val watchlistItems: Flow<List<ContentItem>> = combine(allContent, dao.getWatchlistIds()) { catalog, ids ->
        val idSet = ids.toSet()
        catalog.filter { idSet.contains(it.id) }
    }

    val watchlistIds: Flow<List<String>> = dao.getWatchlistIds()

    suspend fun toggleWatchlist(contentId: String) {
        val inWatchlist = dao.isInWatchlist(contentId)
        if (inWatchlist) {
            dao.removeFromWatchlist(contentId)
            analytics.logWatchlistToggled(contentId, false)
        } else {
            dao.addToWatchlist(WatchlistEntity(contentId))
            analytics.logWatchlistToggled(contentId, true)
        }
    }

    suspend fun isInWatchlist(contentId: String): Boolean {
        return dao.isInWatchlist(contentId)
    }

    // Continue Watching
    val continueWatching: Flow<List<ContinueWatchingItem>> = dao.getContinueWatching().map { entities ->
        entities.map { entity ->
            ContinueWatchingItem(
                contentId = entity.contentId,
                title = entity.title,
                backdropUrl = entity.backdropUrl,
                posterUrl = entity.posterUrl,
                type = if (entity.type == "SERIES") ContentType.SERIES else ContentType.MOVIE,
                seasonNumber = entity.seasonNumber,
                episodeNumber = entity.episodeNumber,
                episodeTitle = entity.episodeTitle,
                positionMs = entity.positionMs,
                durationMs = entity.durationMs,
                lastWatchedDate = entity.lastWatchedDate
            )
        }
    }

    suspend fun savePlaybackProgress(
        item: ContentItem,
        positionMs: Long,
        durationMs: Long,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null,
        episodeTitle: String? = null
    ) {
        if (positionMs <= 5000L && durationMs <= 0L) return
        val entity = ContinueWatchingEntity(
            contentId = item.id,
            title = item.title,
            backdropUrl = item.backdropUrl,
            posterUrl = item.posterUrl,
            type = item.type.name,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            episodeTitle = episodeTitle,
            positionMs = positionMs,
            durationMs = if (durationMs > 0) durationMs else (item.durationMinutes * 60 * 1000L),
            lastWatchedDate = System.currentTimeMillis()
        )
        dao.saveContinueWatching(entity)
    }

    suspend fun removeFromContinueWatching(contentId: String) {
        dao.removeFromContinueWatching(contentId)
    }

    suspend fun clearContinueWatching() {
        dao.clearContinueWatching()
    }

    // Downloads
    val downloads: Flow<List<DownloadedItem>> = dao.getAllDownloads().map { list ->
        list.map { entity ->
            DownloadedItem(
                id = entity.id,
                contentId = entity.contentId,
                title = entity.title,
                contentType = if (entity.contentType == "SERIES") ContentType.SERIES else ContentType.MOVIE,
                seasonNumber = entity.seasonNumber,
                episodeNumber = entity.episodeNumber,
                episodeTitle = entity.episodeTitle,
                backdropUrl = entity.backdropUrl,
                posterUrl = entity.posterUrl,
                quality = entity.quality,
                sizeBytes = entity.sizeBytes,
                progress = entity.progress,
                isCompleted = entity.isCompleted,
                timestamp = entity.timestamp
            )
        }
    }

    suspend fun downloadContent(
        content: ContentItem,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null,
        episodeTitle: String? = null
    ) {
        val downloadId = "dl-${UUID.randomUUID()}"
        val entity = DownloadEntity(
            id = downloadId,
            contentId = content.id,
            title = content.title,
            contentType = content.type.name,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            episodeTitle = episodeTitle,
            backdropUrl = content.backdropUrl,
            posterUrl = content.posterUrl,
            quality = _subscriptionTier.value.videoQuality,
            sizeBytes = (1200L + (content.durationMinutes * 6L)) * 1024L * 1024L, // Simulated byte size ~1.5GB
            progress = 1.0f,
            isCompleted = true
        )
        dao.insertDownload(entity)
        analytics.logDownloadStarted(content.id, content.title)
    }

    suspend fun deleteDownload(id: String) {
        dao.deleteDownload(id)
    }

    suspend fun clearAllDownloads() {
        dao.clearDownloads()
    }

    // Profile Actions
    fun switchProfile(profile: UserProfile) {
        _currentProfile.value = profile
    }

    fun addProfile(name: String, isKids: Boolean) {
        val colors = listOf(0xFF8B5CF6, 0xFF06B6D4, 0xFFEC4899, 0xFFF59E0B, 0xFF10B981)
        val color = colors[(_profiles.value.size) % colors.size]
        val newProfile = UserProfile(
            id = "p-${UUID.randomUUID()}",
            name = name,
            avatarColorHex = color,
            isKids = isKids,
            maturityRating = if (isKids) "PG" else "TV-MA"
        )
        _profiles.value = _profiles.value + newProfile
    }

    // Subscription
    fun setSubscriptionTier(tier: SubscriptionTier) {
        _subscriptionTier.value = tier
        analytics.logSubscriptionSelected(tier.planName)
    }

    // Notifications
    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    // Admin Operations
    suspend fun addCustomContent(item: ContentItem) {
        val entity = CustomContentEntity(
            id = item.id.ifEmpty { "cv-custom-${UUID.randomUUID()}" },
            title = item.title,
            tagline = item.tagline,
            synopsis = item.synopsis,
            type = item.type.name,
            releaseYear = item.releaseYear,
            contentRating = item.contentRating,
            genresCsv = item.genres.joinToString(","),
            durationMinutes = item.durationMinutes,
            totalSeasons = item.totalSeasons,
            rating = item.rating,
            posterUrl = item.posterUrl.ifEmpty { "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800" },
            backdropUrl = item.backdropUrl.ifEmpty { "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=1600" },
            videoUrl = item.videoUrl.ifEmpty { CineVaultCatalog.SAMPLE_STREAM_1 },
            qualityBadge = item.qualityBadge,
            director = item.director,
            studio = item.studio,
            isFeatured = item.isFeatured
        )
        dao.insertCustomContent(entity)
    }

    suspend fun deleteContent(contentId: String) {
        dao.deleteCustomContent(contentId)
        dao.removeFromWatchlist(contentId)
        dao.removeFromContinueWatching(contentId)
    }

    // Recommendation Engine: deterministic matching by genres & content type
    fun getRecommendations(sourceItem: ContentItem, allItems: List<ContentItem>): List<ContentItem> {
        val sourceGenres = sourceItem.genres.toSet()
        return allItems
            .filter { it.id != sourceItem.id }
            .sortedByDescending { item ->
                val overlap = item.genres.count { sourceGenres.contains(it) }
                val typeMatch = if (item.type == sourceItem.type) 1 else 0
                overlap * 2 + typeMatch + (item.rating / 2f)
            }
            .take(8)
    }
}
