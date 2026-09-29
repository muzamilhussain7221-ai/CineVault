package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ContentItem
import com.example.data.model.ContentType
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

@Composable
fun WatchlistScreen(
    viewModel: CineVaultViewModel,
    onContentClick: (ContentItem) -> Unit,
    onPlayClick: (ContentItem) -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.watchlistItems.collectAsState()
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filterTabs = listOf("All Items", "Movies", "Series")

    val filteredItems = remember(items, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> items.filter { it.type == ContentType.MOVIE }
            2 -> items.filter { it.type == ContentType.SERIES }
            else -> items
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "My List",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${items.size} titles saved to your personal vault",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }

        // Filter Tabs
        TabRow(
            selectedTabIndex = selectedFilterIndex,
            containerColor = MidnightSurface,
            contentColor = ElectricViolet,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedFilterIndex]),
                    color = ElectricViolet,
                    height = 2.5.dp
                )
            }
        ) {
            filterTabs.forEachIndexed { idx, label ->
                Tab(
                    selected = selectedFilterIndex == idx,
                    onClick = { selectedFilterIndex = idx },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedFilterIndex == idx) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = if (selectedFilterIndex == idx) Color.White else TextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredItems.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.BookmarkBorder,
                title = "Your Watchlist is Empty",
                description = "Explore our vast catalog of original movies and premium series. Tap '+ My List' to save titles here.",
                actionLabel = "Browse Trending Titles",
                onActionClick = onNavigateHome
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onContentClick(item) },
                        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .background(MidnightSurfaceElevated)
                            ) {
                                AsyncImage(
                                    model = item.posterUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )

                                // Quality Tag
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xCC080B14))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(item.qualityBadge, color = SoftCyan, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${item.releaseYear} • ${if (item.type == ContentType.MOVIE) "Movie" else "Series"}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = TextSecondary
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { onPlayClick(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = ElectricViolet,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.toggleWatchlist(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove from List",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
