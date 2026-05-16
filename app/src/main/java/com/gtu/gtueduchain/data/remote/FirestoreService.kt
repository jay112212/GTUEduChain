package com.gtu.gtueduchain.data.remote

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.gtu.gtueduchain.data.blockchain.Block
import com.gtu.gtueduchain.data.model.ChainState
import com.gtu.gtueduchain.data.model.IssuedDegree
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.io.IOException

class FirestoreService {

    private val db = FirebaseFirestore.getInstance()
    private val blocksCollection = db.collection(BLOCKS_COLLECTION)
    private val degreesCollection = db.collection(DEGREES_COLLECTION)
    private val chainStateDocument = db.collection(METADATA_COLLECTION).document(CHAIN_STATE_DOCUMENT)

    init {
        configureFirestoreIfNeeded(db)
    }

    suspend fun getAllBlocks(preferCache: Boolean = true): List<Block> {
        val snapshot = readQuery(
            blocksCollection.orderBy("index"),
            preferCache = preferCache
        )
        return snapshot.documents.mapNotNull(::mapDocumentToBlock)
    }

    suspend fun getBlocksPage(
        pageSize: Int,
        startAfterIndex: Int? = null,
        preferCache: Boolean = true
    ): List<Block> {
        var query = blocksCollection
            .orderBy("index", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        if (startAfterIndex != null) {
            query = query.startAfter(startAfterIndex)
        }

        val snapshot = readQuery(query, preferCache = preferCache)
        return snapshot.documents.mapNotNull(::mapDocumentToBlock)
    }

    fun listenToLatestBlocks(
        pageSize: Int,
        onUpdate: (List<Block>, Boolean) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        return blocksCollection
            .orderBy("index", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                onUpdate(
                    snapshot.documents.mapNotNull(::mapDocumentToBlock),
                    snapshot.metadata.isFromCache
                )
            }
    }

    suspend fun getLatestBlock(preferCache: Boolean = true): Block? {
        return getBlocksPage(pageSize = 1, preferCache = preferCache).firstOrNull()
    }

    suspend fun getChainState(preferCache: Boolean = true): ChainState? {
        val snapshot = readDocument(chainStateDocument, preferCache = preferCache)
        return snapshot.toChainState()
    }

    suspend fun saveGenesisBlock(block: Block) {
        val batch = db.batch()
        batch.set(blocksCollection.document(block.index.toString()), block.toBlockMap())
        batch.set(chainStateDocument, chainStateMapFor(block))
        batch.commit().await()
    }

    suspend fun backfillIndexesAndChainState(blocks: List<Block>) {
        if (blocks.isEmpty()) return

        val latestBlock = blocks.maxByOrNull { it.index } ?: return

        blocks.filter { it.index != 0 }
            .chunked(BATCH_WRITE_SIZE)
            .forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { block ->
                    batch.set(
                        degreesCollection.document(block.data.enrollment),
                        block.data.toDegreeMap(block.timestamp)
                    )
                }
                batch.commit().await()
            }

        chainStateDocument.set(chainStateMapFor(latestBlock)).await()
    }

    suspend fun getDegreeByEnrollment(
        enrollment: String,
        preferCache: Boolean = true
    ): IssuedDegree? {
        val snapshot = readDocument(
            degreesCollection.document(enrollment),
            preferCache = preferCache
        )

        if (snapshot.exists()) {
            return mapDocumentToDegree(snapshot.data)
        }

        return null
    }

    suspend fun getDegreeByEnrollmentFromBlocks(
        enrollment: String,
        preferCache: Boolean = true
    ): IssuedDegree? {
        val snapshot = readQuery(
            blocksCollection
                .whereEqualTo("data.enrollment", enrollment)
                .limit(1),
            preferCache = preferCache
        )

        return snapshot.documents.firstOrNull()?.let(::mapDocumentToBlock)?.data
    }

    suspend fun saveDegreeIndex(degree: IssuedDegree, timestamp: Long = System.currentTimeMillis()) {
        degreesCollection.document(degree.enrollment)
            .set(degree.toDegreeMap(timestamp))
            .await()
    }

    suspend fun getAllDegrees(preferCache: Boolean = true): List<IssuedDegree> {
        val snapshot = readQuery(
            degreesCollection.orderBy("blockNumber"),
            preferCache = preferCache
        )

        return snapshot.documents.mapNotNull { document ->
            mapDocumentToDegree(document.data)
        }
    }

    suspend fun issueDegreeAtomically(
        enrollment: String,
        blockFactory: (ChainState) -> Block
    ): Block? {
        return db.runTransaction { transaction ->
            val degreeDocument = degreesCollection.document(enrollment)
            val degreeSnapshot = transaction.get(degreeDocument)

            if (degreeSnapshot.exists()) {
                return@runTransaction null
            }

            val chainStateSnapshot = transaction.get(chainStateDocument)
            val chainState = chainStateSnapshot.toChainState()
                ?: throw IllegalStateException("Chain state is not initialized.")

            val block = blockFactory(chainState)

            transaction.set(
                blocksCollection.document(block.index.toString()),
                block.toBlockMap()
            )
            transaction.set(
                degreeDocument,
                block.data.toDegreeMap(block.timestamp)
            )
            transaction.set(
                chainStateDocument,
                chainStateMapFor(block)
            )

            block
        }.await()
    }

