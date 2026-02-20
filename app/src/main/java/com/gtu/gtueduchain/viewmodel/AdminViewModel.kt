package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtu.gtueduchain.domain.usecase.IssueDegreeUseCase
import kotlinx.coroutines.launch

class AdminViewModel(
    private val issueDegreeUseCase: IssueDegreeUseCase
) : ViewModel() {

    var isIssuing by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun issueDegree(
        name: String,
        enrollment: String,
        branch: String,
        cpi: String,
        date: String
    ) {
        viewModelScope.launch {
            isIssuing = true
            error = null

            val success = issueDegreeUseCase(
                name,
                enrollment,
                branch,
                cpi,
                date
            )

            if (!success) {
                error = "Operation failed"
            }

            isIssuing = false
        }
    }
}
