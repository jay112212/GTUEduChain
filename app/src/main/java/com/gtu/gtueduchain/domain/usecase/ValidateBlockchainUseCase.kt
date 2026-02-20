package com.gtu.gtueduchain.domain.usecase

import com.gtu.gtueduchain.data.repository.DegreeRepository

class ValidateBlockchainUseCase(
    private val repository: DegreeRepository
) {

    suspend operator fun invoke(): Boolean {
        return repository.isChainValid()
    }
}
