package com.gtu.gtueduchain.ui.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtu.gtueduchain.viewmodel.StudentViewModel
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun VerificationScreen(
    viewModel: StudentViewModel
) {

    val degree = viewModel.degree
    val error = viewModel.error
    val isSearching = viewModel.isSearching

    var enrollment by remember { mutableStateOf("") }

    // =====================================================
    // 🔵 INPUT SCREEN
    // =====================================================
    if (degree == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6FA))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Filled.School,
                contentDescription = "Verification Logo",
                tint = Color(0xFF5B5FEF),
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verification Portal",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your details to fetch your digital degree from the GTU blockchain ledger.",
                fontSize = 14.sp,
                color = Color.Gray
            )


            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value = enrollment,
                onValueChange = { enrollment = it },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                label = { Text("Enter Enrollment Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (enrollment.isNotBlank()) {
                        viewModel.findDegree(enrollment)
                    }
                },
                enabled = enrollment.isNotBlank() && !isSearching,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (isSearching) "Verifying..." else "Verify Degree")
            }

            error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = Color.Red)
            }
        }
    }

    // =====================================================
    // 🟢 RESULT SCREEN (PREMIUM HR UI)
    // =====================================================
    else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6FA))
                .verticalScroll(rememberScrollState())
        ) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF5B5FEF),
                                Color(0xFF7A4DFF)
                            )
                        )
                    )
            ) {

                IconButton(
                    onClick = { viewModel.clearSearch() },
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GTU VERIFICATION PORTAL",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // AUTHENTIC BADGE
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFDFF5E3)
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Authentic GTU Record - Ledger Verified",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF0F9D58),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            // INFO CARD
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    InfoRow("Student Name", degree.name)
                    InfoRow("Enrollment No", degree.enrollment)
                    InfoRow("Branch", degree.branch)
                    InfoRow("CPI", degree.cpi)
                    InfoRow("Graduation Date", degree.date)

                    Spacer(Modifier.height(12.dp))
                    Divider()
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Blockchain Hash",
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = degree.hash,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
        Spacer(Modifier.height(10.dp))
    }
}
