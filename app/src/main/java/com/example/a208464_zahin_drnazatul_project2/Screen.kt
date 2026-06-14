package com.example.a208464_zahin_drnazatul_project2


sealed class Screen(val route: String) {
    object Home      : Screen("home")
    object Details   : Screen("details")
    object Apply     : Screen("apply")
    object Chat      : Screen("chat")
    object Activity  : Screen("activity")
    object Profile   : Screen("profile")
    // ★ Project 2 new screens:
    object QrScanner : Screen("qr_scanner")   // Camera sensor — scan resume QR
    object Nearby    : Screen("nearby")        // GPS sensor — jobs near me
}
