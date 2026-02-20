package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gtu.gtueduchain.data.blockchain.Block   // ✅ FIXED IMPORT
import com.gtu.gtueduchain.domain.usecase.GetLedgerUseCase
import com.gtu.gtueduchain.domain.usecase.ValidateBlockchainUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LedgerViewModel(
    private val getLedgerUseCase: GetLedgerUseCase,
    private val validateBlockchainUseCase: ValidateBlockchainUseCase
) : ViewModel() {

    var blocks by mutableStateOf<List<Block>>(emptyList())
        private set

    var isValid by mutableStateOf(true)
        private set

    init {
        loadLedger()
    }

    fun loadLedger() {
        viewModelScope.launch {

            val ledger = getLedgerUseCase()     // ✅ suspend allowed
            blocks = ledger.reversed()

            isValid = validateBlockchainUseCase()   // ✅ suspend allowed
        }
    }
}
