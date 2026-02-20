package com.gtu.gtueduchain.ui.student.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gtu.gtueduchain.data.model.IssuedDegree

@Composable
fun DegreeCard(degree: IssuedDegree) {

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {

            Text(
                text = degree.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text("Enrollment: ${degree.enrollment}")
            Text("Branch: ${degree.branch}")
            Text("CPI: ${degree.cpi}")
            Text("Date: ${degree.date}")

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Blockchain Hash:",
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = degree.hash,
                fontSize = 12.sp
            )
        }
    }
}
