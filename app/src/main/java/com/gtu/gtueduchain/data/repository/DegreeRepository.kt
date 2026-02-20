package com.gtu.gtueduchain.data.repository

import com.gtu.gtueduchain.data.blockchain.Block
import com.gtu.gtueduchain.data.model.IssuedDegree

interface DegreeRepository {

    /**
     * Issues a new degree and adds it to blockchain + Firestore.
     * Returns true if success, false if enrollment already exists.
     */
    suspend fun issueDegree(
        name: String,
        enrollment: String,
        branch: String,
        cpi: String,
        date: String
    ): Boolean

    /**
     * Returns degree by enrollment number
     */
    suspend fun getDegreeByEnrollment(enrollment: String): IssuedDegree?

    /**
     * Returns all issued degrees
     */
    suspend fun getAllDegrees(): List<IssuedDegree>

    /**
     * Returns total number of blockchain blocks
     */
    suspend fun getBlockCount(): Int

    /**
     * Returns all blocks including genesis
     */
    suspend fun getAllBlocks(): List<Block>

    /**
     * Returns blockchain ledger
     */
    suspend fun getLedger(): List<Block>

    /**
     * Validates blockchain integrity
     */
    suspend fun isChainValid(): Boolean
}
