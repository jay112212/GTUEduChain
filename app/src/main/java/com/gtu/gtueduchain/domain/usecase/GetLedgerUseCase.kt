package com.gtu.gtueduchain.domain.usecase

import com.gtu.gtueduchain.data.blockchain.Block
import com.gtu.gtueduchain.data.model.IssuedDegree
import com.gtu.gtueduchain.data.repository.DegreeRepository

class GetLedgerUseCase(private val repository: DegreeRepository) {
    suspend operator fun invoke(): List<Block> {
        return repository.getLedger()
    }

}
