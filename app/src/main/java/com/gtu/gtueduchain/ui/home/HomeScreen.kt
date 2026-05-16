package com.gtu.gtueduchain.ui.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtu.gtueduchain.data.blockchain.Block
import com.gtu.gtueduchain.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val degreeCount = viewModel.totalDegrees
    val blockCount = viewModel.totalBlocks
    val latestBlock = viewModel.latestBlock

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFE8E7FF),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Outlined.Security,
                    contentDescription = null,
                    tint = Color(0xFF5B5FEF),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "GTU EduChain",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Decentralized Degree Verification",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(10.dp))

        BlockchainStatusIndicator(isStable = true)

        Spacer(Modifier.height(26.dp))

        when {
            viewModel.isLoading -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text(
                    "Loading dashboard snapshot...",
                    color = Color.Gray
                )
            }

            viewModel.error != null -> {
                Text(
                    text = viewModel.error.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = viewModel::refreshStats) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Retry")
                }
            }

            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardCard(
                        title = "Degrees Issued",
                        value = degreeCount.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    DashboardCard(
                        title = "Chain Blocks",
                        value = blockCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(20.dp))

                LatestActivityCard(latestBlock)
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF0FF)),
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5B5FEF)
            )

            Text(
                title,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LatestActivityCard(
    block: Block?
) {
    if (block == null) return

    val hashPreview =
        if (block.hash.length > 14) {
            block.hash.take(10) + "..." + block.hash.takeLast(4)
        } else {
            block.hash
        }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF0FF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                "Latest Blockchain Activity",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Block #${block.index}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF5B5FEF)
            )

            Spacer(Modifier.height(6.dp))

            Text("Student: ${block.data.name}", fontSize = 14.sp)
            Text("Enrollment: ${block.data.enrollment}", fontSize = 14.sp)

            Spacer(Modifier.height(14.dp))

            Text(
                "Transaction Hash",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                hashPreview,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151)
            )
        }
    }
}

@Composable
fun BlockchainStatusIndicator(
    isStable: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blockchain-status")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blockchain-status-alpha"
    )

    val color = if (isStable) Color(0xFF22C55E) else Color(0xFFEF4444)
    val text = if (isStable) "Chain Stable" else "Blockchain Compromised"

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color.copy(alpha = alpha), CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text,
            color = color,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}
