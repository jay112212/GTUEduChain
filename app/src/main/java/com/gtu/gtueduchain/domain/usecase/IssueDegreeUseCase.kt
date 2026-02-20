package com.gtu.gtueduchain.domain.usecase

import com.gtu.gtueduchain.data.repository.DegreeRepository

class IssueDegreeUseCase(
    private val repository: DegreeRepository
) {

    suspend operator fun invoke(
        name: String,
        enrollment: String,
        branch: String,
        cpi: String,
        date: String
    ): Boolean {
        return repository.issueDegree(
            name,
            enrollment,
            branch,
            cpi,
            date
        )
    }

}

