package com.gtu.gtueduchain.data.blockchain

import com.gtu.gtueduchain.data.model.IssuedDegree
import java.security.MessageDigest

class BlockchainEngine {

    private val blockchain = mutableListOf<Block>()

    init {
        createGenesisBlock()
    }

    private fun createGenesisBlock() {

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
            hash = "GENESIS_HASH"
        )

        val genesisBlock = Block(
            index = 0,
            timestamp = System.currentTimeMillis(),
            data = genesisDegree,
            previousHash = "0",
            hash = generateHash("GENESIS")
        )

        blockchain.add(genesisBlock)
    }

    fun addBlock(degree: IssuedDegree): Block {

        val previousBlock = blockchain.last()

        val index = previousBlock.index + 1
        val timestamp = System.currentTimeMillis()
        val previousHash = previousBlock.hash

        val rawData =
            "$index$timestamp${degree.enrollment}${degree.name}${degree.program}${degree.cpi}${degree.date}$previousHash"
        val hash = generateHash(rawData)

        val updatedDegree = degree.copy(
            status = "ACTIVE ON CHAIN",
            blockNumber = index,
            hash = hash
        )

        val block = Block(
            index = index,
            timestamp = timestamp,
            data = updatedDegree,
            previousHash = previousHash,
            hash = hash
        )

        blockchain.add(block)

        return block
    }

    fun getAllBlocks(): List<Block> = blockchain

    fun getDegrees(): List<IssuedDegree> =
        blockchain.drop(1).map { it.data } // skip genesis

    //  NEW FUNCTION (Required by Repository)
    fun isChainValid(): Boolean {

        for (i in 1 until blockchain.size) {

            val current = blockchain[i]
            val previous = blockchain[i - 1]

            // Check hash link
            if (current.previousHash != previous.hash) {
                return false
            }

            // Recalculate current hash
            val recalculatedHash = generateHash(
                "${current.index}${current.timestamp}${current.data.enrollment}${current.previousHash}"
            )

            if (current.hash != recalculatedHash) {
                return false
            }
        }

        return true
    }

    private fun generateHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
