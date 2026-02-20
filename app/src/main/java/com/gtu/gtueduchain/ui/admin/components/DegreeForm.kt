package com.gtu.gtueduchain.ui.admin.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gtu.gtueduchain.ui.common.PrimaryButton
import com.gtu.gtueduchain.ui.admin.components.BranchDropdown


@Composable
fun DegreeForm(
    onIssue: (name: String, enrollment: String, branch: String, cpi: String) -> Unit,
    isLoading: Boolean
) {
    var name by remember { mutableStateOf("") }
    var enrollment by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("Computer Engineering") }
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Student Full Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = enrollment,
            onValueChange = { enrollment = it },
            label = { Text("Enrollment Number") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        BranchDropdown(
            selectedBranch = branch,
            branches = branches,
            onBranchSelected = { branch = it }
        )

        OutlinedTextField(
            value = cpi,
            onValueChange = { cpi = it },
            label = { Text("Performance Index (CPI)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        PrimaryButton(
            onClick = { onIssue(name, enrollment, branch, cpi) },
            enabled = !isLoading && name.isNotBlank() && enrollment.isNotBlank()
        ) {
            Text(if (isLoading) "Committing to Ledger..." else "Sign & Issue Degree")
        }
    }
}
