package com.gtu.gtueduchain.domain.usecase

import com.gtu.gtueduchain.data.model.IssuedDegree
import com.gtu.gtueduchain.data.repository.DegreeRepository

class VerifyDegreeUseCase(
    private val repository: DegreeRepository
) {
    suspend operator fun invoke(enrollment: String): IssuedDegree? {
        return repository.getDegreeByEnrollment(enrollment)
    }

}
