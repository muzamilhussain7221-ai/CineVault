package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletLight
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.SoftCyanBright

@Composable
fun CineVaultLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false,
    sizeCompact: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Glowing Vault Emblem
        Box(
            modifier = Modifier
                .size(if (sizeCompact) 28.dp else 34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(ElectricViolet, SoftCyan)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "CineVault Logo",
                tint = Color.White,
                modifier = Modifier.size(if (sizeCompact) 18.dp else 22.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Brand Typography
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "CINE",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = if (sizeCompact) 17.sp else 21.sp,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            )
            Text(
                text = "VAULT",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = if (sizeCompact) 17.sp else 21.sp,
                    letterSpacing = 1.sp,
                    color = SoftCyanBright
                )
            )
        }
    }
}
