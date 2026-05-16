package com.gtu.gtueduchain.ui.student

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtu.gtueduchain.data.model.IssuedDegree
import com.gtu.gtueduchain.ui.student.components.EnrollmentInput
import com.gtu.gtueduchain.viewmodel.StudentViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegreeCard(degree: IssuedDegree) {
    val clipboard = LocalClipboardManager.current

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2547)
        ),
        elevation = CardDefaults.cardElevation(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "OFFICIAL GTU RECORD",
                fontSize = 11.sp,
                color = Color(0xFF9FA8FF),
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Gujarat Technological University (GTU)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "THIS CERTIFIES THAT",
                fontSize = 10.sp,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                degree.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "Enrollment No: ${degree.enrollment}",
                color = Color(0xFF8EA2FF),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Has successfully completed the requirements for the degree of",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "${degree.program} in ${degree.specialization}",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(20.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(Modifier.height(16.dp))

            Text("GTU DIGITAL SEAL", fontSize = 10.sp, color = Color.Gray)
            Spacer(Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2C3366)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        degree.hash.take(32) + "...",
                        color = Color(0xFF9FA8FF),
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(degree.hash))
                        }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("STATUS", fontSize = 10.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))

            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF00E676)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    degree.status,
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(
    navController: NavController,
    viewModel: StudentViewModel
) {
    val degree = viewModel.degree
    val error = viewModel.error
    val isLoading = viewModel.isSearching
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Credentials") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(24.dp))

                if (degree == null) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFE8ECFF)
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF5B5FEF),
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .height(28.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                "Student Credentials",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                "Enter your official GTU Enrollment Number to access your verified degree.",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )

                            Spacer(Modifier.height(24.dp))

                            EnrollmentInput(
                                onSearch = { viewModel.findDegree(it) },
                                isLoading = isLoading
                            )

                            if (isLoading) {
                                Spacer(Modifier.height(16.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = viewModel.loadingMessage,
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }

                            if (viewModel.canRetry) {
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = viewModel::retryLastSearch,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Retry")
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                }

                Spacer(Modifier.height(16.dp))
            }

            item {
                error?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                AnimatedVisibility(
                    visible = degree != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    degree?.let {
                        Spacer(Modifier.height(24.dp))
                        DegreeCard(it)
                    }
                }
            }

            item {
                if (degree != null) {
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { generatePdf(context, degree) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Download PDF")
                    }

                    Spacer(Modifier.height(120.dp))
                }
            }
        }
    }
}

fun generatePdf(context: Context, degree: IssuedDegree) {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(1080, 1920, 1).create()
    val page = document.startPage(pageInfo)

    val canvas = page.canvas
    val paint = Paint()

    paint.textSize = 40f
    paint.isFakeBoldText = true
    canvas.drawText("GTU Degree Certificate", 200f, 200f, paint)

    paint.textSize = 28f
    paint.isFakeBoldText = false
    canvas.drawText("Name: ${degree.name}", 200f, 300f, paint)
    canvas.drawText("Enrollment: ${degree.enrollment}", 200f, 350f, paint)
    canvas.drawText("Program: ${degree.program}", 200f, 400f, paint)
    canvas.drawText("Date: ${degree.date}", 200f, 450f, paint)
    canvas.drawText("CPI: ${degree.cpi}", 200f, 500f, paint)

    document.finishPage(page)

    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
        "GTU_${degree.enrollment}.pdf"
    )

    document.writeTo(FileOutputStream(file))
    document.close()

    Toast.makeText(context, "PDF Saved Successfully", Toast.LENGTH_LONG).show()
}
