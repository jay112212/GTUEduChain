package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtu.gtueduchain.data.model.IssuedDegree
import com.gtu.gtueduchain.domain.usecase.VerifyDegreeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentViewModel(
    private val verifyDegreeUseCase: VerifyDegreeUseCase
) : ViewModel() {

    var degree by mutableStateOf<IssuedDegree?>(null)
        private set

    var isSearching by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun findDegree(enrollment: String) {
        viewModelScope.launch {
            isSearching = true
            error = null
            degree = null

            delay(800)

            val result = verifyDegreeUseCase(enrollment)

            if (result != null) {
                degree = result
            } else {
                error = "No degree found for enrollment: $enrollment"
            }

            isSearching = false
        }
    }

    fun clearSearch() {
        degree = null
        error = null
    }
}
