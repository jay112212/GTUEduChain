package com.gtu.gtueduchain.domain.usecase

import com.gtu.gtueduchain.data.model.IssueDegreeResult
import com.gtu.gtueduchain.data.repository.DegreeRepository

class IssueDegreeUseCase(
    private val repository: DegreeRepository
) {

    suspend operator fun invoke(
        name: String,
        enrollment: String,
        branch: String,
        program: String,
        specialization: String,
        collegeName: String,
        dob: String,
        cpi: String,
        date: String
    ): IssueDegreeResult {
        return repository.issueDegree(
            name = name,
            enrollment = enrollment,
            branch = branch,
            program = program,
            specialization = specialization,
            collegeName = collegeName,
            dob = dob,
            cpi = cpi,
            date = date
        )
    }
}
