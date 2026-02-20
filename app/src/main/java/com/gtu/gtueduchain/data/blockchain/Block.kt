package com.gtu.gtueduchain.data.blockchain

import com.gtu.gtueduchain.data.model.IssuedDegree

data class Block(
    val index: Int,
    val timestamp: Long,
    val data: IssuedDegree,
    val previousHash: String,
    val hash: String
)
