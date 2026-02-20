package com.gtu.gtueduchain.ui.admin.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchDropdown(
    selectedBranch: String,
    branches: List<String>,
    onBranchSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selectedBranch,
            onValueChange = {},
            readOnly = true,
            label = { Text("Branch / Department") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            branches.forEach { branch ->
                DropdownMenuItem(
                    text = { Text(branch) },
                    onClick = {
                        onBranchSelected(branch)
                        expanded = false
                    }
                )
            }
        }
    }
}
