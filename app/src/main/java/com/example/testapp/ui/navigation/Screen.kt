package com.example.testapp.ui.navigation

sealed class Screen(
    val route: String
) {
    object Home : Screen(route = ROUTE_HOME)

    object TypesReports : Screen(route = ROUTE_TYPES_REPORTS)

    object Blender : Screen(route = ROUTE_BLENDER)

    object Acid : Screen(route = ROUTE_ACID)

    object Gel : Screen(route = ROUTE_GEL)

    object History : Screen(route = ROUTE_HISTORY)

    object Settings : Screen(route = ROUTE_SETTINGS)

    object Details : Screen(route = ROUTE_DETAILS)

    object Login : Screen(route = ROUTE_LOGIN)

    object Signature : Screen(route = ROUTE_SIGNATURE)

    private companion object {
        const val ROUTE_HOME = "home"
        const val ROUTE_TYPES_REPORTS = "types_reports"
        const val ROUTE_SETTINGS = "settings"
        const val ROUTE_BLENDER = "blender"
        const val ROUTE_ACID = "acid"
        const val ROUTE_GEL = "gel"
        const val ROUTE_HISTORY = "history"
        const val ROUTE_DETAILS = "details"
        const val ROUTE_LOGIN = "login"
        const val ROUTE_SIGNATURE = "signature"
    }
}