package me.devwckd.mcd_service.bungee.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.flow
import me.devwckd.mcd_service.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

fun noTimeoutRequestBuilder(block: HttpRequestBuilder.() -> Unit): HttpRequestBuilder.() -> Unit = {
    timeout {
        connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        socketTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
    }
    block(this)
}

suspend fun HttpClient.createServer(ip: String, port: Int, type: String) = post(mcdUrl("servers")) {
    contentType(ContentType.Application.Json)
    setBody(CreateServerRequest(ip, port, type, Bukkit.getMaxPlayers()))
}.body<CreateServerResponse>().id

suspend fun HttpClient.updateServerCurrentPlayers(id: String, currentPlayers: Int) = put(mcdUrl("servers/$id")) {
    contentType(ContentType.Application.Json)
    setBody(UpdateServerRequest(currentPlayers, null))
}

suspend fun HttpClient.updatePlayer(id: String, player: Player) = put(mcdUrl("players/${player.uniqueId}")) {
    contentType(ContentType.Application.Json)
    setBody(UpdatePlayerRequest(id))
}

suspend fun HttpClient.deleteServer(id: String) = delete(mcdUrl("servers/$id"))

suspend fun HttpClient.sendServerHeartbeat(id: String) = post(mcdUrl("servers/$id/heartbeat")) {
    contentType(ContentType.Application.Json)
    setBody(ServerHeartbeatRequest(20.0, System.currentTimeMillis()))
}

suspend fun HttpClient.subscribe(id: String?, filter: String?) = flow {
    webSocket(
        host = "localhost",
        port = 8080,
        path = "/subscribers",
        request = noTimeoutRequestBuilder {
            filter?.let{ parameter("filter", it) }
            id?.let { parameter("id", it) }
        }
    ) {
        try {
            while (true) {
                val event = receiveDeserialized<Event>()
                emit(event)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

suspend fun HttpClient.createTransport(playerId: UUID, serverId: String) = post(mcdUrl("transports")) {
    contentType(ContentType.Application.Json)
    setBody(CreateTransportRequest(playerId.toString(), serverId))
}

suspend fun HttpClient.authorizeTransport(id: String) = get(mcdUrl("transports/${id}/authorize"))

suspend fun HttpClient.deleteTransport(id: String) = delete(mcdUrl("transports/${id}"))