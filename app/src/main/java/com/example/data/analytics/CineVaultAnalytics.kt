package com.example.data.analytics

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AnalyticsEvent(
    val eventName: String,
    val params: Map<String, Any>,
    val timestamp: Long = System.currentTimeMillis()
)

interface AnalyticsProvider {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
}

class CineVaultAnalytics private constructor() : AnalyticsProvider {
    private val _events = MutableStateFlow<List<AnalyticsEvent>>(emptyList())
    val events: StateFlow<List<AnalyticsEvent>> = _events.asStateFlow()

    override fun logEvent(name: String, params: Map<String, Any>) {
        val event = AnalyticsEvent(name, params)
        _events.value = (listOf(event) + _events.value).take(100)
        Log.d("CineVaultAnalytics", "Event: $name -> $params")
    }

    fun logAppOpened() {
        logEvent("app_opened")
    }

    fun logContentViewed(contentId: String, title: String, type: String) {
        logEvent("content_viewed", mapOf("content_id" to contentId, "title" to title, "type" to type))
    }

    fun logPlaybackStarted(contentId: String, title: String, quality: String) {
        logEvent("play_started", mapOf("content_id" to contentId, "title" to title, "quality" to quality))
    }

    fun logPlaybackCompleted(contentId: String, watchDurationMs: Long) {
        logEvent("play_completed", mapOf("content_id" to contentId, "duration_ms" to watchDurationMs))
    }

    fun logSearchPerformed(query: String, resultCount: Int) {
        logEvent("search_performed", mapOf("query" to query, "result_count" to resultCount))
    }

    fun logWatchlistToggled(contentId: String, added: Boolean) {
        logEvent("watchlist_toggled", mapOf("content_id" to contentId, "added" to added))
    }

    fun logDownloadStarted(contentId: String, title: String) {
        logEvent("download_started", mapOf("content_id" to contentId, "title" to title))
    }

    fun logSubscriptionSelected(tierName: String) {
        logEvent("subscription_selected", mapOf("tier" to tierName))
    }

    companion object {
        val instance by lazy { CineVaultAnalytics() }
    }
}
