package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContentItem
import com.example.data.model.ContentType
import com.example.ui.components.AddProfileDialog
import com.example.ui.components.SwitchProfileDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MovieDetailsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SeriesDetailsScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.CineVaultTheme
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

enum class CineVaultDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    SEARCH("Search", Icons.Filled.Search, Icons.Filled.Search, "nav_search"),
    WATCHLIST("My List", Icons.Filled.Bookmark, Icons.Filled.BookmarkBorder, "nav_watchlist"),
    DOWNLOADS("Downloads", Icons.Filled.Download, Icons.Filled.FileDownload, "nav_downloads"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Filled.PersonOutline, "nav_profile")
}

enum class SubScreen {
    NONE,
    MOVIE_DETAILS,
    SERIES_DETAILS,
    PLAYER,
    NOTIFICATIONS,
    SUBSCRIPTION,
    SETTINGS,
    ADMIN
}

class MainActivity : ComponentActivity() {

    private val viewModel: CineVaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineVaultTheme {
                CineVaultApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CineVaultApp(viewModel: CineVaultViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    var currentDestination by remember { mutableStateOf(CineVaultDestination.HOME) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }

    val selectedContent by viewModel.selectedContent.collectAsState()
    val selectedEpisode by viewModel.selectedEpisode.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()

    var showSwitchProfileDialog by remember { mutableStateOf(false) }
    var showAddProfileDialog by remember { mutableStateOf(false) }

    // Listen to ViewModel user messages (Snackbars)
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Dialogs
    if (showSwitchProfileDialog) {
        SwitchProfileDialog(
            profiles = profiles,
            currentProfile = currentProfile,
            onSelectProfile = { viewModel.switchProfile(it) },
            onAddNewProfileClick = { showAddProfileDialog = true },
            onDismiss = { showSwitchProfileDialog = false }
        )
    }

    if (showAddProfileDialog) {
        AddProfileDialog(
            onAddProfile = { name, isKids ->
                viewModel.addProfile(name, isKids)
            },
            onDismiss = { showAddProfileDialog = false }
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MidnightBackground)) {
        val isExpandedScreen = maxWidth >= 600.dp
        val isPlayerScreen = currentSubScreen == SubScreen.PLAYER

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (!isExpandedScreen && !isPlayerScreen && currentSubScreen == SubScreen.NONE) {
                    NavigationBar(
                        containerColor = MidnightSurface,
                        contentColor = TextPrimary,
                        modifier = Modifier.navigationBarsPadding().testTag("bottom_nav_bar")
                    ) {
                        CineVaultDestination.entries.forEach { dest ->
                            val isSelected = currentDestination == dest
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    currentDestination = dest
                                    currentSubScreen = SubScreen.NONE
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.title,
                                        tint = if (isSelected) ElectricViolet else TextSecondary
                                    )
                                },
                                label = {
                                    Text(
                                        text = dest.title,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MidnightBackground
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Adaptive Navigation Rail on large screens / tablets
                if (isExpandedScreen && !isPlayerScreen && currentSubScreen == SubScreen.NONE) {
                    NavigationRail(
                        containerColor = MidnightSurface,
                        contentColor = TextPrimary,
                        modifier = Modifier.testTag("nav_rail")
                    ) {
                        CineVaultDestination.entries.forEach { dest ->
                            val isSelected = currentDestination == dest
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    currentDestination = dest
                                    currentSubScreen = SubScreen.NONE
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.title,
                                        tint = if (isSelected) ElectricViolet else TextSecondary
                                    )
                                },
                                label = { Text(dest.title, color = if (isSelected) Color.White else TextSecondary) },
                                colors = NavigationRailItemDefaults.colors(
                                    indicatorColor = MidnightBackground
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                // Main Content View
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    AnimatedContent(
                        targetState = currentSubScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { subScreen ->
                        when (subScreen) {
                            SubScreen.MOVIE_DETAILS -> {
                                selectedContent?.let { movie ->
                                    MovieDetailsScreen(
                                        item = movie,
                                        viewModel = viewModel,
                                        onBack = { currentSubScreen = SubScreen.NONE },
                                        onPlay = {
                                            viewModel.selectContent(it)
                                            currentSubScreen = SubScreen.PLAYER
                                        },
                                        onContentClick = {
                                            viewModel.selectContent(it)
                                            currentSubScreen = if (it.type == ContentType.MOVIE) SubScreen.MOVIE_DETAILS else SubScreen.SERIES_DETAILS
                                        }
                                    )
                                } ?: run {
                                    currentSubScreen = SubScreen.NONE
                                }
                            }
                            SubScreen.SERIES_DETAILS -> {
                                selectedContent?.let { series ->
                                    SeriesDetailsScreen(
                                        item = series,
                                        viewModel = viewModel,
                                        onBack = { currentSubScreen = SubScreen.NONE },
                                        onPlayEpisode = { s, ep ->
                                            viewModel.selectContent(s)
                                            viewModel.selectEpisode(ep)
                                            currentSubScreen = SubScreen.PLAYER
                                        },
                                        onContentClick = {
                                            viewModel.selectContent(it)
                                            currentSubScreen = if (it.type == ContentType.MOVIE) SubScreen.MOVIE_DETAILS else SubScreen.SERIES_DETAILS
                                        }
                                    )
                                } ?: run {
                                    currentSubScreen = SubScreen.NONE
                                }
                            }
                            SubScreen.PLAYER -> {
                                selectedContent?.let { content ->
                                    PlayerScreen(
                                        item = content,
                                        episode = selectedEpisode,
                                        viewModel = viewModel,
                                        onBack = { currentSubScreen = SubScreen.NONE }
                                    )
                                } ?: run {
                                    currentSubScreen = SubScreen.NONE
                                }
                            }
                            SubScreen.NOTIFICATIONS -> {
                                NotificationsScreen(
                                    viewModel = viewModel,
                                    onBack = { currentSubScreen = SubScreen.NONE },
                                    onNavigateContent = {
                                        viewModel.selectContent(it)
                                        currentSubScreen = if (it.type == ContentType.MOVIE) SubScreen.MOVIE_DETAILS else SubScreen.SERIES_DETAILS
                                    }
                                )
                            }
                            SubScreen.SUBSCRIPTION -> {
                                SubscriptionScreen(
                                    viewModel = viewModel,
                                    onBack = { currentSubScreen = SubScreen.NONE }
                                )
                            }
                            SubScreen.SETTINGS -> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { currentSubScreen = SubScreen.NONE }
                                )
                            }
                            SubScreen.ADMIN -> {
                                AdminScreen(
                                    viewModel = viewModel,
                                    onBack = { currentSubScreen = SubScreen.NONE }
                                )
                            }
                            SubScreen.NONE -> {
                                // Primary Bottom Navigation Destinations
                                when (currentDestination) {
                                    CineVaultDestination.HOME -> {
                                        HomeScreen(
                                            viewModel = viewModel,
                                            onContentClick = {
                                                viewModel.selectContent(it)
                                                currentSubScreen = if (it.type == ContentType.MOVIE) SubScreen.MOVIE_DETAILS else SubScreen.SERIES_DETAILS
                                            },
                                            onPlayClick = {
                                                viewModel.selectContent(it)
                                                currentSubScreen = SubScreen.PLAYER
                                            },
                                            onNavigateSearch = {
                                                currentDestination = CineVaultDestination.SEARCH
                                            },
                                            onNavigateNotifications = {
                                                currentSubScreen = SubScreen.NOTIFICATIONS
                                            },
                                            onNavigateAdmin = {
                                                currentSubScreen = SubScreen.ADMIN
                                            },
                                            onOpenProfileSwitch = {
                                                showSwitchProfileDialog = true
                                            }
                                        )
                                    }
                                    CineVaultDestination.SEARCH -> {
                                        SearchScreen(
                                            viewModel = viewModel,
                                            onContentClick = {
                                                viewModel.selectContent(it)
                                                currentSubScreen = if (it.type == ContentType.MOVIE) SubScreen.MOVIE_DETAILS else SubScreen.SERIES_DETAILS
                                            }
                                        )
                                    }
                                    CineVaultDestination.WATCHLIST -> {
                                        WatchlistScreen(
                                            viewModel = viewModel,
                                            onContentClick = {
                                                viewModel.selectContent(it)
                                                currentSubScreen = if (it.type == ContentType.MOVIE) SubScreen.MOVIE_DETAILS else SubScreen.SERIES_DETAILS
                                            },
                                            onPlayClick = {
                                                viewModel.selectContent(it)
                                                currentSubScreen = SubScreen.PLAYER
                                            },
                                            onNavigateHome = {
                                                currentDestination = CineVaultDestination.HOME
                                            }
                                        )
                                    }
                                    CineVaultDestination.DOWNLOADS -> {
                                        DownloadsScreen(
                                            viewModel = viewModel,
                                            onPlayContent = {
                                                viewModel.selectContent(it)
                                                currentSubScreen = SubScreen.PLAYER
                                            },
                                            onNavigateHome = {
                                                currentDestination = CineVaultDestination.HOME
                                            }
                                        )
                                    }
                                    CineVaultDestination.PROFILE -> {
                                        ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateSubscription = { currentSubScreen = SubScreen.SUBSCRIPTION },
                                            onNavigateSettings = { currentSubScreen = SubScreen.SETTINGS },
                                            onNavigateAdmin = { currentSubScreen = SubScreen.ADMIN },
                                            onOpenProfileSwitch = { showSwitchProfileDialog = true }
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
