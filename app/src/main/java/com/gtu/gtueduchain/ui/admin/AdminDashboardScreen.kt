package com.gtu.gtueduchain.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtu.gtueduchain.ui.admin.components.DegreeForm
import com.gtu.gtueduchain.ui.common.AppScaffold
import com.gtu.gtueduchain.viewmodel.AdminAuthViewModel
import com.gtu.gtueduchain.viewmodel.AdminViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel,
    authViewModel: AdminAuthViewModel
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Console", fontWeight = FontWeight.Bold)
                        Text(
                            text = authViewModel.currentUserEmail ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            authViewModel.logout()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Issue New Degree",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Fill student details to authorize and commit a new academic record.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(32.dp))
            }

            item {
                DegreeForm(
                    onIssue = { name, enroll, branch, cpi ->
                        viewModel.issueDegree(
                            name,
                            enroll,
                            branch,
                            cpi,
                            "10-02-2026"
                        )
                    },
                    isLoading = viewModel.isIssuing
                )

                viewModel.error?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
