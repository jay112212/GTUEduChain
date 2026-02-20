package com.gtu.gtueduchain.ui.common

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Admin,
        BottomNavItem.Student,
        BottomNavItem.Verify,
        BottomNavItem.Ledger
    )

    NavigationBar {

        val navBackStackEntry =
            navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry.value?.destination?.route

        items.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo("home")
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, null) },
                label = { Text(item.label) }
            )
        }
    }
}
