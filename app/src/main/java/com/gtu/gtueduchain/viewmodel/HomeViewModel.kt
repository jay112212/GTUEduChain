package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtu.gtueduchain.data.blockchain.Block
import com.gtu.gtueduchain.data.repository.DegreeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: DegreeRepository
) : ViewModel() {

    var totalDegrees by mutableStateOf(0)
        private set

    var totalBlocks by mutableStateOf(0)
        private set

    var latestBlock by mutableStateOf<Block?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val blockCountDeferred = async { repository.getBlockCount() }
                val latestBlockDeferred = async { repository.getLatestBlock() }

                totalBlocks = blockCountDeferred.await()
                totalDegrees = (totalBlocks - 1).coerceAtLeast(0)
                latestBlock = latestBlockDeferred.await()
            } catch (error: Exception) {
                this@HomeViewModel.error =
                    error.message ?: "Unable to load dashboard stats right now."
            } finally {
                isLoading = false
            }
        }
    }

    fun refreshStats() {
        loadStats()
    }
}
