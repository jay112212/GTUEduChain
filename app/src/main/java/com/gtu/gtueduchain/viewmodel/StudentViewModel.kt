package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtu.gtueduchain.data.model.IssuedDegree
import com.gtu.gtueduchain.domain.usecase.VerifyDegreeUseCase
import kotlinx.coroutines.launch

class StudentViewModel(
    private val verifyDegreeUseCase: VerifyDegreeUseCase
) : ViewModel() {

    var degree by mutableStateOf<IssuedDegree?>(null)
        private set

    var isSearching by mutableStateOf(false)
        private set

    var loadingMessage by mutableStateOf("Ready to verify.")
        private set

    var blockchainTampered by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var canRetry by mutableStateOf(false)
        private set

    private var lastEnrollment by mutableStateOf<String?>(null)

    fun findDegree(enrollment: String) {
        val normalizedEnrollment = enrollment.trim().uppercase()
        if (normalizedEnrollment.isBlank()) return

        lastEnrollment = normalizedEnrollment

        viewModelScope.launch {
            isSearching = true
            blockchainTampered = false
            error = null
            canRetry = false
            degree = null
            loadingMessage = "Checking blockchain integrity and fetching credentials..."

            try {
                val result = verifyDegreeUseCase(normalizedEnrollment)

                if (result != null) {
                    degree = result
                    loadingMessage = "Verification complete."
                } else {
                    error = "No credential found for enrollment: $normalizedEnrollment"
                    canRetry = false
                    loadingMessage = "No record found."
                }
            } catch (error: Exception) {
                if (error.message == "BLOCKCHAIN_COMPROMISED") {
                    blockchainTampered = true
                    this@StudentViewModel.error =
                        "Ledger integrity verification failed. Data may have been tampered."
                    canRetry = false
                    loadingMessage = "Verification blocked."
                } else {
                    this@StudentViewModel.error =
                        error.message ?: "Verification failed. Check your connection and try again."
                    canRetry = true
                    loadingMessage = "Unable to verify right now."
                }
            } finally {
                isSearching = false
            }
        }
    }

    fun retryLastSearch() {
        lastEnrollment?.let(::findDegree)
    }

    fun clearSearch() {
        degree = null
        error = null
        blockchainTampered = false
        canRetry = false
        isSearching = false
        loadingMessage = "Ready to verify."
    }
}
