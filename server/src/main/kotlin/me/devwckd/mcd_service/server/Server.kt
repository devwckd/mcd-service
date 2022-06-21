package me.devwckd.mcd_service.server

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration


data class Server(
    val id: String,
    val ip: String,
    val port: Int,
    val type: String,
    val serverHealth: ServerHealth = ServerHealth(Instant.DISTANT_PAST)
)

data class ServerHealth(var lastHeartbeat: Instant) {
    private val tpsHistory = ArrayDeque<Double>()
    private val heartbeatPingHistory = ArrayDeque<Long>()

    val averageTps: Double
        get() = tpsHistory.average()

    val averageHeartbeatPing: Double
        get() = heartbeatPingHistory.average()

    val elapsedSinceLastHeartbeat: Duration
        get() = Clock.System.now() - lastHeartbeat

    fun pushTps(tps: Double) {
        tpsHistory.addFirst(tps)
        while (tpsHistory.size > 30) {
            tpsHistory.removeLast()
        }
    }

    fun pushHeartbeatPing(heartbeatPing: Long) {
        heartbeatPingHistory.addFirst(heartbeatPing)
        while (heartbeatPingHistory.size > 30) {
            heartbeatPingHistory.removeLast()
        }
    }

}