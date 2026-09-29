package com.example.ui.screens

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ContentItem
import com.example.data.model.Episode
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    item: ContentItem,
    episode: Episode?,
    viewModel: CineVaultViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember {
        mutableLongStateOf(
            if (episode != null) episode.durationMinutes * 60_000L
            else item.durationMinutes * 60_000L
        )
    }
    var isBuffering by remember { mutableStateOf(false) }
    var isVideoPrepared by remember { mutableStateOf(false) }
    var hasVideoError by remember { mutableStateOf(false) }

    var areControlsVisible by remember { mutableStateOf(true) }
    var isControlsLocked by remember { mutableStateOf(false) }

    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var selectedQuality by remember { mutableStateOf(item.qualityBadge) }
    var selectedSubtitle by remember { mutableStateOf("English [CC]") }
    var selectedAudioTrack by remember { mutableStateOf("English [Original]") }

    var isMuted by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showSubtitleMenu by remember { mutableStateOf(false) }

    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }

    // Subtle cinema ambient breath animation for simulated streaming
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_pulse")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backdrop_scale"
    )

    // Auto-hide controls after 4 seconds of inactivity
    LaunchedEffect(areControlsVisible, isPlaying, isControlsLocked) {
        if (areControlsVisible && isPlaying && !isControlsLocked) {
            delay(4000)
            areControlsVisible = false
        }
    }

    // Playback progress ticker & synchronization
    LaunchedEffect(isPlaying, hasVideoError, isVideoPrepared, playbackSpeed) {
        while (isPlaying) {
            delay((1000L / playbackSpeed.coerceAtLeast(0.25f)).toLong())
            if (isVideoPrepared && !hasVideoError && videoViewRef?.isPlaying == true) {
                try {
                    currentPositionMs = videoViewRef?.currentPosition?.toLong() ?: (currentPositionMs + 1000L)
                    val d = videoViewRef?.duration?.toLong() ?: 0L
                    if (d > 0) durationMs = d
                } catch (e: Exception) {
                    currentPositionMs += (1000L * playbackSpeed).toLong()
                }
            } else {
                // Smooth synthetic playback clock for resilient streaming experience
                currentPositionMs = (currentPositionMs + (1000L * playbackSpeed).toLong()).coerceAtMost(durationMs)
            }

            // Sync with continue watching
            viewModel.recordPlaybackProgress(
                item = item,
                positionMs = currentPositionMs,
                durationMs = durationMs,
                episode = episode
            )
        }
    }

    // On exit, save position
    DisposableEffect(Unit) {
        onDispose {
            viewModel.recordPlaybackProgress(
                item = item,
                positionMs = currentPositionMs,
                durationMs = durationMs,
                episode = episode
            )
            try {
                videoViewRef?.stopPlayback()
            } catch (_: Exception) {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                areControlsVisible = !areControlsVisible
            }
    ) {
        // 1. Cinema Backdrop Canvas Layer (Always visible as fallback when external media is buffering or errored)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(if (episode?.thumbnailUrl?.isNotEmpty() == true) episode.thumbnailUrl else item.backdropUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(if (isPlaying) scaleAnim else 1.0f)
            )

            // Letterbox cinematic vignetting
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x99000000),
                                Color(0x33000000),
                                Color(0x99000000)
                            )
                        )
                    )
            )

            // Live stream quality indicator pill
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x99080B14))
                    .border(0.5.dp, SoftCyan, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) Color(0xFF10B981) else Color(0xFFEF4444))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$selectedQuality • LIVE STREAM",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. Android Video View (Rendered when video is prepared and hasn't faulted)
        if (!hasVideoError) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        val uri = Uri.parse(item.videoUrl)
                        setVideoURI(uri)

                        setOnPreparedListener { mp ->
                            mediaPlayerRef = mp
                            isVideoPrepared = true
                            hasVideoError = false
                            isBuffering = false
                            if (mp.duration > 0) durationMs = mp.duration.toLong()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                try {
                                    mp.playbackParams = PlaybackParams().setSpeed(playbackSpeed)
                                } catch (_: Exception) {}
                            }
                            if (isPlaying) {
                                try { start() } catch (_: Exception) {}
                            }
                        }

                        setOnInfoListener { _, what, _ ->
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) isBuffering = true
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) isBuffering = false
                            true
                        }

                        setOnErrorListener { mp, what, extra ->
                            Log.w("CineVaultPlayer", "MediaPlayer fallback active (code $what, $extra)")
                            hasVideoError = true
                            isVideoPrepared = false
                            isBuffering = false
                            try {
                                mp.reset()
                            } catch (_: Exception) {}
                            true // Consumes error; prevents the native "Can't play this video" popup
                        }

                        videoViewRef = this
                    }
                },
                update = { vv ->
                    if (isVideoPrepared && !hasVideoError) {
                        try {
                            if (isPlaying && !vv.isPlaying) {
                                vv.start()
                            } else if (!isPlaying && vv.isPlaying) {
                                vv.pause()
                            }
                        } catch (_: Exception) {}
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Loading/Buffering Spinner
        if (isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ElectricViolet,
                    modifier = Modifier.size(54.dp),
                    strokeWidth = 4.dp
                )
            }
        }

        // Realistic Simulated Subtitles (Rendered dynamically based on current position)
        if (selectedSubtitle != "Off") {
            val captionText = when {
                currentPositionMs < 15_000L -> "[CineVault Synthesizer Swell • Dolby Atmos]"
                currentPositionMs < 35_000L -> "THORNE: The vault encryption key was breached in Geneva."
                currentPositionMs < 60_000L -> "ARIA: Signal frequency points towards orbit sector 7."
                currentPositionMs < 90_000L -> "COMMANDER: Route power to the primary deflector array."
                currentPositionMs < 120_000L -> "THORNE: We have less than 48 minutes before lockdown."
                else -> "[Cinematic orchestral score continues • 4K UHD]"
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (areControlsVisible) 110.dp else 40.dp)
                    .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = captionText,
                    color = Color.Yellow,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Skip Intro Button (Visible between 5s and 85s)
        if (currentPositionMs in 5_000L..85_000L && !isControlsLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = if (areControlsVisible) 120.dp else 50.dp)
            ) {
                Button(
                    onClick = {
                        val seekTarget = 90_000L
                        currentPositionMs = seekTarget
                        if (isVideoPrepared && !hasVideoError) {
                            try { videoViewRef?.seekTo(seekTarget.toInt()) } catch (_: Exception) {}
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xDD1E2845)),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftCyan)
                ) {
                    Text(
                        text = "Skip Intro >>",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // 3. Full Player Controller Overlay
        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x77000000))
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .align(Alignment.TopCenter),
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
                        Column {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            if (episode != null) {
                                Text(
                                    text = "S${episode.seasonNumber}:E${episode.episodeNumber} - ${episode.title}",
                                    color = SoftCyan,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Top Action Icons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Lock Controls toggle
                        IconButton(onClick = { isControlsLocked = !isControlsLocked }) {
                            Icon(
                                imageVector = if (isControlsLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Lock Controls",
                                tint = if (isControlsLocked) ElectricViolet else Color.White
                            )
                        }

                        if (!isControlsLocked) {
                            // Subtitles & Audio
                            Box {
                                IconButton(onClick = { showSubtitleMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ClosedCaption,
                                        contentDescription = "Audio & Subtitles",
                                        tint = if (selectedSubtitle != "Off") SoftCyan else Color.White
                                    )
                                }
                                DropdownMenu(
                                    expanded = showSubtitleMenu,
                                    onDismissRequest = { showSubtitleMenu = false },
                                    modifier = Modifier.background(MidnightSurfaceElevated)
                                ) {
                                    Text(
                                        "Subtitles",
                                        color = SoftCyan,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                    listOf("English [CC]", "Spanish", "French", "German", "Off").forEach { sub ->
                                        DropdownMenuItem(
                                            text = { Text(sub, color = if (selectedSubtitle == sub) ElectricViolet else Color.White) },
                                            onClick = {
                                                selectedSubtitle = sub
                                                showSubtitleMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Playback Speed
                            Box {
                                IconButton(onClick = { showSpeedMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Playback Speed",
                                        tint = Color.White
                                    )
                                }
                                DropdownMenu(
                                    expanded = showSpeedMenu,
                                    onDismissRequest = { showSpeedMenu = false },
                                    modifier = Modifier.background(MidnightSurfaceElevated)
                                ) {
                                    Text(
                                        "Playback Speed",
                                        color = SoftCyan,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                        DropdownMenuItem(
                                            text = { Text("${speed}x", color = if (playbackSpeed == speed) ElectricViolet else Color.White) },
                                            onClick = {
                                                playbackSpeed = speed
                                                if (isVideoPrepared && !hasVideoError && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    try {
                                                        mediaPlayerRef?.playbackParams = PlaybackParams().setSpeed(speed)
                                                    } catch (_: Exception) {}
                                                }
                                                showSpeedMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Quality Selector
                            Box {
                                IconButton(onClick = { showQualityMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Video Quality",
                                        tint = Color.White
                                    )
                                }
                                DropdownMenu(
                                    expanded = showQualityMenu,
                                    onDismissRequest = { showQualityMenu = false },
                                    modifier = Modifier.background(MidnightSurfaceElevated)
                                ) {
                                    Text(
                                        "Video Quality",
                                        color = SoftCyan,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                    listOf("2160p 4K UHD", "1440p QHD", "1080p Full HD", "720p HD", "480p", "Auto").forEach { q ->
                                        DropdownMenuItem(
                                            text = { Text(q, color = if (selectedQuality == q) ElectricViolet else Color.White) },
                                            onClick = {
                                                selectedQuality = q
                                                showQualityMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Cast icon placeholder
                            IconButton(onClick = { /* Cast placeholder */ }) {
                                Icon(
                                    imageVector = Icons.Default.Cast,
                                    contentDescription = "Cast Screen",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Center Play / 10s Rewind / 10s Forward (Hidden when locked)
                if (!isControlsLocked) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 10s
                        IconButton(
                            onClick = {
                                val target = (currentPositionMs - 10_000L).coerceAtLeast(0L)
                                currentPositionMs = target
                                if (isVideoPrepared && !hasVideoError) {
                                    try { videoViewRef?.seekTo(target.toInt()) } catch (_: Exception) {}
                                }
                            },
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Rewind 10 seconds",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Play/Pause Main
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(ElectricViolet)
                                .clickable {
                                    isPlaying = !isPlaying
                                    if (isVideoPrepared && !hasVideoError) {
                                        try {
                                            if (isPlaying) videoViewRef?.start() else videoViewRef?.pause()
                                        } catch (_: Exception) {}
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        // Forward 10s
                        IconButton(
                            onClick = {
                                val target = (currentPositionMs + 10_000L).coerceAtMost(durationMs)
                                currentPositionMs = target
                                if (isVideoPrepared && !hasVideoError) {
                                    try { videoViewRef?.seekTo(target.toInt()) } catch (_: Exception) {}
                                }
                            },
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Forward 10 seconds",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                // Bottom Controls: Time, Scrub Bar, Volume & Quality
                if (!isControlsLocked) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        // Slider Progress
                        Slider(
                            value = currentPositionMs.toFloat(),
                            onValueChange = { pos ->
                                currentPositionMs = pos.toLong()
                                if (isVideoPrepared && !hasVideoError) {
                                    try { videoViewRef?.seekTo(pos.toInt()) } catch (_: Exception) {}
                                }
                            },
                            valueRange = 0f..durationMs.coerceAtLeast(1L).toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricViolet,
                                activeTrackColor = ElectricViolet,
                                inactiveTrackColor = Color(0x44FFFFFF)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Timestamps & Quick Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${formatTime(currentPositionMs)} / ${formatTime(durationMs)}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Mute toggle
                                IconButton(
                                    onClick = {
                                        isMuted = !isMuted
                                        val vol = if (isMuted) 0f else 1f
                                        try {
                                            mediaPlayerRef?.setVolume(vol, vol)
                                        } catch (_: Exception) {}
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                        contentDescription = "Mute",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Quality Tag
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MidnightSurfaceElevated)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(selectedQuality, color = SoftCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
