package com.gtu.gtueduchain.data.model

data class ChainState(
    val latestIndex: Int,
    val latestHash: String,
    val updatedAt: Long
)
