package com.gtu.gtueduchain.ui.ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtu.gtueduchain.ui.common.AppScaffold
import com.gtu.gtueduchain.ui.ledger.components.BlockCard
import com.gtu.gtueduchain.viewmodel.LedgerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(navController: NavController, viewModel: LedgerViewModel) {
    AppScaffold(
        navController = navController,
        topBar = {
            TopAppBar(
                title = { Text("Blockchain Ledger", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadLedger() }) {
                        Icon(Icons.Default.Refresh, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Validity Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (viewModel.isValid) Color(0xFF10B981) else Color(0xFFEF4444))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Security,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (viewModel.isValid) "LEDGER VERIFIED & SECURE" else "LEDGER COMPROMISED",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.blocks) { block ->
                    BlockCard(block = block)
                }
            }
        }
    }
}
