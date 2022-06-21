package me.devwckd.mcd_service

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import kotlinx.serialization.json.Json
import me.devwckd.mcd_service.app.plugins.configureMonitoring
import me.devwckd.mcd_service.app.plugins.configureRouting
import me.devwckd.mcd_service.app.plugins.configureSerialization
import me.devwckd.mcd_service.player.PlayerHandler
import me.devwckd.mcd_service.player.PlayerManager
import me.devwckd.mcd_service.proxy.ProxyHandler
import me.devwckd.mcd_service.proxy.ProxyManager
import me.devwckd.mcd_service.room.RoomHandler
import me.devwckd.mcd_service.room.RoomManager
import me.devwckd.mcd_service.server.ServerHandler
import me.devwckd.mcd_service.server.ServerManager
import me.devwckd.mcd_service.subscriber.SubscriberManager
import org.jetbrains.exposed.sql.Database
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import java.net.URI

fun main() {
    startKoin {
        slf4jLogger()
        modules(module {
            single { RoomManager() }
            single { ServerManager() }
            single { ProxyManager() }
            single { SubscriberManager() }
            single { PlayerManager() }

            single { RoomHandler(get(), get()) }
            single { ServerHandler(get(), get()) }
            single { ProxyHandler(get(), get()) }
            single { PlayerHandler(get(), get()) }

            single<Json> { Json }
        })
    }

    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureMonitoring()
    }.start(wait = true)
}