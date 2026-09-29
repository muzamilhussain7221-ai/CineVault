package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContentItem
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary

@Composable
fun ContentRow(
    title: String,
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null
) {
    if (items.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 0.2.sp
                ),
                color = TextPrimary
            )

            if (onSeeAllClick != null) {
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = SoftCyan,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable(onClick = onSeeAllClick)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Row of Poster Cards
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                PosterCard(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}
