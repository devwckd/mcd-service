package me.devwckd.mcd_service.app.plugins

import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import me.devwckd.mcd_service.player.playerRoutes
import me.devwckd.mcd_service.player.playerStatusPages
import me.devwckd.mcd_service.proxy.proxyRoutes
import me.devwckd.mcd_service.proxy.proxyStatusPages
import me.devwckd.mcd_service.server.serverRoutes
import me.devwckd.mcd_service.server.serverStatusPages
import me.devwckd.mcd_service.subscriber.subscriberRoutes
import org.koin.ktor.ext.inject
import java.net.http.WebSocket
import java.time.Duration

fun Application.configureRouting() {
    val json: Json by inject()

    install(StatusPages) {
        serverStatusPages()
        proxyStatusPages()
        playerStatusPages()
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(json)
    }

    routing {
        serverRoutes()
        proxyRoutes()
        subscriberRoutes()
        playerRoutes()
    }
}
