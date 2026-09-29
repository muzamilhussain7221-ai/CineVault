package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.data.model.Episode
import com.example.ui.components.EpisodeCard
import com.example.ui.components.PosterCard
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.RatingGold
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

@Composable
fun SeriesDetailsScreen(
    item: ContentItem,
    viewModel: CineVaultViewModel,
    onBack: () -> Unit,
    onPlayEpisode: (ContentItem, Episode) -> Unit,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val watchlistIds by viewModel.watchlistIds.collectAsState()
    val allItems by viewModel.allContent.collectAsState()
    val isInWatchlist = watchlistIds.contains(item.id)

    val seasons = item.seasons.ifEmpty {
        listOf(
            com.example.data.model.Season(
                seasonNumber = 1,
                title = "Season 1",
                episodeCount = 6,
                episodes = listOf(
                    Episode("ep-def-1", item.id, 1, 1, "Episode 1: The Beginning", item.synopsis, 52, item.releaseYear.toString(), item.videoUrl, item.backdropUrl),
                    Episode("ep-def-2", item.id, 1, 2, "Episode 2: Dark Horizon", "Unexpected alliances form under intense pressure.", 48, item.releaseYear.toString(), item.videoUrl, item.backdropUrl)
                )
            )
        )
    }

    var selectedSeasonIndex by remember { mutableIntStateOf(0) }
    var seasonDropdownExpanded by remember { mutableStateOf(false) }
    val currentSeason = seasons.getOrElse(selectedSeasonIndex) { seasons.first() }

    val recommendations = remember(item, allItems) {
        viewModel.repository.getRecommendations(item, allItems)
    }

    val firstEpisode = currentSeason.episodes.firstOrNull() ?: Episode(
        "ep-1", item.id, 1, 1, "Episode 1", item.synopsis, 50, "2026", item.videoUrl, item.backdropUrl
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        // Hero Backdrop
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(item.backdropUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x99080B14),
                                    Color(0x33080B14),
                                    MidnightBackground
                                )
                            )
                        )
                )

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x99080B14), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Stream '${item.title}' on CineVault!")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Series"))
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x99080B14), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Header info
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Card(
                    modifier = Modifier
                        .width(100.dp)
                        .aspectRatio(2f / 3f),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item.posterUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 21.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.tagline,
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftCyan, fontSize = 11.5.sp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = RatingGold, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(String.format("%.1f", item.rating), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MidnightSurfaceVariant)
                                .border(0.5.dp, SoftCyan, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(item.qualityBadge, color = SoftCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MidnightSurfaceElevated)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(item.contentRating, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${item.releaseYear} • ${seasons.size} Seasons • Dolby Atmos",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.genres.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }
            }
        }

        // Action Buttons Row
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = { onPlayEpisode(item, firstEpisode) },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch S1:E1", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.toggleWatchlist(item) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isInWatchlist) SoftCyan else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isInWatchlist) "In List" else "My List", color = Color.White, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.downloadItem(item, firstEpisode) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        // Synopsis
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = item.synopsis,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, lineHeight = 20.sp, fontSize = 13.sp)
                )
            }
        }

        // Season Selector Dropdown
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MidnightSurfaceElevated)
                        .border(1.dp, MidnightCardBorder, RoundedCornerShape(8.dp))
                        .clickable { seasonDropdownExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentSeason.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Season",
                        tint = SoftCyan
                    )
                }

                DropdownMenu(
                    expanded = seasonDropdownExpanded,
                    onDismissRequest = { seasonDropdownExpanded = false },
                    modifier = Modifier.background(MidnightSurfaceElevated)
                ) {
                    seasons.forEachIndexed { idx, s ->
                        DropdownMenuItem(
                            text = { Text(s.title, color = Color.White) },
                            onClick = {
                                selectedSeasonIndex = idx
                                seasonDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Episodes Header
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Episodes (${currentSeason.episodes.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Auto-play enabled",
                    style = MaterialTheme.typography.bodySmall.copy(color = SoftCyan, fontSize = 11.sp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Episode List
        items(currentSeason.episodes, key = { it.id }) { episode ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                EpisodeCard(
                    episode = episode,
                    onPlay = { onPlayEpisode(item, episode) },
                    onDownload = { viewModel.downloadItem(item, episode) }
                )
            }
        }

        // Recommendations Section ("More Like This")
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "More Series Like This",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendations, key = { it.id }) { rec ->
                    PosterCard(
                        item = rec,
                        onClick = { onContentClick(rec) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
