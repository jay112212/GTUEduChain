package com.gtu.gtueduchain.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun AppScaffold(
    navController: NavController,
    showBottomBar: Boolean = true,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    content(PaddingValues())
}

