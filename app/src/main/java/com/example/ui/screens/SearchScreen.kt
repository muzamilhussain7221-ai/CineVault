package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContentItem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PosterCard
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: CineVaultViewModel,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchState by viewModel.searchState.collectAsState()
    val filterOptions = listOf("All", "Movies", "Series", "4K UHD", "Action", "Sci-Fi", "Drama")
    val sortOptions = listOf("Trending", "Top Rated", "Newest")
    val trendingTags = listOf("Neon Horizon", "Cyberpunk", "Last Signal", "4K Sci-Fi", "Space", "Heist")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .padding(top = 12.dp)
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = searchState.query,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search movies, series, actors, directors...", color = TextSecondary, fontSize = 13.5.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = SoftCyan
                )
            },
            trailingIcon = {
                if (searchState.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MidnightSurfaceVariant,
                unfocusedContainerColor = MidnightSurfaceElevated,
                focusedBorderColor = ElectricViolet,
                unfocusedBorderColor = MidnightCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { filter ->
                val isSelected = searchState.selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onSearchFilterSelected(filter) },
                    label = {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricViolet,
                        selectedLabelColor = Color.White,
                        containerColor = MidnightSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) ElectricViolet else MidnightCardBorder,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        // Sort By Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort by: ",
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            sortOptions.forEach { sort ->
                val isSelected = searchState.sortBy == sort
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) MidnightSurfaceVariant else Color.Transparent)
                        .border(
                            0.5.dp,
                            if (isSelected) SoftCyan else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { viewModel.onSearchSortSelected(sort) }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = sort,
                        color = if (isSelected) SoftCyan else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Search Content / Results Grid or Suggestions
        if (searchState.query.isEmpty() && searchState.selectedFilter == "All") {
            // Trending and Recent Searches view
            Column(modifier = Modifier.padding(16.dp)) {
                // Trending searches
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = SoftCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Trending Searches",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trendingTags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MidnightSurfaceElevated)
                                .border(1.dp, MidnightCardBorder, RoundedCornerShape(20.dp))
                                .clickable {
                                    viewModel.onSearchQueryChanged(tag)
                                    viewModel.addRecentSearch(tag)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = tag, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }

                // Recent searches
                if (searchState.recentSearches.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recent Searches",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Clear All",
                            color = SoftCyan,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable { viewModel.clearRecentSearches() }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    searchState.recentSearches.forEach { query ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onSearchQueryChanged(query) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = query, color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else if (searchState.results.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = "No results found for '${searchState.query}'",
                description = "Try searching with a different title, actor, genre, or change your filters.",
                actionLabel = "Reset Filters",
                onActionClick = {
                    viewModel.onSearchQueryChanged("")
                    viewModel.onSearchFilterSelected("All")
                }
            )
        } else {
            // Results Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 130.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchState.results, key = { it.id }) { item ->
                    PosterCard(
                        item = item,
                        onClick = {
                            viewModel.addRecentSearch(item.title)
                            onContentClick(item)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
