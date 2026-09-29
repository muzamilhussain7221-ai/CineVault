package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ProfileScreen(
    viewModel: CineVaultViewModel,
    onNavigateSubscription: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onOpenProfileSwitch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.currentProfile.collectAsState()
    val subscriptionTier by viewModel.subscriptionTier.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Active Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(currentProfile.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = currentProfile.name,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentProfile.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (currentProfile.isKids) "Kids Profile • Maturity PG" else "Primary Profile • Maturity TV-MA",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Plan: ${subscriptionTier.planName}",
                            style = MaterialTheme.typography.labelSmall.copy(color = SoftCyan, fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = onOpenProfileSwitch,
                        colors = ButtonDefaults.buttonColors(containerColor = MidnightSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricViolet),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Switch", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Subscription Tier Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onNavigateSubscription),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricViolet)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardMembership, contentDescription = null, tint = SoftCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Current Plan: ${subscriptionTier.planName}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${subscriptionTier.videoQuality} • ${subscriptionTier.supportedDevicesCount} screens • ${subscriptionTier.priceMonthly}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecondary)
                }
            }
        }

        // Account & Preferences Section
        item {
            Text(
                text = "Preferences & System",
                style = MaterialTheme.typography.labelLarge.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.Settings,
                        title = "App & Playback Settings",
                        subtitle = "Autoplay, video quality, storage, cache",
                        onClick = onNavigateSettings
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.SwitchAccount,
                        title = "Manage Profiles",
                        subtitle = "Add or modify viewer profiles and maturity",
                        onClick = onOpenProfileSwitch
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Admin Dashboard",
                        subtitle = "Catalog manager, analytics, publish titles",
                        onClick = onNavigateAdmin
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.Devices,
                        title = "Connected Devices",
                        subtitle = "Pixel 9 Pro (Active), CineVault Web, Living Room TV",
                        onClick = onNavigateSettings
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.HelpOutline,
                        title = "Help & Support",
                        subtitle = "FAQ, contact support, connection check",
                        onClick = onNavigateSettings
                    )
                }
            }
        }

        // Sign Out Button
        item {
            Button(
                onClick = { /* Sign out feedback */ },
                colors = ButtonDefaults.buttonColors(containerColor = MidnightSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Log Out", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out of CineVault", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = SoftCyan, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
            Text(text = subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
    }
}
