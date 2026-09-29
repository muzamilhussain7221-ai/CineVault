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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.SubscriptionTier
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletDark
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

@Composable
fun SubscriptionScreen(
    viewModel: CineVaultViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val currentTier by viewModel.subscriptionTier.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 60.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Subscription & Plans",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        // Subtitle banner
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(
                    text = "Unlock Ultra HD Cinema",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose the ideal CineVault streaming plan for you and your family. Switch or cancel anytime.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp)
                )
            }
        }

        // Tier cards
        SubscriptionTier.entries.forEach { tier ->
            item {
                val isSelected = currentTier == tier
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectSubscriptionTier(tier) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MidnightSurfaceVariant else MidnightSurfaceElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) ElectricViolet else MidnightCardBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = tier.planName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                                    color = Color.White
                                )
                                Text(
                                    text = "${tier.resolutionLabel} • ${tier.audioQuality}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SoftCyan, fontSize = 11.sp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = tier.priceMonthly,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = if (isSelected) ElectricViolet else Color.White
                                    )
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ElectricVioletDark)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("ACTIVE PLAN", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Features List
                        tier.benefits.forEach { benefit ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) ElectricViolet else MidnightSurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = benefit,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 12.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.selectSubscriptionTier(tier) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) ElectricViolet else MidnightSurfaceElevated
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isSelected) "Current Active Tier" else "Switch to ${tier.planName}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Billing History Simulation
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceElevated),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = SoftCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recent Billing Receipts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        "Sep 15, 2026 • Premium 4K Ultra • $14.99 • Paid",
                        "Aug 15, 2026 • Premium 4K Ultra • $14.99 • Paid",
                        "Jul 15, 2026 • Premium 4K Ultra • $14.99 • Paid"
                    ).forEach { receipt ->
                        Text(
                            text = receipt,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.5.sp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
