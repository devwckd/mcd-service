package me.devwckd.mcd_service.proxy

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

data class Proxy(
    val id: String,
    val ip: String,
    val port: Int,
    val proxyHealth: ProxyHealth = ProxyHealth(Instant.DISTANT_PAST),
    var webSocketServerSession: WebSocketServerSession? = null
)

data class ProxyHealth(var lastHeartbeat: Instant) {
    private val memoryHistory = ArrayDeque<Double>()
    private val heartbeatPingHistory = ArrayDeque<Long>()

    val elapsedSinceLastHeartbeat: Duration
        get() = Clock.System.now() - lastHeartbeat


    fun pushHeartbeatPing(heartbeatPing: Long) {
        heartbeatPingHistory.addFirst(heartbeatPing)
        while (heartbeatPingHistory.size > 30) {
            heartbeatPingHistory.removeLast()
        }
    }

}