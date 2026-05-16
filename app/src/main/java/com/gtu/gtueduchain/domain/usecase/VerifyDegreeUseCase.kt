package com.gtu.gtueduchain.domain.usecase

import com.gtu.gtueduchain.data.model.IssuedDegree
import com.gtu.gtueduchain.data.repository.DegreeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class VerifyDegreeUseCase(
    private val repository: DegreeRepository
) {

    suspend operator fun invoke(enrollment: String): IssuedDegree? = coroutineScope {
        val degreeDeferred = async {
            repository.getDegreeByEnrollment(enrollment)
        }

        val validationDeferred = async {
            repository.isChainValid()
        }

        val degree = degreeDeferred.await()
        val isValid = validationDeferred.await()

        if (!isValid) {
            throw IllegalStateException("BLOCKCHAIN_COMPROMISED")
        }

        degree
    }
}
