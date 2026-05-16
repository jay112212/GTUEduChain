package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtu.gtueduchain.data.model.IssueDegreeResult
import com.gtu.gtueduchain.domain.usecase.IssueDegreeUseCase
import kotlinx.coroutines.launch

class AdminViewModel(
    private val issueDegreeUseCase: IssueDegreeUseCase
) : ViewModel() {

    var isIssuing by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    private var lastRequest by mutableStateOf<IssueRequest?>(null)

    fun issueDegree(
        name: String,
        enrollment: String,
        specialization: String,
        cpi: String,
        date: String,
        program: String,
        collegeName: String,
        dob: String
    ) {
        val request = IssueRequest(
            name = name,
            enrollment = enrollment,
            specialization = specialization,
            cpi = cpi,
            date = date,
            program = program,
            collegeName = collegeName,
            dob = dob
        )

        lastRequest = request

        viewModelScope.launch {
            isIssuing = true
            error = null
            successMessage = null

            when (
                val result = issueDegreeUseCase(
                    name = request.name,
                    enrollment = request.enrollment,
                    branch = request.specialization,
                    program = request.program,
                    specialization = request.specialization,
                    collegeName = request.collegeName,
                    dob = request.dob,
                    cpi = request.cpi,
                    date = request.date
                )
            ) {
                IssueDegreeResult.Success -> {
                    successMessage = "Degree committed to Firestore and blockchain successfully."
                }

                IssueDegreeResult.DuplicateEnrollment -> {
                    error = "A credential already exists for this enrollment number."
                }

                is IssueDegreeResult.Failure -> {
                    error = result.message
                }
            }

            isIssuing = false
        }
    }

    fun retryLastIssue() {
        lastRequest?.let { request ->
            issueDegree(
                name = request.name,
                enrollment = request.enrollment,
                specialization = request.specialization,
                cpi = request.cpi,
                date = request.date,
                program = request.program,
                collegeName = request.collegeName,
                dob = request.dob
            )
        }
    }

    fun consumeSuccessMessage() {
        successMessage = null
    }

    private data class IssueRequest(
        val name: String,
        val enrollment: String,
        val specialization: String,
        val cpi: String,
        val date: String,
        val program: String,
        val collegeName: String,
        val dob: String
    )
}
