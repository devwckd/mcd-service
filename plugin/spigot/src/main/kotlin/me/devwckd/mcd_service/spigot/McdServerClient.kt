package me.devwckd.mcd_service.spigot

import com.github.shynixn.mccoroutine.bukkit.launch
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import me.devwckd.mcd_service.*
import org.bukkit.plugin.java.JavaPlugin
import java.net.ConnectException

class McdServerClient(
    val plugin: JavaPlugin
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
            val body = httpClient.post("http://localhost:8080/servers") {
                contentType(ContentType.Application.Json)
                setBody(CreateServerRequest("localhost", 25566, "test"))
            }.body<CreateProxyResponse>()

            id = body.id
            true
        } catch (exception: ConnectException) {
            plugin.logger.severe("Couldn't connect to mcd service, shutting down...")
            plugin.server.shutdown()
            false
        }
    }

    fun startHeartbeatThread() {
        plugin.launch {
            while (true) {
                delay(1000)

                httpClient.post("http://localhost:8080/servers/$id/heartbeat") {
                    contentType(ContentType.Application.Json)
                    setBody(ProxyHeartbeatRequest(System.currentTimeMillis()))
                }
            }
        }
    }

    suspend fun delete() {
        if (id == null) return
        httpClient.delete("http://localhost:8080/servers/$id")
    }

}