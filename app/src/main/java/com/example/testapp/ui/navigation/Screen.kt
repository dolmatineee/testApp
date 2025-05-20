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


    object Fields : Screen(route = ROUTE_FIELDS)
    object Wells : Screen(route = ROUTE_WELLS)
    object Layers : Screen(route = ROUTE_LAYERS)
    object Customers : Screen(route = ROUTE_CUSTOMERS)






    object SupervisorCurrentReports : Screen(route = ROUTE_SUPERVISOR_CURRENT_REPORTS)
    object SupervisorAllReports : Screen(route = ROUTE_SUPERVISOR_ALL_REPORTS)
    object SupervisorReportsFilter : Screen(route = ROUTE_SUPERVISOR_REPORTS_FILTER)
    object SupervisorSettings : Screen(route = ROUTE_SUPERVISOR_SUPERVISOR_SETTINGS)
    object SupervisorSignature : Screen(route = ROUTE_SUPERVISOR_SIGNATURE)


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



        const val ROUTE_FIELDS = "fields"
        const val ROUTE_WELLS = "wells"
        const val ROUTE_LAYERS = "layers"
        const val ROUTE_CUSTOMERS = "customers"




        const val ROUTE_SUPERVISOR_CURRENT_REPORTS = "supervisor_current_reports"
        const val ROUTE_SUPERVISOR_ALL_REPORTS = "supervisor_all_reports"
        const val ROUTE_SUPERVISOR_REPORTS_FILTER = "supervisor_reports_filter"
        const val ROUTE_SUPERVISOR_SUPERVISOR_SETTINGS = "supervisor_settings"
        const val ROUTE_SUPERVISOR_SIGNATURE = "supervisor_signature"

    }
}