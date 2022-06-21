package me.devwckd.mcd_service.bungee

import com.github.shynixn.mccoroutine.bungeecord.launch
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.devwckd.mcd_service.*
import me.devwckd.mcd_service.bungee.util.noTimeoutRequestBuilder
import net.md_5.bungee.api.plugin.Plugin
import java.lang.management.ManagementFactory
import java.lang.management.MemoryUsage
import java.net.ConnectException
import java.net.InetSocketAddress
import java.time.Duration
import java.time.Instant

class McdProxyClient(
    val plugin: Plugin
) {

    private var id: String? = null

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets) {
            pingInterval = -1
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(HttpTimeout)
    }

    suspend fun create(): Boolean {
        return try {
            val body = httpClient.post("http://localhost:8080/proxies") {
                contentType(ContentType.Application.Json)
                setBody(CreateProxyRequest("localhost", 25565))
            }.body<CreateProxyResponse>()

            id = body.id
            true
        } catch (exception: ConnectException) {
            plugin.logger.severe("Couldn't connect to mcd service, shutting down...")
            plugin.proxy.stop()
            false
        }
    }

    suspend fun fetchServers() {
        val body = httpClient.get("http://localhost:8080/servers").body<ListServersResponse>()

        body.items.forEach {
            registerServer(it.id, it.ip, it.port)
        }
    }

    fun startHeartbeatThread() {
        plugin.launch {
            while (true) {
                delay(1000)

                httpClient.post("http://localhost:8080/proxies/$id/heartbeat") {
                    contentType(ContentType.Application.Json)
                    setBody(ProxyHeartbeatRequest(System.currentTimeMillis()))
                }
            }
        }
    }

    fun startSubscriberThread() {
        try {
            plugin.launch {
                httpClient.webSocket(
                    host = "localhost",
                    port = 8080,
                    path = "/subscribers?filter=ServerCreatedEvent|ServerDeletedEvent",
                    request = noTimeoutRequestBuilder()
                ) {
                    while (true) {
                        when (val event = receiveDeserialized<Event>()) {
                            is ServerCreatedEvent -> {
                                val (id, ip, port) = event.serverInfo
                                registerServer(id, ip, port)
                            }
                            is ServerDeletedEvent -> {
                                val id = event.serverInfo.id
                                unregisterServer(id)
                            }
                            else -> {}
                        }
                    }
                }
            }
        } catch (exception: Throwable) {
            exception.printStackTrace()
            startSubscriberThread()
        }
    }

    suspend fun delete() {
        if (id == null) return
        httpClient.delete("http://localhost:8080/proxies/$id")
    }

    private fun registerServer(id: String, ip: String, port: Int) {
        plugin.proxy.servers[id] = plugin.proxy.constructServerInfo(id, InetSocketAddress(ip, port), "", false)
        plugin.proxy.config.listeners.forEach { listenerInfo ->
            listenerInfo.serverPriority.add(id)
        }
    }

    private fun unregisterServer(id: String) {
        plugin.proxy.servers.remove(id)
    }

}