package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ContentType
import com.example.data.model.SubscriptionTier
import com.example.data.model.UserProfile
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletLight
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.RatingGold
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SwitchProfileDialog(
    profiles: List<UserProfile>,
    currentProfile: UserProfile,
    onSelectProfile: (UserProfile) -> Unit,
    onAddNewProfileClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MidnightSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Who's Watching?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    ),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    profiles.forEach { profile ->
                        val isSelected = profile.id == currentProfile.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MidnightSurfaceVariant else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricViolet else MidnightCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onSelectProfile(profile)
                                    onDismiss()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(profile.avatarColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = profile.name,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (profile.isKids) "Kids Profile (PG)" else "Maturity: ${profile.maturityRating}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = SoftCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Add Profile Button
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onAddNewProfileClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricVioletLight)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Profile")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun AddProfileDialog(
    onAddProfile: (name: String, isKids: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isKids by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightSurfaceElevated,
        title = {
            Text("Create New Profile", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Profile Name", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isKids = !isKids }
                ) {
                    Checkbox(
                        checked = isKids,
                        onCheckedChange = { isKids = it },
                        colors = CheckboxDefaults.colors(checkedColor = SoftCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Kids Experience (Age-appropriate ratings only)",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAddProfile(name.trim(), isKids)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("Create Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddReviewDialog(
    contentTitle: String,
    onSubmit: (rating: Float, comment: String) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableFloatStateOf(9.0f) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightSurfaceElevated,
        title = {
            Text("Review $contentTitle", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Rating:", color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = RatingGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(String.format("%.1f / 10", rating), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 1f..10f,
                    steps = 17,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricVioletLight,
                        activeTrackColor = ElectricViolet
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Write your thoughts...", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (comment.isNotBlank()) {
                        onSubmit(rating, comment.trim())
                        onDismiss()
                    }
                },
                enabled = comment.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("Post Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
