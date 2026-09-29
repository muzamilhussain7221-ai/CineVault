package com.example.data.model

enum class ContentType {
    MOVIE,
    SERIES
}

enum class MediaQuality(val label: String, val badge: String) {
    SD_360("360p", "SD"),
    SD_480("480p", "SD"),
    HD_720("720p HD", "HD"),
    FHD_1080("1080p Full HD", "FHD"),
    QHD_1440("1440p QHD", "2K"),
    UHD_4K("2160p 4K UHD", "4K")
}

data class CastMember(
    val id: String,
    val name: String,
    val characterName: String,
    val role: String = "Actor",
    val avatarUrl: String = ""
)

data class Review(
    val id: String,
    val authorName: String,
    val authorAvatar: String = "",
    val rating: Float, // 1 to 10
    val date: String,
    val comment: String,
    val isUserReview: Boolean = false
)

data class Episode(
    val id: String,
    val seriesId: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val title: String,
    val synopsis: String,
    val durationMinutes: Int,
    val releaseDate: String,
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val watchedProgress: Float = 0f,
    val isWatched: Boolean = false
)

data class Season(
    val seasonNumber: Int,
    val title: String,
    val episodeCount: Int,
    val episodes: List<Episode>
)

data class ContentItem(
    val id: String,
    val title: String,
    val tagline: String,
    val synopsis: String,
    val type: ContentType,
    val releaseYear: Int,
    val contentRating: String, // e.g. "PG-13", "TV-MA", "16+"
    val genres: List<String>,
    val durationMinutes: Int = 110, // For movies
    val totalSeasons: Int = 1, // For series
    val rating: Float, // e.g. 8.8
    val posterUrl: String,
    val backdropUrl: String,
    val videoUrl: String,
    val trailerUrl: String = "",
    val qualityBadge: String = "4K UHD",
    val audioLanguages: List<String> = listOf("English [Original]", "Spanish", "French", "German", "Japanese"),
    val subtitleLanguages: List<String> = listOf("English [CC]", "Spanish", "French", "German", "Japanese", "Off"),
    val audioFormat: String = "Dolby Atmos 5.1",
    val director: String = "David Vance",
    val writers: List<String> = listOf("Elena Rostova", "Marcus Chen"),
    val studio: String = "CineVault Studios",
    val cast: List<CastMember> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isNewRelease: Boolean = false,
    val isRecommended: Boolean = false,
    val watchCount: Long = 1250000L
)

data class UserProfile(
    val id: String,
    val name: String,
    val avatarColorHex: Long = 0xFF8B5CF6,
    val isKids: Boolean = false,
    val languagePreference: String = "English",
    val maturityRating: String = "TV-MA",
    val autoPlayNext: Boolean = true,
    val defaultSubtitles: String = "English [CC]"
)

enum class SubscriptionTier(
    val planName: String,
    val priceMonthly: String,
    val videoQuality: String,
    val resolutionLabel: String,
    val supportedDevicesCount: Int,
    val audioQuality: String,
    val offlineDownloads: Boolean,
    val benefits: List<String>
) {
    FREE(
        planName = "Free with Ads",
        priceMonthly = "$0 / mo",
        videoQuality = "720p HD",
        resolutionLabel = "Standard HD",
        supportedDevicesCount = 1,
        audioQuality = "Stereo 2.0",
        offlineDownloads = false,
        benefits = listOf(
            "Access to select movies and series",
            "Streaming in 720p HD",
            "Watch on phone or tablet",
            "Supported with occasional ads"
        )
    ),
    STANDARD(
        planName = "Standard HD",
        priceMonthly = "$9.99 / mo",
        videoQuality = "1080p Full HD",
        resolutionLabel = "Full HD 1080p",
        supportedDevicesCount = 2,
        audioQuality = "Surround 5.1",
        offlineDownloads = true,
        benefits = listOf(
            "Full unlimited movie & series vault",
            "Crisp 1080p Full HD streaming",
            "Watch on 2 screens simultaneously",
            "Download up to 25 titles offline",
            "Completely Ad-free entertainment"
        )
    ),
    PREMIUM(
        planName = "Premium 4K Ultra",
        priceMonthly = "$14.99 / mo",
        videoQuality = "4K UHD + HDR10",
        resolutionLabel = "4K Ultra HD & Dolby Vision",
        supportedDevicesCount = 4,
        audioQuality = "Dolby Atmos 7.1",
        offlineDownloads = true,
        benefits = listOf(
            "Complete CineVault catalog in 4K UHD",
            "HDR10 & Dolby Vision enhancement",
            "Immersive Dolby Atmos spatial audio",
            "Watch on 4 screens simultaneously",
            "Unlimited offline title downloads",
            "Early access to new original releases",
            "VIP priority streaming bandwidth"
        )
    )
}

data class DownloadedItem(
    val id: String,
    val contentId: String,
    val title: String,
    val contentType: ContentType,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val episodeTitle: String? = null,
    val backdropUrl: String,
    val posterUrl: String,
    val quality: String = "1080p Full HD",
    val sizeBytes: Long = 1_450_000_000L, // ~1.45 GB
    val progress: Float = 1.0f,
    val isCompleted: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class ContinueWatchingItem(
    val contentId: String,
    val title: String,
    val backdropUrl: String,
    val posterUrl: String,
    val type: ContentType,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val episodeTitle: String? = null,
    val positionMs: Long = 0L,
    val durationMs: Long = 1L,
    val lastWatchedDate: Long = System.currentTimeMillis()
) {
    val progressFraction: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

enum class NotificationType {
    NEW_RELEASE,
    EPISODE_DROP,
    CONTINUE_WATCHING,
    SUBSCRIPTION,
    SYSTEM
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val targetContentId: String? = null
)
