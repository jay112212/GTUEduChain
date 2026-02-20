package com.gtu.gtueduchain.ui.student

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtu.gtueduchain.ui.student.components.DegreeCard
import com.gtu.gtueduchain.ui.student.components.EnrollmentInput
import com.gtu.gtueduchain.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(navController: NavController, viewModel: StudentViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Degree Verification", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    )
        { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Icon(
                    Icons.Default.School,
                    null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Student Portal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Enter your details to fetch your digital degree from the GTU blockchain ledger.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(32.dp))
            }

            item {
                EnrollmentInput(
                    onSearch = { viewModel.findDegree(it) },
                    isLoading = viewModel.isSearching
                )
                
                viewModel.error?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
            }

            item {
                AnimatedVisibility(
                    visible = viewModel.degree != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(Modifier.height(32.dp))
                        viewModel.degree?.let {
                            DegreeCard(degree = it)
                        }
                    }
                }
            }
        }
    }
}
