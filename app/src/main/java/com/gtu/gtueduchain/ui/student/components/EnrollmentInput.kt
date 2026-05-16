package com.gtu.gtueduchain.ui.student.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gtu.gtueduchain.ui.common.PrimaryButton

@Composable
fun EnrollmentInput(
    onSearch: (String) -> Unit,
    isLoading: Boolean
) {
    var enrollment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = enrollment,
            onValueChange = { enrollment = it },
            label = { Text("Enter Enrollment Number") },
            placeholder = { Text("e.g. 190010107001") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            leadingIcon = { Icon(Icons.Default.Search, null) }
        )

        PrimaryButton(
            onClick = { onSearch(enrollment) },
            enabled = !isLoading && enrollment.isNotBlank()
        ) {
            Text(if (isLoading) "Searching Ledger..." else "Access GTU Portal")
        }
    }
}
