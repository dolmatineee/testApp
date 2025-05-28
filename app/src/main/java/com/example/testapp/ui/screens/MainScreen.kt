package com.example.testapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.ui.navigation.AppNavGraph
import com.example.testapp.ui.navigation.NavigationItem
import com.example.testapp.ui.navigation.Screen
import com.example.testapp.ui.navigation.rememberNavigationState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
) {

    val sharedPreferences = LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userRole = sharedPreferences.getString("position", "employee")
    if (userRole != null) {
        Log.e("userRole", userRole)
    }

    val navigationState = rememberNavigationState()
    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in listOf(
        Screen.Login.route,
        Screen.SupervisorSignature.route,
        Screen.SupervisorReportsFilter.route,
        Screen.Fields.route,
        Screen.Wells.route,
        Screen.Layers.route,
        Screen.Customers.route,
    )

    val reportFilters = remember { mutableStateOf(ReportFilters()) }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                ) {

                    val items = when(userRole) {
                        "Ведущий супервайзер по ГРП ООО БНД" -> {
                            listOf(
                                NavigationItem.SupervisorCurrentReports,
                                NavigationItem.SupervisorAllReports,
                                NavigationItem.SupervisorSettings
                            )
                        }

                        "Инженер ГРП ООО ЛРС" -> {
                            listOf(
                                NavigationItem.EngineerCurrentReports,
                                NavigationItem.SupervisorAllReports,
                                NavigationItem.SupervisorSettings
                            )
                        }

                        "Мастер ГРП ООО ЛРС" -> {
                            listOf(
                                NavigationItem.Home,
                                NavigationItem.History,
                                NavigationItem.Settings
                            )
                        }

                        "Администратор" -> {
                            listOf(
                                NavigationItem.AdministratorStats,
                                NavigationItem.AdministratorEmployees,
                                NavigationItem.Settings
                            )
                        }

                        else -> {
                            listOf(
                                NavigationItem.Home,
                                NavigationItem.History,
                                NavigationItem.Settings
                            )
                        }
                    }



                    items.forEach { item ->

                        val selected = navBackStackEntry?.destination?.hierarchy?.any {
                            it.route == item.screen.route
                        } ?: false

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navigationState.navigateTo(item.screen.route)
                                }

                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                }
            }

        }
    ) { paddingValues ->
        AppNavGraph(
            userRole = userRole,
            navHostController = navigationState.navHostController,
            typesReportsScreenContent = {
                TypesReportsScreen(
                    onBlenderReportClickListener = {
                        navigationState.navHostController.navigate(Screen.Blender.route)
                    },
                    onAcidReportClickListener = {
                        navigationState.navHostController.navigate(Screen.Acid.route)
                    },
                    onGelReportClickListener = {
                        navigationState.navHostController.navigate(Screen.Gel.route)
                    }
                )
            },
            blenderScreenContent = {
                BlenderScreen(
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onSignatureCardClickListener = {
                        navigationState.navHostController.navigate(Screen.SupervisorSignature.route)
                    }
                )
            },
            acidScreenContent = {
                AcidScreen(
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onSignatureCardClickListener = {
                        navigationState.navHostController.navigate(Screen.SupervisorSignature.route)
                    }
                )
            },
            gelScreenContent = {
                GelScreen(
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    }
                )
            },
            historyScreenContent = {
                ReportsScreen(
                    onFilterClickListener = {
                        navigationState.navHostController.navigate(Screen.SupervisorReportsFilter.route)
                    },
                    reportFilters = reportFilters.value
                )
            },
            settingsScreenContent = {
                ProfileScreen(
                    onLogoutClickListener = {

                    },
                    onSignatureCardClickListener = {
                        navigationState.navHostController.navigate(Screen.SupervisorSignature.route)
                    }
                )
            },
            detailsScreenContent = {

            },
            loginScreenContent = {
                LoginScreen(
                    onLoginSuccess = {
                        navigationState.navHostController.navigate(Screen.Home.route)
                    }
                )
            },
            supervisorCurrentReportsContent = {
                SupervisorReportsScreen()
            },
            supervisorAllReportsContent = {
                ReportsScreen(
                    onFilterClickListener = {
                        navigationState.navHostController.navigate(Screen.SupervisorReportsFilter.route)
                    },
                    reportFilters = reportFilters.value
                )
            },
            supervisorReportsFilterContent = {
                FilterScreen(
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onDeleteFilters = {
                        reportFilters.value = ReportFilters()
                    },
                    onSaveFilters = { newFilters ->
                        reportFilters.value = newFilters
                        navigationState.navHostController.popBackStack()
                    },
                    onFieldsClick = {
                        navigationState.navHostController.navigate(Screen.Fields.route)
                    },
                    onWellsClick = {
                        navigationState.navHostController.navigate(Screen.Wells.route)
                    },
                    onLayersClick = {
                        navigationState.navHostController.navigate(Screen.Layers.route)
                    },
                    onCustomersClick = {
                        navigationState.navHostController.navigate(Screen.Customers.route)
                    },
                    reportFilters = reportFilters.value,
                )
            },
            supervisorSettingsContent = {
                ProfileScreen(
                    onLogoutClickListener = {
                        navigationState.navHostController.navigate(Screen.Login.route)
                    },
                    onSignatureCardClickListener = {
                        navigationState.navHostController.navigate(Screen.SupervisorSignature.route)
                    }
                )
            },
            supervisorSignatureContent = {
                SignatureScreen(
                    onBackClickListener = {
                        navigationState.navHostController.popBackStack()
                    },

                    )
            },
            fieldsScreenContent = {
                FieldsScreen(
                    reportFilters = reportFilters.value,
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onSaveFields = { selectedFields ->
                        reportFilters.value = reportFilters.value.copy(fields = selectedFields)
                        navigationState.navHostController.popBackStack()
                    }
                )
            },
            wellsScreenContent = {
                WellsScreen(
                    reportFilters = reportFilters.value,
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onSaveWells = { selectedWells ->
                        reportFilters.value = reportFilters.value.copy(wells = selectedWells)
                        navigationState.navHostController.popBackStack()
                    }
                )
            },
            layersScreenContent = {
                LayersScreen(
                    reportFilters = reportFilters.value,
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onSaveLayers = { selectedLayers ->
                        reportFilters.value = reportFilters.value.copy(layers = selectedLayers)
                        navigationState.navHostController.popBackStack()
                    }
                )
            },
            customersScreenContent = {
                CustomersScreen(
                    reportFilters = reportFilters.value,
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onSaveCustomers = { selectedCustomers ->
                        reportFilters.value =
                            reportFilters.value.copy(customers = selectedCustomers)
                        navigationState.navHostController.popBackStack()
                    }
                )
            },
            engineerCurrentReportsContent = {
                EngineerReportsScreen()
            },
            administratorStatsContent = {
                AdministratorStatisticsScreen()
            },
            administratorEmployeesContent = {
                EmployeesScreen()
            }
        )
    }
}



