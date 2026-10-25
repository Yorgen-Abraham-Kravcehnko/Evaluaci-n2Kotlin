package com.example.ev2rinconsur.navigation

sealed class Route(val path: String) {
    data object Login    : Route("login")
    data object Register : Route("register")
    data object Home     : Route("home")
    data object Profile  : Route("profile")
    data object Totals   : Route("totals")
    data object History  : Route("history")
    data object Crud     : Route("crud")
}