    private suspend fun readQuery(
        query: Query,
        preferCache: Boolean
    ): QuerySnapshot {
        return retryFirestoreOperation {
            if (!preferCache) {
                return@retryFirestoreOperation query.get(Source.DEFAULT).await()
            }

            try {
                val cached = query.get(Source.CACHE).await()
                if (!cached.isEmpty) {
                    cached
                } else {
                    query.get(Source.DEFAULT).await()
                }
            } catch (_: Exception) {
                query.get(Source.DEFAULT).await()
            }
        }
    }

    private suspend fun readDocument(
        document: DocumentReference,
        preferCache: Boolean
    ): DocumentSnapshot {
        return retryFirestoreOperation {
            if (!preferCache) {
                return@retryFirestoreOperation document.get(Source.DEFAULT).await()
            }

            try {
                val cached = document.get(Source.CACHE).await()
                if (cached.exists()) {
                    cached
                } else {
                    document.get(Source.DEFAULT).await()
                }
            } catch (_: Exception) {
                document.get(Source.DEFAULT).await()
            }
        }
    }

    private suspend fun <T> retryFirestoreOperation(
        maxAttempts: Int = 3,
        initialDelayMillis: Long = 250L,
        action: suspend () -> T
    ): T {
        var currentDelayMillis = initialDelayMillis
        var lastError: Throwable? = null

        repeat(maxAttempts) { attempt ->
            try {
                return action()
            } catch (error: Throwable) {
                lastError = error

                val shouldRetry = error is IOException ||
                    (error is FirebaseFirestoreException && error.code in RETRYABLE_CODES)

                if (!shouldRetry || attempt == maxAttempts - 1) {
                    throw error
                }

                delay(currentDelayMillis)
                currentDelayMillis *= 2
            }
        }

        throw lastError ?: IllegalStateException("Firestore operation failed.")
    }

    private fun chainStateMapFor(block: Block): Map<String, Any> {
        return mapOf(
            "latestIndex" to block.index,
            "latestHash" to block.hash,
            "updatedAt" to System.currentTimeMillis()
        )
    }

    private fun Block.toBlockMap(): Map<String, Any> {
        return mapOf(
            "index" to index,
            "previousHash" to previousHash,
            "hash" to hash,
            "timestamp" to timestamp,
            "data" to data.toDegreeMap(timestamp)
        )
    }

    private fun IssuedDegree.toDegreeMap(timestamp: Long): Map<String, Any> {
        return mapOf(
            "name" to name,
            "enrollment" to enrollment,
            "branch" to branch,
            "program" to program,
            "specialization" to specialization,
            "collegeName" to collegeName,
            "dob" to dob,
            "cpi" to cpi,
            "date" to date,
            "status" to status,
            "blockNumber" to blockNumber,
            "hash" to hash,
            "timestamp" to timestamp
        )
    }

    private fun mapDocumentToBlock(document: DocumentSnapshot): Block? {
        return mapDocumentToBlock(document.data)
    }

    private fun mapDocumentToBlock(data: Map<String, Any>?): Block? {
        if (data == null) return null

        val index = (data["index"] as? Number)?.toInt() ?: return null
        val previousHash = data["previousHash"] as? String ?: ""
        val hash = data["hash"] as? String ?: ""
        val timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L
        val degree = mapDocumentToDegree(data["data"] as? Map<String, Any>) ?: return null

        return Block(
            index = index,
            timestamp = timestamp,
            data = degree,
            previousHash = previousHash,
            hash = hash
        )
    }

    private fun mapDocumentToDegree(data: Map<String, Any>?): IssuedDegree? {
        if (data == null) return null

        return IssuedDegree(
            name = data["name"] as? String ?: "",
            enrollment = data["enrollment"] as? String ?: "",
            branch = data["branch"] as? String ?: "",
            program = data["program"] as? String ?: "",
            specialization = data["specialization"] as? String ?: "",
            collegeName = data["collegeName"] as? String ?: "",
            dob = data["dob"] as? String ?: "",
            cpi = data["cpi"] as? String ?: "",
            date = data["date"] as? String ?: "",
            status = data["status"] as? String ?: "",
            blockNumber = (data["blockNumber"] as? Number)?.toInt() ?: 0,
            hash = data["hash"] as? String ?: ""
        )
    }

    private fun DocumentSnapshot.toChainState(): ChainState? {
        val data = data ?: return null
        return ChainState(
            latestIndex = (data["latestIndex"] as? Number)?.toInt() ?: return null,
            latestHash = data["latestHash"] as? String ?: return null,
            updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L
        )
    }

    private companion object {
        private const val BLOCKS_COLLECTION = "blocks"
        private const val DEGREES_COLLECTION = "degrees"
        private const val METADATA_COLLECTION = "metadata"
        private const val CHAIN_STATE_DOCUMENT = "chainState"
        private const val BATCH_WRITE_SIZE = 400
        @Volatile
        private var isConfigured = false

        private val RETRYABLE_CODES = setOf(
            FirebaseFirestoreException.Code.ABORTED,
            FirebaseFirestoreException.Code.CANCELLED,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED,
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED,
            FirebaseFirestoreException.Code.UNAVAILABLE
        )

        private fun configureFirestoreIfNeeded(db: FirebaseFirestore) {
            if (isConfigured) return

            synchronized(this) {
                if (isConfigured) return

                runCatching {
                    db.firestoreSettings = FirebaseFirestoreSettings.Builder(db.firestoreSettings)
                        .setPersistenceEnabled(true)
                        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()
                }

                isConfigured = true
            }
        }
    }
}
