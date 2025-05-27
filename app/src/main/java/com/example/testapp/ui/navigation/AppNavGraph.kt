package com.example.testapp.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun AppNavGraph(
    userRole: String?,
    navHostController: NavHostController,
    typesReportsScreenContent: @Composable () -> Unit,
    blenderScreenContent: @Composable () -> Unit,
    acidScreenContent: @Composable () -> Unit,
    gelScreenContent: @Composable () -> Unit,
    historyScreenContent: @Composable () -> Unit,
    settingsScreenContent: @Composable () -> Unit,
    detailsScreenContent: @Composable () -> Unit,
    loginScreenContent: @Composable () -> Unit,

    fieldsScreenContent: @Composable () -> Unit,
    wellsScreenContent: @Composable () -> Unit,
    layersScreenContent: @Composable () -> Unit,
    customersScreenContent: @Composable () -> Unit,




    supervisorCurrentReportsContent: @Composable () -> Unit,
    supervisorAllReportsContent: @Composable () -> Unit,
    supervisorReportsFilterContent: @Composable () -> Unit,
    supervisorSettingsContent: @Composable () -> Unit,
    supervisorSignatureContent: @Composable () -> Unit,



    engineerCurrentReportsContent: @Composable () -> Unit,
) {
    val sharedPreferences = LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)


    NavHost(
        navController = navHostController,
        startDestination = if (isLoggedIn) {
            when (userRole) {
                "Ведущий супервайзер по ГРП ООО БНД" -> Screen.SupervisorCurrentReports.route
                "Мастер ГРП ООО ЛРС" -> Screen.Home.route
                "Инженер ГРП ООО ЛРС" -> Screen.EngineerCurrentReports.route
                else -> {
                    Screen.Login.route
                }
            }
        } else {
            Screen.Login.route
        }
    ) {
        homeScreenNavGraph(
            typesReportsScreenContent = typesReportsScreenContent,
            blenderScreenContent = blenderScreenContent,
            acidScreenContent = acidScreenContent,
            gelScreenContent = gelScreenContent
        )

        composable(Screen.History.route) {
            historyScreenContent()
        }

        composable(Screen.Settings.route) {
            settingsScreenContent()
        }

        composable(Screen.Details.route) {
            detailsScreenContent()
        }

        composable(Screen.Login.route) {
            loginScreenContent()
        }

        composable(Screen.Fields.route) {
            fieldsScreenContent()
        }

        composable(Screen.Wells.route) {
            wellsScreenContent()
        }

        composable(Screen.Layers.route) {
            layersScreenContent()
        }

        composable(Screen.Customers.route) {
            customersScreenContent()
        }









        composable(Screen.SupervisorCurrentReports.route) {
            supervisorCurrentReportsContent()
        }

        composable(Screen.SupervisorAllReports.route) {
            supervisorAllReportsContent()
        }

        composable(Screen.SupervisorReportsFilter.route) {
            supervisorReportsFilterContent()
        }

        composable(Screen.SupervisorSettings.route) {
            supervisorSettingsContent()
        }

        composable(Screen.SupervisorSignature.route) {
            supervisorSignatureContent()
        }


        composable(Screen.EngineerCurrentReports.route) {
            engineerCurrentReportsContent()
        }
    }
}