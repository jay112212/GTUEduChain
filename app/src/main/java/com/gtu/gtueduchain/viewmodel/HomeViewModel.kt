package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gtu.gtueduchain.domain.usecase.GetLedgerUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getLedgerUseCase: GetLedgerUseCase
) : ViewModel() {

    var totalDegrees by mutableStateOf(0)
        private set

    var totalBlocks by mutableStateOf(0)
        private set

    init {
        updateStats()   // 🔥 Auto load when ViewModel created
    }

    fun updateStats() {
        viewModelScope.launch {

            val ledger = getLedgerUseCase()   // ✅ now allowed

            totalDegrees = if (ledger.isNotEmpty()) ledger.size - 1 else 0
            totalBlocks = ledger.size
        }
    }
}
