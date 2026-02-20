package com.gtu.gtueduchain.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gtu.gtueduchain.ui.common.AppScaffold
import com.gtu.gtueduchain.ui.home.components.HeroSection
import com.gtu.gtueduchain.ui.home.components.StatsSection
import com.gtu.gtueduchain.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {

    // 🔥 Automatically refresh stats when screen opens
    LaunchedEffect(Unit) {
        viewModel.updateStats()
    }

    // ✅ Directly observe state
    val totalDegrees = viewModel.totalDegrees

    AppScaffold(navController = navController) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            item {
                HeroSection()
            }

            item {
                Spacer(Modifier.height(8.dp))
                StatsSection(
                    totalDegrees = totalDegrees
                )
            }
        }
    }
}
