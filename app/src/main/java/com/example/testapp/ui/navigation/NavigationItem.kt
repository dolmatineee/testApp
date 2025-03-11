package com.example.testapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val screen: Screen,
    val icon: ImageVector
) {

    object Home: NavigationItem(
        screen = Screen.Home,
        icon = Icons.Filled.Home
    )

    object History: NavigationItem(
        screen = Screen.History,
        icon = Icons.Filled.Menu
    )

    object Settings: NavigationItem(
        screen = Screen.Settings,
        icon = Icons.Filled.Settings
    )
}