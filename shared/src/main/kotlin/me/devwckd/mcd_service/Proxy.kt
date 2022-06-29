package me.devwckd.mcd_service

import kotlinx.serialization.Serializable
import java.time.Instant

typealias ListProxiesResponse = Paginated<ProxyInfo>

@Serializable
data class CreateProxyRequest(
    val ip: String,
    val port: Int,
    val maxPlayers: Int
)

typealias CreateProxyResponse = ProxyInfo

typealias ReadProxyResponse = ProxyInfo

@Serializable
data class UpdateProxyRequest(
    val currentPlayers: Int?,
    val maxPlayers: Int?
)

typealias UpdateProxyResponse = ProxyInfo

@Serializable
data class ProxyHeartbeatRequest(
    val sentAt: Long
)

@Serializable
data class ProxyInfo(
    val id: String,
    val ip: String,
    val port: Int,
    val currentPlayers: Int,
    val maxPlayers: Int
)