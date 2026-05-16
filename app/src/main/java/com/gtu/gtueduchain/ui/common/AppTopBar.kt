package com.gtu.gtueduchain.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    title: String
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("GTU Portal") },
                    onClick = {
                        expanded = false
                        navController.navigate("home")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Student") },
                    onClick = {
                        expanded = false
                        navController.navigate("student")
                    }
                )
                DropdownMenuItem(
                    text = { Text("HR Verification") },
                    onClick = {
                        expanded = false
                        navController.navigate("verify")
                    }
                )
                DropdownMenuItem(
                    text = { Text("GTU Ledger") },
                    onClick = {
                        expanded = false
                        navController.navigate("ledger")
                    }
                )
            }
        }
    )
}
