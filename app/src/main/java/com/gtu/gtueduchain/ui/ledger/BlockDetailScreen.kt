package com.gtu.gtueduchain.ui.ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtu.gtueduchain.data.blockchain.Block
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockDetailScreen(
    navController: NavController,
    block: Block?
) {

    if (block == null) return

    val clipboard = LocalClipboardManager.current
    val timeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
    val formattedTime = SimpleDateFormat(
        "dd MMM yyyy, hh:mm:ss a",
        Locale.getDefault()
    ).format(Date(block.timestamp))

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = { Text("Block Analysis", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            DetailCard("HASH FINGERPRINT", block.hash, clipboard)

            Spacer(Modifier.height(16.dp))

            DetailCard("PARENT HASH", block.previousHash, clipboard)

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SmallInfoCard("BLOCK", block.index.toString())
                SmallInfoCard("TIME", formattedTime)
            }

            Spacer(Modifier.height(24.dp))

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            Spacer(Modifier.height(20.dp))

            Text(
                "VALIDATED TRANSACTION DATA",
                color = Color(0xFF818CF8),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(16.dp))

            InfoRow("Credential", "${block.data.program} in ${block.data.specialization}")
            InfoRow("Student", block.data.name)
            InfoRow("Enrollment", block.data.enrollment)
            InfoRow("CPI", block.data.cpi)

            Spacer(Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF312E81)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.Green)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "CONSENSUS ACHIEVED",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "This block has been mathematically validated and permanently added to the GTU blockchain ledger.",
                        color = Color(0xFFCBD5E1)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun DetailCard(title: String, value: String, clipboard: androidx.compose.ui.platform.ClipboardManager) {

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(Modifier.padding(20.dp)) {

            Text(title, color = Color(0xFF94A3B8), fontSize = 12.sp)

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    value,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {
                    clipboard.setText(AnnotatedString(value))
                }) {
                    Icon(Icons.Default.ContentCopy, null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun SmallInfoCard(title: String, value: String) {
    Card(
        modifier = Modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(label, color = Color(0xFF94A3B8), fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = Color.White)
        Spacer(Modifier.height(12.dp))
    }
}