package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.analytics.CineVaultAnalytics
import com.example.data.local.CineVaultDatabase
import com.example.data.model.ContentItem
import com.example.data.model.ContentType
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.DownloadedItem
import com.example.data.model.Episode
import com.example.data.model.NotificationItem
import com.example.data.model.Review
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserProfile
import com.example.data.repository.CineVaultCatalog
import com.example.data.repository.CineVaultRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val selectedFilter: String = "All", // "All", "Movies", "Series", "4K UHD", "Action", "Sci-Fi", "Drama"
    val sortBy: String = "Trending", // "Trending", "Top Rated", "Newest"
    val results: List<ContentItem> = emptyList(),
    val recentSearches: List<String> = listOf("Neon Horizon", "Cyberpunk", "Last Signal", "4K Sci-Fi", "Space Odyssey"),
    val isSearching: Boolean = false
)

class CineVaultViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CineVaultDatabase.getInstance(application)
    val repository = CineVaultRepository(db.dao(), viewModelScope)
    val analytics = CineVaultAnalytics.instance

    // UI Message / Toast bus
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // All catalog items
    val allContent: StateFlow<List<ContentItem>> = repository.allContent

    // Active profile
    val currentProfile: StateFlow<UserProfile> = repository.currentProfile
    val profiles: StateFlow<List<UserProfile>> = repository.profiles

    // Subscription
    val subscriptionTier: StateFlow<SubscriptionTier> = repository.subscriptionTier

    // Notifications
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications

    // Watchlist items & IDs
    val watchlistItems: StateFlow<List<ContentItem>> = repository.watchlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchlistIds: StateFlow<List<String>> = repository.watchlistIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Continue Watching
    val continueWatching: StateFlow<List<ContinueWatchingItem>> = repository.continueWatching
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Downloads
    val downloads: StateFlow<List<DownloadedItem>> = repository.downloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search state
    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    // Currently Selected Content for Details or Playback
    private val _selectedContent = MutableStateFlow<ContentItem?>(null)
    val selectedContent: StateFlow<ContentItem?> = _selectedContent.asStateFlow()

    // Currently Selected Episode for Series Playback
    private val _selectedEpisode = MutableStateFlow<Episode?>(null)
    val selectedEpisode: StateFlow<Episode?> = _selectedEpisode.asStateFlow()

    init {
        analytics.logAppOpened()
        // Initialize search with all content
        viewModelScope.launch {
            allContent.collect { items ->
                updateSearchFilter(_searchState.value.query, _searchState.value.selectedFilter, _searchState.value.sortBy, items)
            }
        }
    }

    fun selectContent(item: ContentItem) {
        _selectedContent.value = item
        _selectedEpisode.value = item.seasons.firstOrNull()?.episodes?.firstOrNull()
        analytics.logContentViewed(item.id, item.title, item.type.name)
    }

    fun selectEpisode(episode: Episode) {
        _selectedEpisode.value = episode
    }

    fun toggleWatchlist(item: ContentItem) {
        viewModelScope.launch {
            val willBeInWatchlist = !watchlistIds.value.contains(item.id)
            repository.toggleWatchlist(item.id)
            _userMessage.emit(
                if (willBeInWatchlist) "Added '${item.title}' to My List"
                else "Removed '${item.title}' from My List"
            )
        }
    }

    fun removeFromContinueWatching(contentId: String) {
        viewModelScope.launch {
            repository.removeFromContinueWatching(contentId)
            _userMessage.emit("Removed from Continue Watching")
        }
    }

    fun clearContinueWatching() {
        viewModelScope.launch {
            repository.clearContinueWatching()
            _userMessage.emit("Cleared Continue Watching history")
        }
    }

    fun downloadItem(content: ContentItem, episode: Episode? = null) {
        viewModelScope.launch {
            repository.downloadContent(
                content = content,
                seasonNumber = episode?.seasonNumber,
                episodeNumber = episode?.episodeNumber,
                episodeTitle = episode?.title
            )
            _userMessage.emit("Downloading '${episode?.title ?: content.title}' for offline viewing")
        }
    }

    fun deleteDownload(downloadId: String) {
        viewModelScope.launch {
            repository.deleteDownload(downloadId)
            _userMessage.emit("Download deleted from device")
        }
    }

    fun clearAllDownloads() {
        viewModelScope.launch {
            repository.clearAllDownloads()
            _userMessage.emit("Cleared all offline downloads")
        }
    }

    fun switchProfile(profile: UserProfile) {
        repository.switchProfile(profile)
        viewModelScope.launch {
            _userMessage.emit("Switched profile to ${profile.name}")
        }
    }

    fun addProfile(name: String, isKids: Boolean) {
        repository.addProfile(name, isKids)
        viewModelScope.launch {
            _userMessage.emit("Profile '$name' created")
        }
    }

    fun selectSubscriptionTier(tier: SubscriptionTier) {
        repository.setSubscriptionTier(tier)
        viewModelScope.launch {
            _userMessage.emit("Plan updated to ${tier.planName}")
        }
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
        viewModelScope.launch {
            _userMessage.emit("All notifications marked as read")
        }
    }

    fun clearNotifications() {
        repository.clearNotifications()
        viewModelScope.launch {
            _userMessage.emit("Notifications cleared")
        }
    }

    // Search Logic
    fun onSearchQueryChanged(newQuery: String) {
        _searchState.value = _searchState.value.copy(query = newQuery)
        updateSearchFilter(newQuery, _searchState.value.selectedFilter, _searchState.value.sortBy, allContent.value)
        if (newQuery.isNotBlank()) {
            analytics.logSearchPerformed(newQuery, _searchState.value.results.size)
        }
    }

    fun onSearchFilterSelected(filter: String) {
        _searchState.value = _searchState.value.copy(selectedFilter = filter)
        updateSearchFilter(_searchState.value.query, filter, _searchState.value.sortBy, allContent.value)
    }

    fun onSearchSortSelected(sortBy: String) {
        _searchState.value = _searchState.value.copy(sortBy = sortBy)
        updateSearchFilter(_searchState.value.query, _searchState.value.selectedFilter, sortBy, allContent.value)
    }

    fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        val current = _searchState.value.recentSearches.filterNot { it.equals(query, ignoreCase = true) }
        _searchState.value = _searchState.value.copy(
            recentSearches = (listOf(query) + current).take(8)
        )
    }

    fun clearRecentSearches() {
        _searchState.value = _searchState.value.copy(recentSearches = emptyList())
    }

    private fun updateSearchFilter(query: String, filter: String, sortBy: String, items: List<ContentItem>) {
        var filtered = items

        // Query filter
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            filtered = filtered.filter { item ->
                item.title.lowercase().contains(q) ||
                item.synopsis.lowercase().contains(q) ||
                item.genres.any { it.lowercase().contains(q) } ||
                item.director.lowercase().contains(q) ||
                item.cast.any { it.name.lowercase().contains(q) } ||
                item.releaseYear.toString().contains(q)
            }
        }

        // Category filter
        filtered = when (filter) {
            "Movies" -> filtered.filter { it.type == ContentType.MOVIE }
            "Series" -> filtered.filter { it.type == ContentType.SERIES }
            "4K UHD" -> filtered.filter { it.qualityBadge.contains("4K", ignoreCase = true) }
            "Action" -> filtered.filter { it.genres.any { g -> g.equals("Action", ignoreCase = true) } }
            "Sci-Fi" -> filtered.filter { it.genres.any { g -> g.equals("Sci-Fi", ignoreCase = true) } }
            "Drama" -> filtered.filter { it.genres.any { g -> g.equals("Drama", ignoreCase = true) } }
            else -> filtered
        }

        // Sorting
        filtered = when (sortBy) {
            "Top Rated" -> filtered.sortedByDescending { it.rating }
            "Newest" -> filtered.sortedByDescending { it.releaseYear }
            else -> filtered.sortedByDescending { it.watchCount }
        }

        _searchState.value = _searchState.value.copy(results = filtered)
    }

    // Playback Progress Sync
    fun recordPlaybackProgress(item: ContentItem, positionMs: Long, durationMs: Long, episode: Episode? = null) {
        viewModelScope.launch {
            repository.savePlaybackProgress(
                item = item,
                positionMs = positionMs,
                durationMs = durationMs,
                seasonNumber = episode?.seasonNumber,
                episodeNumber = episode?.episodeNumber,
                episodeTitle = episode?.title
            )
        }
    }

    // User Add Review
    fun addReview(contentId: String, rating: Float, comment: String) {
        val current = _selectedContent.value ?: return
        if (current.id == contentId) {
            val newReview = Review(
                id = "rev-${System.currentTimeMillis()}",
                authorName = currentProfile.value.name,
                rating = rating,
                date = "Today",
                comment = comment,
                isUserReview = true
            )
            _selectedContent.value = current.copy(
                reviews = listOf(newReview) + current.reviews
            )
            viewModelScope.launch {
                _userMessage.emit("Review submitted! Thank you.")
            }
        }
    }

    // Admin Operations
    fun addAdminContent(
        title: String,
        synopsis: String,
        type: ContentType,
        releaseYear: Int,
        genres: List<String>,
        durationMinutes: Int,
        totalSeasons: Int,
        rating: Float,
        posterUrl: String,
        backdropUrl: String,
        isFeatured: Boolean
    ) {
        viewModelScope.launch {
            val newItem = ContentItem(
                id = "cv-custom-${System.currentTimeMillis()}",
                title = title,
                tagline = "Original CineVault Premiere",
                synopsis = synopsis,
                type = type,
                releaseYear = releaseYear,
                contentRating = "TV-MA",
                genres = genres,
                durationMinutes = durationMinutes,
                totalSeasons = totalSeasons,
                rating = rating,
                posterUrl = posterUrl.ifEmpty { "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800" },
                backdropUrl = backdropUrl.ifEmpty { "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=1600" },
                videoUrl = CineVaultCatalog.SAMPLE_STREAM_1,
                qualityBadge = "4K UHD",
                director = "CineVault Director",
                studio = "CineVault Originals",
                isFeatured = isFeatured,
                isNewRelease = true
            )
            repository.addCustomContent(newItem)
            _userMessage.emit("Title '$title' published to CineVault catalog")
        }
    }

    fun deleteAdminContent(contentId: String) {
        viewModelScope.launch {
            repository.deleteContent(contentId)
            _userMessage.emit("Content deleted from catalog")
        }
    }
}
