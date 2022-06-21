package me.devwckd.mcd_service

import kotlinx.serialization.Serializable

typealias ListServersResponse = Paginated<ServerInfo>

@Serializable
data class CreateServerRequest(
    val ip: String,
    val port: Int,
    val type: String,
)

typealias CreateServerResponse = ServerInfo

typealias ReadServerResponse = ServerInfo

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
    val type: String
)