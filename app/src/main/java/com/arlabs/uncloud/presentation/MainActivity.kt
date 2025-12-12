package com.arlabs.uncloud.presentation

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.arlabs.uncloud.domain.repository.UserRepository
import com.arlabs.uncloud.presentation.navigation.Screen
import com.arlabs.uncloud.presentation.onboarding.OnboardingScreen
import com.arlabs.uncloud.presentation.settings.SettingsScreen
import com.arlabs.uncloud.presentation.theme.QuitSmokingTheme
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

        // Splash Screen Logic
        // We use mutableStateOf so that the Compose content recomposes when this changes.
        val isReady = androidx.compose.runtime.mutableStateOf(false)
        val startDestination = androidx.compose.runtime.mutableStateOf<String?>(null)

        lifecycleScope.launch {
            // Check if user config exists.
             val config = userRepository.userConfig.first()
             startDestination.value = if (config != null) Screen.Home.route else Screen.Onboarding.route
             isReady.value = true
        }

        // Keep splash screen on screen until we have determined the start destination
        splashScreen.setKeepOnScreenCondition { !isReady.value }

        setContent {
            // Only render content once we are ready.
            if (isReady.value && startDestination.value != null) {
                QuitSmokingTheme {
                    val navController = rememberNavController()

                    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Black) {
                            innerPadding ->
                        NavHost(
                                navController = navController,
                                startDestination = startDestination.value!!,
                                modifier = Modifier.padding(innerPadding)
                        ) {
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
                                        onNavigateToHealth = {
                                            navController.navigate(Screen.Health.route)
                                        },
                                        onNavigateToSettings = {
                                            navController.navigate(Screen.Settings.route)
                                        }
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
                                        onNavigateToPrivacy = {
                                            navController.navigate(Screen.PrivacyPolicy.route)
                                        },
                                        onNavigateToTerms = {
                                            navController.navigate(Screen.TermsOfService.route)
                                        }
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
                        }
                    }
                }
            }
        }
    }
}
