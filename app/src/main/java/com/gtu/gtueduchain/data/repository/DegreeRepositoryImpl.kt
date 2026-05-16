package com.gtu.gtueduchain.data.repository

import com.gtu.gtueduchain.data.blockchain.Block
import com.gtu.gtueduchain.data.model.ChainState
import com.gtu.gtueduchain.data.model.IssueDegreeResult
import com.gtu.gtueduchain.data.model.IssuedDegree
import com.gtu.gtueduchain.data.remote.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class DegreeRepositoryImpl(
    private val firestoreService: FirestoreService
) : DegreeRepository {

    private val bootstrapMutex = Mutex()
    private val validationMutex = Mutex()

    private val degreeCache = linkedMapOf<String, IssuedDegree>()

    @Volatile
    private var bootstrapComplete = false

    @Volatile
    private var latestBlockCache: Block? = null

    @Volatile
    private var blockCountCache: Int? = null

    @Volatile
    private var chainValidationCache: ChainValidationCache? = null

    override suspend fun issueDegree(
        name: String,
        enrollment: String,
        branch: String,
        program: String,
        specialization: String,
        collegeName: String,
        dob: String,
        cpi: String,
        date: String
    ): IssueDegreeResult = withContext(Dispatchers.IO) {
        try {
            ensureLedgerBootstrap()

            val normalizedEnrollment = enrollment.normalizedEnrollment()
            val existingDegree = getDegreeByEnrollment(normalizedEnrollment)

            if (existingDegree != null) {
                return@withContext IssueDegreeResult.DuplicateEnrollment
            }

            val block = firestoreService.issueDegreeAtomically(normalizedEnrollment) { chainState ->
                buildBlock(
                    chainState = chainState,
                    degree = IssuedDegree(
                        name = name.trim(),
                        enrollment = normalizedEnrollment,
                        branch = branch.trim(),
                        program = program.trim(),
                        specialization = specialization.trim(),
                        collegeName = collegeName.trim(),
                        dob = dob.trim(),
                        cpi = cpi.trim(),
                        date = date.trim(),
                        status = "ACTIVE ON CHAIN",
                        blockNumber = 0,
                        hash = ""
                    )
                )
            } ?: return@withContext IssueDegreeResult.DuplicateEnrollment

            degreeCache[normalizedEnrollment] = block.data
            latestBlockCache = block
            blockCountCache = block.index + 1
            chainValidationCache = ChainValidationCache(
                isValid = true,
                validatedAtMillis = System.currentTimeMillis(),
                latestIndex = block.index,
                latestHash = block.hash
            )

            IssueDegreeResult.Success
        } catch (error: Exception) {
            IssueDegreeResult.Failure(
                message = error.message ?: "Unable to issue degree right now."
            )
        }
    }

    override suspend fun getLedger(): List<Block> = withContext(Dispatchers.IO) {
        ensureLedgerBootstrap()
        firestoreService.getAllBlocks()
    }

    override suspend fun getAllBlocks(): List<Block> = withContext(Dispatchers.IO) {
        ensureLedgerBootstrap()
        firestoreService.getAllBlocks()
    }

    override suspend fun getBlockCount(): Int = withContext(Dispatchers.IO) {
        blockCountCache?.let { return@withContext it }

        ensureLedgerBootstrap()

        val chainState = firestoreService.getChainState()
        val count = chainState?.latestIndex?.plus(1)
            ?: firestoreService.getLatestBlock()?.index?.plus(1)
            ?: 0

        blockCountCache = count
        count
    }

    override suspend fun getLatestBlock(): Block? = withContext(Dispatchers.IO) {
        latestBlockCache?.let { return@withContext it.takeIf { block -> block.index != 0 } }

        ensureLedgerBootstrap()

        val latest = firestoreService.getLatestBlock()
        latestBlockCache = latest
        latest?.takeIf { it.index != 0 }
    }

    override suspend fun getDegreeByEnrollment(enrollment: String): IssuedDegree? =
        withContext(Dispatchers.IO) {
            ensureLedgerBootstrap()

            val normalizedEnrollment = enrollment.normalizedEnrollment()
            degreeCache[normalizedEnrollment]?.let { return@withContext it }

            val indexedDegree = firestoreService.getDegreeByEnrollment(normalizedEnrollment)
            if (indexedDegree != null) {
                degreeCache[normalizedEnrollment] = indexedDegree
                return@withContext indexedDegree
            }

            val legacyDegree = firestoreService.getDegreeByEnrollmentFromBlocks(normalizedEnrollment)
            if (legacyDegree != null) {
                degreeCache[normalizedEnrollment] = legacyDegree
                firestoreService.saveDegreeIndex(legacyDegree)
            }

            legacyDegree
        }

    override suspend fun getAllDegrees(): List<IssuedDegree> = withContext(Dispatchers.IO) {
        ensureLedgerBootstrap()

        val degrees = firestoreService.getAllDegrees()
        if (degrees.isNotEmpty()) {
            degrees.forEach { degreeCache[it.enrollment] = it }
            return@withContext degrees
        }

        firestoreService.getAllBlocks()
            .filter { it.index != 0 }
            .map { it.data }
    }

    override suspend fun isChainValid(): Boolean = withContext(Dispatchers.IO) {
        ensureLedgerBootstrap()

        validationMutex.withLock {
            val currentChainState = firestoreService.getChainState()
            val cachedValidation = chainValidationCache

            if (
                cachedValidation != null &&
                currentChainState != null &&
                cachedValidation.isFreshFor(currentChainState)
            ) {
                return@withLock cachedValidation.isValid
            }

            val blocks = firestoreService.getAllBlocks(preferCache = false)
                .sortedBy { it.index }

            val isValid = validateChain(blocks)
            val latestBlock = blocks.lastOrNull()

            chainValidationCache = ChainValidationCache(
                isValid = isValid,
                validatedAtMillis = System.currentTimeMillis(),
                latestIndex = latestBlock?.index ?: 0,
                latestHash = latestBlock?.hash.orEmpty()
            )

            latestBlockCache = latestBlock
            blockCountCache = latestBlock?.index?.plus(1) ?: 0

            isValid
        }
    }

    private suspend fun ensureLedgerBootstrap() {
        if (bootstrapComplete) return

        bootstrapMutex.withLock {
            if (bootstrapComplete) return

            val chainState = firestoreService.getChainState(preferCache = false)
            if (chainState != null) {
                latestBlockCache = firestoreService.getLatestBlock(preferCache = false)
                blockCountCache = chainState.latestIndex + 1
                bootstrapComplete = true
                return
            }

            val existingBlocks = firestoreService.getAllBlocks(preferCache = false)
                .sortedBy { it.index }

            if (existingBlocks.isEmpty()) {
                val genesisBlock = createGenesisBlock()
                firestoreService.saveGenesisBlock(genesisBlock)
                latestBlockCache = genesisBlock
                blockCountCache = 1
            } else {
                firestoreService.backfillIndexesAndChainState(existingBlocks)
                latestBlockCache = existingBlocks.lastOrNull()
                blockCountCache = existingBlocks.size
            }

            bootstrapComplete = true
        }
    }

    private fun createGenesisBlock(): Block {
        val timestamp = System.currentTimeMillis()
        val genesisDegree = IssuedDegree(
            name = "Genesis",
            enrollment = "0000",
            branch = "System",
            program = "System",
            specialization = "Genesis",
            collegeName = "GTU System",
            dob = "01-01-2000",
            cpi = "0",
            date = "01-01-2026",
            status = "GENESIS",
            blockNumber = 0,
            hash = ""
        )

        val hash = generateHash(
            rawHashInput(
                index = 0,
                timestamp = timestamp,
                degree = genesisDegree,
                previousHash = "0"
            )
        )

        val finalizedDegree = genesisDegree.copy(hash = hash)
        return Block(
            index = 0,
            timestamp = timestamp,
            data = finalizedDegree,
            previousHash = "0",
            hash = hash
        )
    }

    private fun buildBlock(
        chainState: ChainState,
        degree: IssuedDegree
    ): Block {
        val nextIndex = chainState.latestIndex + 1
        val timestamp = System.currentTimeMillis()
        val hash = generateHash(
            rawHashInput(
                index = nextIndex,
                timestamp = timestamp,
                degree = degree,
                previousHash = chainState.latestHash
            )
        )

        val finalizedDegree = degree.copy(
            blockNumber = nextIndex,
            hash = hash
        )

        return Block(
            index = nextIndex,
            timestamp = timestamp,
            data = finalizedDegree,
            previousHash = chainState.latestHash,
            hash = hash
        )
    }

    private fun validateChain(blocks: List<Block>): Boolean {
        if (blocks.isEmpty()) return true

        for (index in 1 until blocks.size) {
            val current = blocks[index]
            val previous = blocks[index - 1]

            if (current.previousHash != previous.hash) {
                return false
            }

            val recalculatedHash = generateHash(
                rawHashInput(
                    index = current.index,
                    timestamp = current.timestamp,
                    degree = current.data,
                    previousHash = current.previousHash
                )
            )

            if (current.hash != recalculatedHash) {
                return false
            }
        }

        return true
    }

    private fun rawHashInput(
        index: Int,
        timestamp: Long,
        degree: IssuedDegree,
        previousHash: String
    ): String {
        return buildString {
            append(index)
            append(timestamp)
            append(degree.name)
            append(degree.enrollment)
            append(degree.branch)
            append(degree.program)
            append(degree.specialization)
            append(degree.collegeName)
            append(degree.dob)
            append(degree.cpi)
            append(degree.date)
            append(previousHash)
        }
    }

    private fun generateHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun String.normalizedEnrollment(): String {
        return trim().uppercase()
    }

    private data class ChainValidationCache(
        val isValid: Boolean,
        val validatedAtMillis: Long,
        val latestIndex: Int,
        val latestHash: String
    ) {
        fun isFreshFor(chainState: ChainState): Boolean {
            return isValid &&
                latestIndex == chainState.latestIndex &&
                latestHash == chainState.latestHash &&
                System.currentTimeMillis() - validatedAtMillis < VALIDATION_CACHE_WINDOW_MILLIS
        }
    }

    private companion object {
        private const val VALIDATION_CACHE_WINDOW_MILLIS = 30_000L
    }
}
