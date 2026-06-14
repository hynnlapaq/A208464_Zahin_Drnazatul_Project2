package com.example.a208464_zahin_drnazatul_project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a208464_zahin_drnazatul_project2.ui.theme.A208464_zahin_drnazatul_project2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A208464_zahin_drnazatul_project2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MaterialTheme.colorScheme.background
                ) {
                    JobHubApp()
                }
            }
        }
    }
}

@Composable
fun JobHubApp(navController: NavHostController = rememberNavController()) {

    val application = LocalContext.current.applicationContext as JobApplication
    val jobViewModel: JobViewModel = viewModel(
        factory = JobViewModel.factory(application.repository)
    )

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        // ── Screen 1: Home ────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel      = jobViewModel,
                onJobClick     = { job ->
                    jobViewModel.onJobSelected(job)
                    navController.navigate(Screen.Details.route)
                },
                onActivityClick = { navController.navigate(Screen.Activity.route) },
                onProfileClick  = { navController.navigate(Screen.Profile.route) },
                onNearbyClick   = { navController.navigate(Screen.Nearby.route) }
            )
        }

        // ── Screen 2: Job Detail ──────────────────────────────────────────────
        composable(Screen.Details.route) {
            JobDetailScreen(
                viewModel    = jobViewModel,
                onBack       = { navController.popBackStack() },
                onApplyClick = { navController.navigate(Screen.Apply.route) },
                onChatClick  = { navController.navigate(Screen.Chat.route) }
            )
        }

        // ── Screen 3: Apply ───────────────────────────────────────────────────
        composable(Screen.Apply.route) {
            ApplyScreen(
                viewModel       = jobViewModel,
                onBack          = { navController.popBackStack() },
                onSubmitSuccess = {
                    navController.navigate(Screen.Activity.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onScanQr = { navController.navigate(Screen.QrScanner.route) }
            )
        }

        // ── Screen 4: Chat ────────────────────────────────────────────────────
        composable(Screen.Chat.route) {
            ChatScreen(viewModel = jobViewModel, onBack = { navController.popBackStack() })
        }

        // ── Screen 5: Activity ────────────────────────────────────────────────
        composable(Screen.Activity.route) {
            ActivityScreen(
                viewModel      = jobViewModel,
                onBack         = { navController.popBackStack() },
                onHomeClick    = { navController.navigate(Screen.Home.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        // ── Screen 6: Profile ─────────────────────────────────────────────────
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel       = jobViewModel,
                onBack          = { navController.popBackStack() },
                onHomeClick     = { navController.navigate(Screen.Home.route) },
                onActivityClick = { navController.navigate(Screen.Activity.route) }
            )
        }

        // ── Screen 7: Nearby Jobs (GPS) ───────────────────────────────────────
        composable(Screen.Nearby.route) {
            NearbyJobsScreen(
                viewModel  = jobViewModel,
                onBack     = { navController.popBackStack() },
                onJobClick = { job ->
                    jobViewModel.onJobSelected(job)
                    navController.navigate(Screen.Details.route)
                }
            )
        }

        // ── Screen 8: QR Scanner (Camera) ────────────────────────────────────
        composable(Screen.QrScanner.route) {
            QrScannerScreen(
                viewModel       = jobViewModel,
                onBack          = { navController.popBackStack() },
                onScanSuccess   = { navController.popBackStack() }
            )
        }
    }
}
