package com.gtu.gtueduchain.data.model

data class IssuedDegree(
    val name: String,
    val enrollment: String,
    val branch: String,
    val program: String,        // BE / BSc / BCom
    val specialization: String, // IT / CE / AI-ML etc
    val collegeName: String,
    val dob: String,
    val cpi: String,
    val date: String,
    val status: String,
    val blockNumber: Int,
    val hash: String
)