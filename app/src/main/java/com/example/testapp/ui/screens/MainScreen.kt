package com.example.testapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.testapp.ui.navigation.AppNavGraph
import com.example.testapp.ui.navigation.NavigationItem
import com.example.testapp.ui.navigation.Screen
import com.example.testapp.ui.navigation.rememberNavigationState
import com.example.testapp.utils.toImageBitmap

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
) {
    val navigationState = rememberNavigationState()
    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in listOf(
        Screen.Login.route,
        Screen.Signature.route
    )
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                ) {

                    val items = listOf(
                        NavigationItem.Home,
                        NavigationItem.History,
                        NavigationItem.Settings,
                    )

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
                                selectedIconColor = MaterialTheme.colorScheme.background,
                            )
                        )
                    }
                }
            }

        }
    ) { paddingValues ->
        AppNavGraph(
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

                    }
                )
            },
            blenderScreenContent = {
                BlenderScreen(
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onSignatureCardClickListener =  {
                        navigationState.navHostController.navigate(Screen.Signature.route)
                    }
                )
            },
            acidScreenContent = {
                AcidScreen(
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    },
                    onSignatureCardClickListener = {
                        navigationState.navHostController.navigate(Screen.Signature.route)
                    }
                )
            },
            gelScreenContent = {

            },
            historyScreenContent = {
                ReportsScreen(
                    onFilterClickListener = {

                    }
                )
            },
            settingsScreenContent = {

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
            signatureScreenContent = {
                SignatureScreen(
                    onBackClickListener = {
                        navigationState.navHostController.popBackStack()
                    }
                )
            }
        )
    }
}
