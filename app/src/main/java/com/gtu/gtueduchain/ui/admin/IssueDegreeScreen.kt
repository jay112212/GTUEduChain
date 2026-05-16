package com.gtu.gtueduchain.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtu.gtueduchain.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDegreeScreen(
    navController: NavController,
    viewModel: AdminViewModel
) {
    var name by remember { mutableStateOf("") }
    var enrollment by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var collegeName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("B.E") }
    var selectedCourse by remember { mutableStateOf("Computer Engineering") }
    var completionDate by remember { mutableStateOf("") }
    var cpi by remember { mutableStateOf("") }

    var showDobPicker by remember { mutableStateOf(false) }
    var showCompletionPicker by remember { mutableStateOf(false) }

    val today = System.currentTimeMillis()
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val noFutureSelectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= today
        }
    }

    LaunchedEffect(viewModel.successMessage) {
        if (viewModel.successMessage != null) {
            name = ""
            enrollment = ""
            dob = ""
            collegeName = ""
            completionDate = ""
            cpi = ""
        }
    }

    if (showDobPicker) {
        val state = rememberDatePickerState(selectableDates = noFutureSelectableDates)

        DatePickerDialog(
            onDismissRequest = { showDobPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        dob = formatter.format(Date(it))
                    }
                    showDobPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDobPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showCompletionPicker) {
        val state = rememberDatePickerState(selectableDates = noFutureSelectableDates)

        DatePickerDialog(
            onDismissRequest = { showCompletionPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        completionDate = formatter.format(Date(it))
                    }
                    showCompletionPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showCompletionPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    val collegeCourses = mapOf(
        "B.E" to listOf(
            "Computer Engineering",
            "Information Technology",
            "Mechanical Engineering",
            "Civil Engineering",
            "Electrical Engineering",
            "Electronics & Communication",
            "AI & ML",
            "Cyber Security",
            "Data Science",
            "Robotics Engineering",
            "Automobile Engineering",
            "Chemical Engineering",
            "Biomedical Engineering"
        ),
        "Diploma" to listOf(
            "Computer Engineering",
            "Mechanical Engineering",
            "Civil Engineering",
            "Electrical Engineering",
            "Electronics",
            "Automobile Engineering",
            "Pharmacy (D.Pharm)",
            "Business Management",
            "Hotel Management"
        ),
        "Commerce" to listOf(
            "B.Com General",
            "B.Com Accounting",
            "B.Com Finance",
            "B.Com Banking & Insurance",
            "B.Com Marketing",
            "BBA",
            "BMS",
            "BA Economics",
            "CA",
            "CS",
            "CMA"
        ),
        "Pharmacy" to listOf("D.Pharm", "B.Pharm", "Pharm.D", "M.Pharm", "PhD in Pharmacy"),
        "Science" to listOf(
            "B.Sc Physics",
            "B.Sc Chemistry",
            "B.Sc Mathematics",
            "B.Sc Computer Science",
            "B.Sc Biotechnology",
            "B.Sc Microbiology",
            "B.Sc Environmental Science",
            "B.Sc Data Science",
            "B.Sc IT",
            "B.Sc Agriculture",
            "B.Sc Nursing"
        ),
        "Arts" to listOf(
            "BA English",
            "BA Hindi",
            "BA Gujarati",
            "BA Psychology",
            "BA Sociology",
            "BA Political Science",
            "BA History",
            "BA Geography",
            "BA Philosophy",
            "BA Journalism",
            "BA Mass Communication",
            "Bachelor of Fine Arts (BFA)",
            "Bachelor of Social Work (BSW)",
            "Bachelor of Performing Arts"
        ),
        "Medical" to listOf(
            "MBBS",
            "BDS",
            "BAMS",
            "BHMS",
            "BUMS",
            "BMLT (Medical Lab Technology)",
            "BOT (Occupational Therapy)"
        ),
        "Postgraduate" to listOf(
            "M.E",
            "M.Tech",
            "M.Com",
            "MBA",
            "MCA",
            "M.Sc",
            "MA",
            "M.Pharm",
            "MD",
            "MS",
            "MDS",
            "PhD"
        )
    )

    val categories = collegeCourses.keys.toList()
    val currentCourses = collegeCourses[selectedCategory] ?: emptyList()

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
            Column(
                Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Commence Issuance", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Create a permanent blockchain record.", fontSize = 13.sp, color = Color.Gray)
                    }

                    Icon(Icons.Outlined.Close, null, Modifier.clickable {
                        navController.popBackStack()
                    })
                }

                Spacer(Modifier.height(16.dp))

                viewModel.error?.let {
                    ErrorCard(it)
                    Spacer(Modifier.height(12.dp))
                }

                viewModel.successMessage?.let {
                    SuccessCard(it)
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

                OutlinedTextField(
                    value = dob,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date of Birth") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDobPicker = true }
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = collegeName,
                    onValueChange = { collegeName = it },
                    label = { Text("College Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                DropdownField("Category", categories, selectedCategory) {
                    selectedCategory = it
                    selectedCourse = collegeCourses[it]?.first().orEmpty()
                }

                Spacer(Modifier.height(10.dp))

                DropdownField("Course / Specialization", currentCourses, selectedCourse) {
                    selectedCourse = it
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = completionDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Completion Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCompletionPicker = true }
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = cpi,
                    onValueChange = { cpi = it },
                    label = { Text("Performance Index (CPI)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                if (viewModel.isIssuing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        if (
                            name.isBlank() ||
                            enrollment.isBlank() ||
                            dob.isBlank() ||
                            collegeName.isBlank() ||
                            cpi.isBlank() ||
                            completionDate.isBlank()
                        ) {
                            return@Button
                        }

                        viewModel.issueDegree(
                            name,
                            enrollment,
                            selectedCourse,
                            cpi,
                            completionDate,
                            selectedCategory,
                            collegeName,
                            dob
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isIssuing
                ) {
                    Icon(Icons.Outlined.Security, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (viewModel.isIssuing) "Committing..." else "Sign & Commit to Ledger")
                }

                if (viewModel.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = viewModel::retryLastIssue,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !viewModel.isIssuing
                    ) {
                        Icon(Icons.Outlined.Refresh, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Retry Last Submission")
                    }
                }

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ErrorCard(message: String) {
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

@Composable
fun SuccessCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CheckCircle, null, tint = Color(0xFF0F9D58))
            Spacer(Modifier.width(8.dp))
            Text(message, color = Color(0xFF0F9D58), fontSize = 13.sp)
        }
    }
}
