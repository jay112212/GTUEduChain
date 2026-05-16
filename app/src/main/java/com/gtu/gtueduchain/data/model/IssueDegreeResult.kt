package com.gtu.gtueduchain.data.model

sealed interface IssueDegreeResult {
    data object Success : IssueDegreeResult
    data object DuplicateEnrollment : IssueDegreeResult
    data class Failure(
        val message: String,
        val canRetry: Boolean = true
    ) : IssueDegreeResult
}
