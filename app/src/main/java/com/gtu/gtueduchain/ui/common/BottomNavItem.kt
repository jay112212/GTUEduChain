package com.gtu.gtueduchain.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Outlined.Home, "Home")
    object Admin : BottomNavItem("admin", Icons.Outlined.Security, "Admin")
    object Student : BottomNavItem("student", Icons.Outlined.School, "Student")   // TODO: FIXED
    object Verify : BottomNavItem("verify", Icons.Outlined.CheckCircle, "Verify")
    object Ledger : BottomNavItem("ledger", Icons.Outlined.Storage, "Ledger")
}
