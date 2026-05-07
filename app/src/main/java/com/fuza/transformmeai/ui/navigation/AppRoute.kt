package com.fuza.transformmeai.ui.navigation

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")

    data object Camera : AppRoute("camera")

    data object Scanning : AppRoute("scanning")

    data object Result : AppRoute("result")

    data object Paywall : AppRoute("paywall")
}