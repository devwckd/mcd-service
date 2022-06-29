package me.devwckd.mcd_service

import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.serialization.json.Json
import me.devwckd.mcd_service.app.plugins.configureMonitoring
import me.devwckd.mcd_service.app.plugins.configureRouting
import me.devwckd.mcd_service.app.plugins.configureSerialization
import me.devwckd.mcd_service.player.PlayerHandler
import me.devwckd.mcd_service.player.PlayerManager
import me.devwckd.mcd_service.proxy.ProxyHandler
import me.devwckd.mcd_service.proxy.ProxyManager
import me.devwckd.mcd_service.server.ServerHandler
import me.devwckd.mcd_service.server.ServerManager
import me.devwckd.mcd_service.subscriber.SubscriberManager
import me.devwckd.mcd_service.transport.TransportHandler
import me.devwckd.mcd_service.transport.TransportManager
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger

fun main() {
    startKoin {
        slf4jLogger()
        modules(module {
            single { ServerManager() }
            single { ProxyManager() }
            single { SubscriberManager() }
            single { PlayerManager() }
            single { TransportManager() }

            single { ServerHandler(get(), get()) }
            single { ProxyHandler(get(), get()) }
            single { PlayerHandler(get(), get()) }
            single { TransportHandler(get(), get(), get(), get(), get()) }

            single<Json> { Json }
        })
    }

    WatchDog().start()

    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureMonitoring()
    }.start(wait = true)
}