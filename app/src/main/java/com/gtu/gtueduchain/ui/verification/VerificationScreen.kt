package com.gtu.gtueduchain.ui.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtu.gtueduchain.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    viewModel: StudentViewModel
) {
    val degree = viewModel.degree
    val error = viewModel.error
    val isSearching = viewModel.isSearching

    var enrollment by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recruiter Verification") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.clearSearch() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (degree == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF2F4F8))
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))

                Card(
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF5B5FEF),
                            modifier = Modifier.height(60.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Recruiter Credential Check",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Verify candidate credentials directly from the GTU EduChain blockchain ledger.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(28.dp))

                        OutlinedTextField(
                            value = enrollment,
                            onValueChange = { enrollment = it },
                            leadingIcon = {
                                Icon(Icons.Default.Search, null)
                            },
                            placeholder = {
                                Text("Enter Enrollment Number")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (enrollment.isNotBlank()) {
                                    viewModel.findDegree(enrollment)
                                }
                            },
                            enabled = enrollment.isNotBlank() && !isSearching,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (isSearching) "Verifying..." else "Verify Credential")
                        }

                        if (isSearching) {
                            Spacer(Modifier.height(16.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = viewModel.loadingMessage,
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }

                        error?.let {
                            Spacer(Modifier.height(14.dp))
                            Text(it, color = Color.Red)
                        }

                        if (viewModel.canRetry) {
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = viewModel::retryLastSearch,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Retry Verification")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        } else {
            val verifiedTimestamp = remember {
                SimpleDateFormat(
                    "M/d/yyyy, h:mm:ss a",
                    Locale.getDefault()
                ).format(Date())
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF2F4F8))
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE6F4EA)
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Credential AUTHENTIC",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F9D58),
                            fontSize = 16.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Ledger Verified: $verifiedTimestamp",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        TwoColumnRow(
                            "Student Name", degree.name,
                            "Degree Program", degree.program
                        )

                        TwoColumnRow(
                            "Enrollment No", degree.enrollment,
                            "Branch / Stream", degree.branch
                        )

                        TwoColumnRow(
                            "College Name", degree.collegeName,
                            "Graduation Date", degree.date
                        )

                        TwoColumnRow(
                            "Date of Birth", degree.dob,
                            "CPI / CGPA", degree.cpi
                        )

                        Spacer(Modifier.height(16.dp))
                        Divider()
                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Blockchain Transaction Hash",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F3F4)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    degree.hash,
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(degree.hash))
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, null)
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            "Status: ${degree.status}",
                            color = Color(0xFF00C853),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun TwoColumnRow(
    label1: String,
    value1: String,
    label2: String,
    value2: String
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(label1, fontSize = 11.sp, color = Color.Gray)
            Text(value1, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(label2, fontSize = 11.sp, color = Color.Gray)
            Text(value2, fontWeight = FontWeight.SemiBold)
        }
    }

    Spacer(Modifier.height(14.dp))
}
