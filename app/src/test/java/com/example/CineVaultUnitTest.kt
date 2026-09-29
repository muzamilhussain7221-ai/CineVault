package com.example

import com.example.data.model.ContentType
import com.example.data.model.SubscriptionTier
import com.example.data.repository.CineVaultCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CineVaultUnitTest {

    @Test
    fun testCatalogInitialItemsNotEmpty() {
        val items = CineVaultCatalog.initialItems
        assertTrue("Catalog should contain initial sample items", items.isNotEmpty())
        assertTrue("Catalog should have at least 8 items", items.size >= 8)
    }

    @Test
    fun testMovieAndSeriesAttributes() {
        val movies = CineVaultCatalog.initialItems.filter { it.type == ContentType.MOVIE }
        val series = CineVaultCatalog.initialItems.filter { it.type == ContentType.SERIES }

        assertTrue("Catalog must have movies", movies.isNotEmpty())
        assertTrue("Catalog must have TV series", series.isNotEmpty())

        movies.forEach { movie ->
            assertTrue("Movie should have duration > 0", movie.durationMinutes > 0)
            assertTrue("Movie should have release year >= 2020", movie.releaseYear >= 2020)
            assertTrue("Movie rating should be between 1 and 10", movie.rating in 1f..10f)
            assertNotNull(movie.videoUrl)
        }

        series.forEach { s ->
            assertTrue("Series should have at least 1 season", s.totalSeasons >= 1)
            assertTrue("Series should have seasons list populated", s.seasons.isNotEmpty())
            assertTrue("Episodes should have runtimes", s.seasons.first().episodes.first().durationMinutes > 0)
        }
    }

    @Test
    fun testSubscriptionTiersIntegrity() {
        assertEquals("Should have 3 subscription tiers", 3, SubscriptionTier.entries.size)

        val freeTier = SubscriptionTier.FREE
        val standardTier = SubscriptionTier.STANDARD
        val premiumTier = SubscriptionTier.PREMIUM

        assertFalse("Free tier should not support offline downloads", freeTier.offlineDownloads)
        assertTrue("Standard tier should support offline downloads", standardTier.offlineDownloads)
        assertTrue("Premium tier should support offline downloads", premiumTier.offlineDownloads)

        assertTrue("Premium should have 4 screens", premiumTier.supportedDevicesCount == 4)
        assertTrue("Premium should support 4K UHD", premiumTier.videoQuality.contains("4K"))
    }
}
