package com.fuza.transformmeai.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fuza.transformmeai.ui.screens.camera.CameraRoute
import com.fuza.transformmeai.ui.screens.login.LoginRoute
import com.fuza.transformmeai.ui.screens.paywall.PaywallRoute
import com.fuza.transformmeai.ui.screens.result.ResultRoute
import com.fuza.transformmeai.ui.screens.scanning.ScanningRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Login.route,
        enterTransition = {
            fadeIn(animationSpec = tween(240)) +
                slideInHorizontally(animationSpec = tween(240)) { it / 12 }
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(animationSpec = tween(200)) { -it / 12 }
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(220)) +
                slideInHorizontally(animationSpec = tween(220)) { -it / 12 }
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(animationSpec = tween(200)) { it / 12 }
        },
    ) {
        composable(AppRoute.Login.route) {
            LoginRoute(
                onAuthenticated = {
                    navController.navigate(AppRoute.Camera.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppRoute.Camera.route) {
            CameraRoute(
                onCaptured = { navController.navigate(AppRoute.Scanning.route) },
            )
        }
        composable(AppRoute.Scanning.route) {
            ScanningRoute(
                onFinished = {
                    navController.navigate(AppRoute.Result.route) {
                        popUpTo(AppRoute.Camera.route) { inclusive = false }
                    }
                },
                onRetryCamera = {
                    navController.popBackStack(AppRoute.Camera.route, inclusive = false)
                },
            )
        }
        composable(AppRoute.Result.route) {
            ResultRoute(
                onLockedLookTapped = { navController.navigate(AppRoute.Paywall.route) },
                onStartOver = {
                    navController.popBackStack(AppRoute.Camera.route, inclusive = false)
                },
            )
        }
        composable(AppRoute.Paywall.route) {
            PaywallRoute(
                onUnlocked = { navController.popBackStack() },
            )
        }
    }
}