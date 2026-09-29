package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

@Composable
fun NotificationsScreen(
    viewModel: CineVaultViewModel,
    onBack: () -> Unit,
    onNavigateContent: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val notifications by viewModel.notifications.collectAsState()
    val allContent by viewModel.allContent.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            if (notifications.isNotEmpty()) {
                Text(
                    text = "Mark All Read",
                    style = MaterialTheme.typography.labelSmall.copy(color = SoftCyan, fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { viewModel.markAllNotificationsRead() }
                )
            }
        }

        if (notifications.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.NotificationsNone,
                title = "No New Notifications",
                description = "You're all caught up! When new seasons drop or premieres arrive, you'll see them here."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    val targetItem = allContent.find { it.id == notif.targetContentId }
                    val icon = when (notif.type) {
                        NotificationType.EPISODE_DROP -> Icons.Default.Tv
                        NotificationType.NEW_RELEASE -> Icons.Default.Movie
                        NotificationType.CONTINUE_WATCHING -> Icons.Default.PlayCircle
                        NotificationType.SUBSCRIPTION -> Icons.Default.CardMembership
                        NotificationType.SYSTEM -> Icons.Default.NotificationsActive
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.markNotificationRead(notif.id)
                                if (targetItem != null) onNavigateContent(targetItem)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notif.isRead) MidnightSurfaceVariant else MidnightSurfaceElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!notif.isRead) ElectricViolet else MidnightCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (!notif.isRead) ElectricViolet else MidnightSurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.5.sp
                                        ),
                                        color = Color.White
                                    )
                                    Text(
                                        text = notif.timeAgo,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (!notif.isRead) TextPrimary else TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
