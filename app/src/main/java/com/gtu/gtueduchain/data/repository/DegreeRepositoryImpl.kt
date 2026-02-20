package com.gtu.gtueduchain.data.repository

import com.gtu.gtueduchain.data.blockchain.Block
import com.gtu.gtueduchain.data.blockchain.BlockchainEngine
import com.gtu.gtueduchain.data.model.IssuedDegree
import com.gtu.gtueduchain.data.remote.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DegreeRepositoryImpl(
    private val engine: BlockchainEngine,
    private val firestoreService: FirestoreService
) : DegreeRepository {

    override suspend fun issueDegree(
        name: String,
        enrollment: String,
        branch: String,
        cpi: String,
        date: String
    ): Boolean {

        return try {

            // Prevent duplicate locally
            if (engine.getDegrees().any { it.enrollment == enrollment }) {
                return false
            }

            val degree = IssuedDegree(
                name = name,
                enrollment = enrollment,
                branch = branch,
                cpi = cpi,
                date = date,
                status = "ACTIVE ON CHAIN",
                blockNumber = 0,
                hash = ""
            )

            engine.addBlock(degree)

            val latest = engine.getDegrees().last()

            // 🔥 Firestore write (can throw exception)
            firestoreService.saveDegree(
                name = latest.name,
                enrollment = latest.enrollment,
                branch = latest.branch,
                cpi = latest.cpi,
                date = latest.date,
                hash = latest.hash
            )

            true

        } catch (e: Exception) {

            e.printStackTrace()

            false
        }
    }

    override suspend fun getDegreeByEnrollment(enrollment: String): IssuedDegree? =
        withContext(Dispatchers.IO) {
            engine.getDegrees().find { it.enrollment == enrollment }
        }

    override suspend fun getAllDegrees(): List<IssuedDegree> =
        withContext(Dispatchers.IO) {
            engine.getDegrees()
        }

    override suspend fun getBlockCount(): Int =
        withContext(Dispatchers.IO) {
            engine.getAllBlocks().size
        }

    override suspend fun getAllBlocks(): List<Block> =
        withContext(Dispatchers.IO) {
            engine.getAllBlocks()
        }

    override suspend fun getLedger(): List<Block> =
        withContext(Dispatchers.IO) {
            engine.getAllBlocks()
        }

    override suspend fun isChainValid(): Boolean =
        withContext(Dispatchers.IO) {
            engine.isChainValid()
        }
}
