package com.gtu.gtueduchain.data.model

data class DashboardStats(
    val totalDegrees: Long = 0,
    val totalVerifications: Long = 0,
    val tamperedAttempts: Long = 0,
    val lastIssuedEnrollment: String = "",
    val lastIssuedDate: String = "",
    val blockchainStatus: String = "UNKNOWN"
)
