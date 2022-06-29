package me.devwckd.mcd_service.bungee.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.flow
import me.devwckd.mcd_service.*
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

fun noTimeoutRequestBuilder(block: HttpRequestBuilder.() -> Unit): HttpRequestBuilder.() -> Unit = {
    timeout {
        connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        socketTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
    }
    block(this)
}

suspend fun HttpClient.createProxy(ip: String, port: Int) = post(mcdUrl("proxies")) {
    contentType(ContentType.Application.Json)
    setBody(CreateProxyRequest(ip, port, ProxyServer.getInstance().config.playerLimit))
}.body<CreateProxyResponse>().id

suspend fun HttpClient.updateProxyCurrentPlayers(id: String, currentPlayers: Int) = put(mcdUrl("proxies/$id")) {
    contentType(ContentType.Application.Json)
    setBody(UpdateProxyRequest(currentPlayers, null))
}

suspend fun HttpClient.deleteProxy(id: String) = delete(mcdUrl("proxies/$id"))

suspend fun HttpClient.createPlayer(id: String, proxiedPlayer: ProxiedPlayer) = post(mcdUrl("players")) {
    contentType(ContentType.Application.Json)
    setBody(CreatePlayerRequest(proxiedPlayer.uniqueId, proxiedPlayer.name, id))
}

suspend fun HttpClient.deletePlayer(proxiedPlayer: ProxiedPlayer) = delete(mcdUrl("players/${proxiedPlayer.uniqueId}"))

suspend fun HttpClient.sendProxyHeartbeat(id: String) = post(mcdUrl("proxies/$id/heartbeat")) {
    contentType(ContentType.Application.Json)
    setBody(ProxyHeartbeatRequest(System.currentTimeMillis()))
}

suspend fun HttpClient.listAllServers() = get(mcdUrl("servers?itemsPerPage=100000")).body<ListServersResponse>()

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
        } catch (_: Exception) {
        }
    }
}

suspend fun HttpClient.createTransport(playerId: UUID, serverId: String) = post(mcdUrl("transports")) {
    contentType(ContentType.Application.Json)
    setBody(CreateTransportRequest(playerId.toString(), serverId))
}

suspend fun HttpClient.authorizeTransport(id: String) = get(mcdUrl("transports/${id}/authorize"))

suspend fun HttpClient.deleteTransport(id: String) = delete(mcdUrl("transports/${id}"))