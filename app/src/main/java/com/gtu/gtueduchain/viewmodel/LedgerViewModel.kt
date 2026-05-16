package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.gtu.gtueduchain.data.blockchain.Block
import com.gtu.gtueduchain.data.remote.FirestoreService
import com.gtu.gtueduchain.data.repository.DegreeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LedgerViewModel(
    private val firestoreService: FirestoreService,
    private val repository: DegreeRepository
) : ViewModel() {

    var blocks by mutableStateOf<List<Block>>(emptyList())
        private set

    var isValid by mutableStateOf(true)
        private set

    var isMining by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    var isFromCache by mutableStateOf(false)
        private set

    var hasMore by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private var lastVisibleIndex: Int? = null
    private var highestSeenIndex = -1
    private var listener: ListenerRegistration? = null
    private var validationJob: Job? = null

    init {
        refreshLedger()
        startRealtimeLedger()
    }

    fun refreshLedger() {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val firstPage = firestoreService.getBlocksPage(PAGE_SIZE)
                blocks = firstPage
                lastVisibleIndex = firstPage.lastOrNull()?.index
                hasMore = firstPage.size == PAGE_SIZE
                highestSeenIndex = firstPage.maxOfOrNull { it.index } ?: -1
                validateLedger()
            } catch (error: Exception) {
                this@LedgerViewModel.error =
                    error.message ?: "Unable to load blockchain records."
            } finally {
                isLoading = false
            }
        }
    }

    fun loadNextPage() {
        val cursor = lastVisibleIndex ?: return
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true
            error = null

            try {
                val nextPage = firestoreService.getBlocksPage(
                    pageSize = PAGE_SIZE,
                    startAfterIndex = cursor
                )

                blocks = (blocks + nextPage)
                    .distinctBy { it.index }
                    .sortedByDescending { it.index }

                lastVisibleIndex = nextPage.lastOrNull()?.index ?: lastVisibleIndex
                hasMore = nextPage.size == PAGE_SIZE
            } catch (error: Exception) {
                this@LedgerViewModel.error =
                    error.message ?: "Unable to load older blocks."
            } finally {
                isLoadingMore = false
            }
        }
    }

    private fun startRealtimeLedger() {
        listener = firestoreService.listenToLatestBlocks(
            pageSize = PAGE_SIZE,
            onUpdate = { latestBlocks, fromCache ->
                val latestIndex = latestBlocks.maxOfOrNull { it.index } ?: -1

                if (latestIndex > highestSeenIndex && highestSeenIndex != -1) {
                    viewModelScope.launch {
                        isMining = true
                        delay(700)
                        isMining = false
                    }
                }

                highestSeenIndex = maxOf(highestSeenIndex, latestIndex)
                isFromCache = fromCache

                blocks = (blocks + latestBlocks)
                    .distinctBy { it.index }
                    .sortedByDescending { it.index }

                if (lastVisibleIndex == null) {
                    lastVisibleIndex = blocks.lastOrNull()?.index
                }

                validateLedger()
            },
            onError = { throwable ->
                error = throwable.message ?: "Realtime sync is temporarily unavailable."
            }
        )
    }

    private fun validateLedger() {
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            runCatching { repository.isChainValid() }
                .onSuccess { isValid = it }
                .onFailure {
                    isValid = false
                    error = it.message ?: "Unable to validate ledger integrity."
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }

    private companion object {
        private const val PAGE_SIZE = 20
    }
}
