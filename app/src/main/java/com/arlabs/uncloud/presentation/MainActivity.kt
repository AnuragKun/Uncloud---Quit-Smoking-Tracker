package com.arlabs.uncloud.presentation

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.arlabs.uncloud.domain.repository.UserRepository
import com.arlabs.uncloud.presentation.navigation.Screen
import com.arlabs.uncloud.presentation.onboarding.OnboardingScreen
import com.arlabs.uncloud.presentation.panic.PanicScreen
import com.arlabs.uncloud.presentation.settings.SettingsScreen
import com.arlabs.uncloud.presentation.theme.QuitSmokingTheme
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
                navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )

        // WorkManager Logic (unchanged)
        val workRequest =
                androidx.work.PeriodicWorkRequestBuilder<
                                com.arlabs.uncloud.worker.DailyMotivationWorker>(
                                24,
                                java.util.concurrent.TimeUnit.HOURS
                        )
                        .build()

        androidx.work.WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                        "DailyMotivationWork",
                        androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                        workRequest
                )

        val isReady = androidx.compose.runtime.mutableStateOf(false)
        val startDestination = androidx.compose.runtime.mutableStateOf<String?>(null)

        lifecycleScope.launch {
             val config = userRepository.userConfig.first()
             startDestination.value = if (config != null) Screen.Home.route else Screen.Onboarding.route
             isReady.value = true
        }

        splashScreen.setKeepOnScreenCondition { !isReady.value }

        setContent {
            if (isReady.value && startDestination.value != null) {
                QuitSmokingTheme {
                    val navController = rememberNavController()
                    
                    // Bottom Navigation Logic
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = currentBackStackEntry?.destination
                    
                    // Show BottomBar only on main screens
                    val showBottomBar = currentDestination?.route in listOf(
                        Screen.Home.route, 
                        Screen.Protocol.route
                    ) // Panic is fullscreen, so no bottom bar there usually? User said "move panic button to bottom nav". 
                    // Wait, if Panic is in Bottom Nav, it acts as a tab. 
                    
                    Scaffold(
                        modifier = Modifier.fillMaxSize(), 
                        containerColor = Color.Black,
                        bottomBar = {
                            // Only show if needed
                            if (showBottomBar || currentDestination?.route == Screen.Panic.route) {

                                // Premium Container
                                NavigationBar(
                                    containerColor = Color(0xFF0F1216), // Deep Dark Background
                                    contentColor = Color(0xFF8B9BB4),   // Unselected Icon Color (Slate Grey)
                                    tonalElevation = 0.dp,              // Remove default shadow
                                    modifier = Modifier.border(
                                        width = 1.dp,
                                        color = Color(0xFF252A30),      // Subtle Top Border
                                        shape = RectangleShape
                                    )
                                ) {

                                    // 1. MONITOR (Home)
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                                        label = { Text("Monitor", fontWeight = FontWeight.SemiBold) },
                                        selected = currentDestination?.route == Screen.Home.route,
                                        onClick = {
                                            navController.navigate(Screen.Home.route) {
                                                launchSingleTop = true
                                                popUpTo(Screen.Home.route)
                                            }
                                        },
                                        // Cyan Glow Theme
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFF00E5FF), // Cyan Icon
                                            selectedTextColor = Color(0xFF00E5FF), // Cyan Text
                                            indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.15f), // Subtle Cyan Glow
                                            unselectedIconColor = Color(0xFF8B9BB4),
                                            unselectedTextColor = Color(0xFF8B9BB4)
                                        )
                                    )

                                    // 2. SHIELD (Panic) - The "Red" Danger Button
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Rounded.Warning, contentDescription = "Panic") },
                                        label = { Text("Shield", fontWeight = FontWeight.SemiBold) },
                                        selected = currentDestination?.route == Screen.Panic.route,
                                        onClick = {
                                            navController.navigate(Screen.Panic.route) { launchSingleTop = true }
                                        },
                                        // Red Alert Theme
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFFFF5252), // Bright Red Icon
                                            selectedTextColor = Color(0xFFFF5252), // Bright Red Text
                                            indicatorColor = Color(0xFFFF5252).copy(alpha = 0.15f), // Red Glow
                                            unselectedIconColor = Color(0xFF8B9BB4), // Still Grey when inactive
                                            unselectedTextColor = Color(0xFF8B9BB4)
                                        )
                                    )

                                    // 3. PROTOCOL (Timeline/Ranking)
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Rounded.DateRange, contentDescription = "Protocol") },
                                        label = { Text("Manage", fontWeight = FontWeight.SemiBold) },
                                        selected = currentDestination?.route == Screen.Protocol.route,
                                        onClick = {
                                            navController.navigate(Screen.Protocol.route) { launchSingleTop = true }
                                        },
                                        // Cyan Glow Theme
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFF00E5FF),
                                            selectedTextColor = Color(0xFF00E5FF),
                                            indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
                                            unselectedIconColor = Color(0xFF8B9BB4),
                                            unselectedTextColor = Color(0xFF8B9BB4)
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                                navController = navController,
                                startDestination = startDestination.value!!,
                                modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(
                                route = Screen.Panic.route,
                                deepLinks = listOf(navDeepLink { uriPattern = "uncloud://panic" })
                            ) {
                                PanicScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.Onboarding.route) {
                                OnboardingScreen(
                                        onNavigateToHome = {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Onboarding.route) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                )
                            }
                            composable(Screen.Home.route) {
                                com.arlabs.uncloud.presentation.home.HomeScreen(
                                        onNavigateToHealth = { navController.navigate(Screen.Health.route) },
                                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                                        onNavigateToRanking = { navController.navigate(Screen.Ranking.route) },
                                        onNavigateToPanic = { navController.navigate(Screen.Panic.route) } // Keep for widget/redundancy
                                )
                            }
                            composable(Screen.Protocol.route) {
                                com.arlabs.uncloud.presentation.protocol.ProtocolScreen(
                                    onNavigateToBreach = { navController.navigate(Screen.ReportBreach.route) }
                                )
                            }
                            composable(Screen.ReportBreach.route) {
                                com.arlabs.uncloud.presentation.protocol.ReportBreachScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.Health.route) {
                                com.arlabs.uncloud.presentation.health.HealthScreen(
                                        onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) },
                                        onNavigateToTerms = { navController.navigate(Screen.TermsOfService.route) }
                                )
                            }
                            composable(Screen.PrivacyPolicy.route) {
                                com.arlabs.uncloud.presentation.legal.PrivacyPolicyScreen(
                                        onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.TermsOfService.route) {
                                com.arlabs.uncloud.presentation.legal.TermsOfServiceScreen(
                                        onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.Ranking.route) {
                                com.arlabs.uncloud.presentation.ranking.RankingScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
