package me.devwckd.mcd_service

import kotlinx.serialization.Serializable

typealias ListServersResponse = Paginated<ServerInfo>

@Serializable
data class CreateServerRequest(
    val ip: String,
    val port: Int,
    val type: String,
    val maxPlayers: Int
)

typealias CreateServerResponse = ServerInfo

typealias ReadServerResponse = ServerInfo

@Serializable
data class UpdateServerRequest(
    val currentPlayers: Int? = null,
    val maxPlayers: Int? = null
)

typealias UpdateServerResponse = ServerInfo

@Serializable
data class ServerHeartbeatRequest(
    val tps: Double,
    val sentAt: Long
)

@Serializable
data class ServerInfo(
    val id: String,
    val ip: String,
    val port: Int,
    val type: String,
    val currentPlayers: Int,
    val maxPlayers: Int
)