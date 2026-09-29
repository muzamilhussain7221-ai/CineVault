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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.ui.components.AddReviewDialog
import com.example.ui.components.PosterCard
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GlassBorder
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
fun MovieDetailsScreen(
    item: ContentItem,
    viewModel: CineVaultViewModel,
    onBack: () -> Unit,
    onPlay: (ContentItem) -> Unit,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val watchlistIds by viewModel.watchlistIds.collectAsState()
    val allItems by viewModel.allContent.collectAsState()
    val isInWatchlist = watchlistIds.contains(item.id)

    val recommendations = remember(item, allItems) {
        viewModel.repository.getRecommendations(item, allItems)
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("About", "Cast", "More Like This", "Reviews")

    var showReviewDialog by remember { mutableStateOf(false) }

    if (showReviewDialog) {
        AddReviewDialog(
            contentTitle = item.title,
            onSubmit = { rating, comment ->
                viewModel.addReview(item.id, rating, comment)
            },
            onDismiss = { showReviewDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        // Hero Backdrop with back button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
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

                // Cinematic Gradients
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x99080B14),
                                    Color(0x22080B14),
                                    MidnightBackground
                                )
                            )
                        )
                )

                // Top Back and Share Button
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
                                putExtra(Intent.EXTRA_TEXT, "Watch '${item.title}' on CineVault - Your World of Movies & Series!")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Movie"))
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

        // Poster, Title & Metadata
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Poster
                Card(
                    modifier = Modifier
                        .width(105.dp)
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

                // Metadata
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.tagline,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SoftCyan,
                            fontSize = 11.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Badges row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = RatingGold, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format("%.1f", item.rating),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        // Quality Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MidnightSurfaceVariant)
                                .border(0.5.dp, SoftCyan, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(item.qualityBadge, color = SoftCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        // Age
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MidnightSurfaceElevated)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(item.contentRating, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${item.releaseYear} • ${item.durationMinutes} min • ${item.audioFormat}",
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

        // Action Buttons Row (Play, Add to List, Download)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = { onPlay(item) },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Movie", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
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
                        onClick = { viewModel.downloadItem(item) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download", color = Color.White, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onPlay(item) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trailer", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        // Synopsis
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = item.synopsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        lineHeight = 21.sp,
                        fontSize = 13.5.sp
                    )
                )
            }
        }

        // Section Tabs: About, Cast, More Like This, Reviews
        item {
            Spacer(modifier = Modifier.height(16.dp))
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MidnightSurface,
                contentColor = ElectricViolet,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = ElectricViolet,
                        height = 2.5.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.5.sp
                                ),
                                color = if (selectedTabIndex == index) Color.White else TextSecondary
                            )
                        }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTabIndex) {
            0 -> {
                // About
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetadataRow("Director", item.director)
                        MetadataRow("Writers", item.writers.joinToString(", "))
                        MetadataRow("Studio", item.studio)
                        MetadataRow("Audio Format", item.audioFormat)
                        MetadataRow("Languages", item.audioLanguages.joinToString(", "))
                        MetadataRow("Subtitles", item.subtitleLanguages.joinToString(", "))
                    }
                }
            }
            1 -> {
                // Cast
                item {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        if (item.cast.isEmpty()) {
                            Text(
                                text = "Cast information unavailable",
                                color = TextSecondary,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(item.cast, key = { it.id }) { actor ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(85.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(62.dp)
                                                .clip(CircleShape)
                                                .background(MidnightSurfaceElevated)
                                                .border(1.dp, MidnightCardBorder, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = actor.name,
                                                tint = SoftCyan,
                                                modifier = Modifier.size(34.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = actor.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 11.5.sp),
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = actor.characterName,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // More Like This (smart recommendations)
                item {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
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
                }
            }
            3 -> {
                // Reviews
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Community Reviews",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Button(
                                onClick = { showReviewDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MidnightSurfaceElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricViolet),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.RateReview, contentDescription = null, tint = SoftCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Write Review", color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (item.reviews.isEmpty()) {
                            Text(
                                text = "Be the first to review this title!",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            item.reviews.forEach { rev ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(rev.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = RatingGold, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(String.format("%.1f", rev.rating), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(rev.comment, color = TextPrimary, fontSize = 12.sp, lineHeight = 17.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(rev.date, color = TextSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(0.35f))
        Text(text = value, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(0.65f))
    }
}
