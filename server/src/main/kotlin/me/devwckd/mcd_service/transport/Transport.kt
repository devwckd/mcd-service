package me.devwckd.mcd_service.transport

import java.util.UUID

data class Transport(
    val id: String,
    val player: UUID,
    val server: String,
    val createdAt: Long = System.currentTimeMillis()
)