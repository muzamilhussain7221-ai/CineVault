package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ContentItem
import com.example.data.model.ContentType
import com.example.ui.components.CineVaultLogo
import com.example.ui.components.ContentRow
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.RatingGold
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

@Composable
fun HomeScreen(
    viewModel: CineVaultViewModel,
    onContentClick: (ContentItem) -> Unit,
    onPlayClick: (ContentItem) -> Unit,
    onNavigateSearch: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onOpenProfileSwitch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allItems by viewModel.allContent.collectAsState()
    val continueWatchingList by viewModel.continueWatching.collectAsState()
    val watchlistIds by viewModel.watchlistIds.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotificationsCount = notifications.count { !it.isRead }

    // Featured hero items
    val featuredItems = remember(allItems) {
        allItems.filter { it.isFeatured }.ifEmpty { allItems.take(3) }
    }
    var heroIndex by remember { mutableIntStateOf(0) }
    val currentHero = featuredItems.getOrElse(heroIndex % featuredItems.size.coerceAtLeast(1)) { allItems.first() }

    var isTrailerAudioMuted by remember { mutableStateOf(true) }

    // Categorized lists
    val trendingItems = remember(allItems) { allItems.filter { it.isTrending } }
    val popularMovies = remember(allItems) { allItems.filter { it.type == ContentType.MOVIE } }
    val popularSeries = remember(allItems) { allItems.filter { it.type == ContentType.SERIES } }
    val newReleases = remember(allItems) { allItems.filter { it.isNewRelease } }
    val topRated = remember(allItems) { allItems.sortedByDescending { it.rating } }
    val recommendedItems = remember(allItems) { allItems.filter { it.isRecommended } }
    val sciFiItems = remember(allItems) { allItems.filter { it.genres.any { g -> g.contains("Sci-Fi", ignoreCase = true) } } }
    val actionItems = remember(allItems) { allItems.filter { it.genres.any { g -> g.contains("Action", ignoreCase = true) } } }
    val dramaItems = remember(allItems) { allItems.filter { it.genres.any { g -> g.contains("Drama", ignoreCase = true) } } }

    val isInWatchlist = watchlistIds.contains(currentHero.id)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        // 1. Top Bar Area
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CineVaultLogo()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Search
                    IconButton(
                        onClick = onNavigateSearch,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextPrimary
                        )
                    }

                    // Notifications with badge
                    IconButton(
                        onClick = onNavigateNotifications,
                        modifier = Modifier.size(38.dp)
                    ) {
                        BadgedBox(badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge(
                                    containerColor = ElectricViolet,
                                    contentColor = Color.White
                                ) {
                                    Text(unreadNotificationsCount.toString())
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = TextPrimary
                            )
                        }
                    }

                    // Admin shortcut
                    IconButton(
                        onClick = onNavigateAdmin,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Portal",
                            tint = SoftCyan
                        )
                    }

                    // User Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(currentProfile.avatarColorHex))
                            .border(1.dp, GlassBorder, CircleShape)
                            .clickable(onClick = onOpenProfileSwitch),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Current Profile",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Large Cinematic Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
            ) {
                // Hero Backdrop Artwork
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentHero.backdropUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = currentHero.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Atmospheric Gradient Overlay (top, sides, bottom fade to MidnightBackground)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x99080B14),
                                    Color(0x33080B14),
                                    Color(0xE6080B14),
                                    MidnightBackground
                                )
                            )
                        )
                )

                // Hero Metadata & Action Buttons
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Badge Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quality badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xCC080B14))
                                .border(0.5.dp, SoftCyan, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentHero.qualityBadge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SoftCyan
                                )
                            )
                        }

                        // Content rating
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xAA1E2845))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentHero.contentRating,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            )
                        }

                        // Rating score
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = RatingGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format("%.1f", currentHero.rating),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        // Year & Runtime / Seasons
                        Text(
                            text = "• ${currentHero.releaseYear} • ${if (currentHero.type == ContentType.MOVIE) "${currentHero.durationMinutes}m" else "${currentHero.totalSeasons} Seasons"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Title
                    Text(
                        text = currentHero.title,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Genres
                    Text(
                        text = currentHero.genres.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SoftCyan,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Synopsis
                    Text(
                        text = currentHero.synopsis,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 16.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onPlayClick(currentHero) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Watch Now",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Watch Now",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.toggleWatchlist(currentHero) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Watchlist",
                                tint = if (isInWatchlist) SoftCyan else Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isInWatchlist) "In List" else "My List",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Mute/Unmute preview toggle
                        IconButton(
                            onClick = { isTrailerAudioMuted = !isTrailerAudioMuted },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000))
                                .border(1.dp, GlassBorder, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isTrailerAudioMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "Toggle Trailer Audio",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hero Carousel Dots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        featuredItems.forEachIndexed { index, item ->
                            val isSelected = index == heroIndex % featuredItems.size
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (isSelected) 18.dp else 6.dp, 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isSelected) ElectricViolet else Color(0x66FFFFFF))
                                    .clickable { heroIndex = index }
                            )
                        }
                    }
                }
            }
        }

        // 3. Continue Watching Row (if user has active watched items)
        if (continueWatchingList.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Continue Watching",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = TextPrimary
                        )

                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.labelSmall.copy(color = SoftCyan),
                            modifier = Modifier.clickable { viewModel.clearContinueWatching() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(continueWatchingList, key = { it.contentId }) { item ->
                            val contentItem = allItems.find { it.id == item.contentId }
                            ContinueWatchingCard(
                                item = item,
                                onPlay = {
                                    if (contentItem != null) {
                                        onPlayClick(contentItem)
                                    }
                                },
                                onDismiss = { viewModel.removeFromContinueWatching(item.contentId) }
                            )
                        }
                    }
                }
            }
        }

        // 4. Content Rows
        item {
            ContentRow(
                title = "Trending Now",
                items = trendingItems,
                onItemClick = onContentClick
            )
        }

        item {
            ContentRow(
                title = "Recommended For You",
                items = recommendedItems,
                onItemClick = onContentClick
            )
        }

        item {
            ContentRow(
                title = "New Releases",
                items = newReleases,
                onItemClick = onContentClick
            )
        }

        item {
            ContentRow(
                title = "Popular Series",
                items = popularSeries,
                onItemClick = onContentClick
            )
        }

        item {
            ContentRow(
                title = "Top Rated in 4K UHD",
                items = topRated,
                onItemClick = onContentClick
            )
        }

        item {
            ContentRow(
                title = "Popular Movies",
                items = popularMovies,
                onItemClick = onContentClick
            )
        }

        item {
            ContentRow(
                title = "Sci-Fi & Cyberpunk",
                items = sciFiItems,
                onItemClick = onContentClick
            )
        }

        item {
            ContentRow(
                title = "High-Octane Action",
                items = actionItems,
                onItemClick = onContentClick
            )
        }

        item {
            ContentRow(
                title = "Captivating Drama",
                items = dramaItems,
                onItemClick = onContentClick
            )
        }

        // Bottom spacing for navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
