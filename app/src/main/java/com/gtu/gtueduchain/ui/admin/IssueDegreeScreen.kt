package com.gtu.gtueduchain.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtu.gtueduchain.ui.admin.components.BranchDropdown
import com.gtu.gtueduchain.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDegreeScreen(
    navController: NavController,
    viewModel: AdminViewModel
) {

    var name by remember { mutableStateOf("") }
    var enrollment by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("Computer Engineering") }
    var date by remember { mutableStateOf("10-02-2026") }
    var cpi by remember { mutableStateOf("") }

    val branches = listOf(
        "Computer Engineering",
        "Information Technology",
        "Mechanical Engineering",
        "Civil Engineering",
        "Electrical Engineering",
        "Electronics & Communication",
        "AI & ML",
        "Data Science"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {

        Card(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Column(Modifier.padding(24.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Commence Issuance", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Create a permanent ledger record for a GTU graduate.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }

                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                viewModel.error?.let {
                    ErrorCard(it)
                    Spacer(Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = enrollment,
                    onValueChange = { enrollment = it },
                    label = { Text("Enrollment Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                BranchDropdown(branch, branches) { branch = it }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Completion Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = cpi,
                    onValueChange = { cpi = it },
                    label = { Text("Performance Index (CPI)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || enrollment.isBlank() || cpi.isBlank()) return@Button

                        viewModel.issueDegree(
                            name,
                            enrollment,
                            branch,
                            cpi,
                            date
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isIssuing
                ) {
                    Icon(Icons.Outlined.Security, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (viewModel.isIssuing)
                            "Committing to Blockchain..."
                        else
                            "Sign & Commit to Ledger"
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E6)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Error, null, tint = Color(0xFFDC2626))
            Spacer(Modifier.width(8.dp))
            Text(message, color = Color(0xFFDC2626), fontSize = 13.sp)
        }
    }
}
