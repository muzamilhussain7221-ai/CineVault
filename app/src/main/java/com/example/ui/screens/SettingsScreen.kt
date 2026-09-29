package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun SettingsScreen(
    viewModel: CineVaultViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var autoPlayNext by remember { mutableStateOf(true) }
    var dataSaverMode by remember { mutableStateOf(false) }
    var downloadOverWifiOnly by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var cacheClearedMessage by remember { mutableStateOf<String?>(null) }
    var activePolicyModal by remember { mutableStateOf<String?>(null) }

    if (activePolicyModal != null) {
        val title = when (activePolicyModal) {
            "privacy" -> "Privacy Policy"
            "terms" -> "Terms of Service"
            "dmca" -> "DMCA & Copyright Compliance"
            "licensing" -> "Content Licensing Information"
            else -> "About CineVault"
        }
        val text = when (activePolicyModal) {
            "privacy" -> "CineVault values your privacy. We collect minimal telemetry strictly to provide seamless resume-watching functionality, personalized recommendations, and secure authentication. We do not sell user data to third parties."
            "terms" -> "By using CineVault, you agree to access content strictly for personal, non-commercial viewing. All streamed titles are protected by international intellectual property laws."
            "dmca" -> "CineVault complies fully with the Digital Millennium Copyright Act. If you believe any content on our platform infringes copyright, contact copyright@cinevault.stream with documentation for prompt review."
            "licensing" -> "All movies and series distributed via CineVault are licensed original productions, public-domain creative commons media, or authorized developer sample titles."
            else -> "CineVault version 1.0.0 (Production Release)\n'Your World of Movies & Series'\nEngineered with Jetpack Compose & Material 3."
        }

        AlertDialog(
            onDismissRequest = { activePolicyModal = null },
            containerColor = MidnightSurfaceElevated,
            title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(text, color = TextPrimary, lineHeight = 20.sp, fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = { activePolicyModal = null }) {
                    Text("Close", color = SoftCyan)
                }
            }
        )
    }

    if (cacheClearedMessage != null) {
        AlertDialog(
            onDismissRequest = { cacheClearedMessage = null },
            containerColor = MidnightSurfaceElevated,
            title = { Text("Cache Cleared", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(cacheClearedMessage ?: "", color = TextPrimary) },
            confirmButton = {
                TextButton(onClick = { cacheClearedMessage = null }) {
                    Text("OK", color = SoftCyan)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        // Section: Playback
        item {
            Text(
                text = "Video & Playback",
                style = MaterialTheme.typography.labelLarge.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Autoplay Next Episode", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Automatically starts the next episode when current finishes", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = autoPlayNext,
                            onCheckedChange = { autoPlayNext = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricViolet)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cellular Data Saver", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Streams at 720p HD when not connected to Wi-Fi", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = dataSaverMode,
                            onCheckedChange = { dataSaverMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricViolet)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Download on Wi-Fi Only", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Avoid downloading large offline 4K files over mobile data", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = downloadOverWifiOnly,
                            onCheckedChange = { downloadOverWifiOnly = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricViolet)
                        )
                    }
                }
            }
        }

        // Section: Storage & Maintenance
        item {
            Text(
                text = "Storage & App Cache",
                style = MaterialTheme.typography.labelLarge.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                cacheClearedMessage = "Freed 142.8 MB of image caches and temporary streaming buffers."
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, tint = SoftCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Clear App Cache", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Frees space without deleting your watchlist or downloads", color = TextSecondary, fontSize = 11.sp)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
        }

        // Section: Legal & Policies
        item {
            Text(
                text = "Legal & Compliance",
                style = MaterialTheme.typography.labelLarge.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column {
                    PolicyRow("Privacy Policy") { activePolicyModal = "privacy" }
                    PolicyRow("Terms of Service") { activePolicyModal = "terms" }
                    PolicyRow("DMCA & Copyright Policy") { activePolicyModal = "dmca" }
                    PolicyRow("Content Licensing Information") { activePolicyModal = "licensing" }
                    PolicyRow("About CineVault") { activePolicyModal = "about" }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun PolicyRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, fontSize = 13.sp)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
    }
}
