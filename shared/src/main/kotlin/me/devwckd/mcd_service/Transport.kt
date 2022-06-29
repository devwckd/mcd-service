package me.devwckd.mcd_service

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateTransportRequest(
    val player: String,
    val destination: String,
)

typealias CreateTransportResponse = TransportInfo

@Serializable
class TransportInfo(
    val id: String,
    val playerId: @Serializable(with = UUIDSerializer::class) UUID,
    val serverId: String,
    val createdAt: Long
)