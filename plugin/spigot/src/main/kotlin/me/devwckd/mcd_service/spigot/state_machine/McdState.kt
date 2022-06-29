package me.devwckd.mcd_service.spigot.state_machine

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import de.halfbit.comachine.dsl.ComachineBlock
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import me.devwckd.mcd_service.TransportCreatedEvent
import me.devwckd.mcd_service.bungee.util.*
import me.devwckd.mcd_service.spigot.McdServicePlugin
import me.devwckd.mcd_service.spigot.event.PlayerTransportRequestEvent
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.net.ConnectException

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

private val plugin by lazy { JavaPlugin.getPlugin(McdServicePlugin::class.java) }

sealed class McdState
object Connect : McdState()
class Active(val serverId: String) : McdState()
class Reconnect(val serverId: String? = null, val attempt: Int) : McdState()
class Disconnect(val serverId: String? = null, val exception: Throwable? = null, val shutdown: Boolean = true) :
    McdState()

fun ComachineBlock<McdState, McdEvent>.onConnect() = whenIn<Connect> {
    launchOnEnter {
        try {
            val id = httpClient.createServer("localhost", 25566, "")
            transitionTo { Active(id) }
        } catch (ex: Exception) {
            transitionTo { Reconnect(null, 5) }
        }
    }

    on<Shutdown> {
        transitionTo { Disconnect() }
    }
}

fun ComachineBlock<McdState, McdEvent>.onActive() = whenIn<Active> {
    launchOnEnter {
        try {
            Bukkit.getOnlinePlayers().forEach {
                httpClient.updatePlayer(state.serverId, it)
            }
            httpClient.updateServerCurrentPlayers(state.serverId, Bukkit.getOnlinePlayers().size)

            repeatWithDelay(1000) {
                httpClient.sendServerHeartbeat(state.serverId)
            }
        } catch (ex: Exception) {
            transitionTo { Reconnect(state.serverId, 5) }
        }
    }

    launchOnEnter {
        try {
            httpClient.subscribe(state.serverId, "TransportCreatedEvent").collect {
                when (it) {
                    is TransportCreatedEvent -> {
                        plugin.launch(plugin.asyncDispatcher) {
                            val transportInfo = it.transportInfo
                            plugin.logger.info("received transport for ${transportInfo.id}")

                            val player = Bukkit.getPlayer(transportInfo.playerId)
                            if (player == null) {
                                plugin.logger.warning("${transportInfo.id} is not on this server, retrying...")
                                httpClient.deleteTransport(transportInfo.id)
                                httpClient.createTransport(transportInfo.playerId, transportInfo.serverId)
                                return@launch
                            }

                            val playerTransportRequestEvent = PlayerTransportRequestEvent(player)
                            Bukkit.getPluginManager().callEvent(playerTransportRequestEvent)
                            if (playerTransportRequestEvent.isCancelled) {
                                httpClient.deleteTransport(transportInfo.id)
                                plugin.logger.warning("${transportInfo.id} transport failed")
                            } else {
                                httpClient.authorizeTransport(transportInfo.id)
                                plugin.logger.info("${transportInfo.id} transported")
                            }
                        }
                    }
                    else -> {}
                }
            }
        } catch (connectException: ConnectException) {
            transitionTo { Reconnect(state.serverId, 5) }
        } catch (exception: Throwable) {
            exception.printStackTrace()
        }
    }

    on<Shutdown> {
        transitionTo { Disconnect(state.serverId) }
    }

    on<PlayerConnect> {
        try {
            launchInState {
                httpClient.updateServerCurrentPlayers(state.serverId, Bukkit.getOnlinePlayers().size)
                httpClient.updatePlayer(state.serverId, it.player)
                httpClient.createTransport(it.player.uniqueId, state.serverId)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            it.player.kickPlayer("error")
        }
    }

    on<PlayerDisconnect> {
        launchInState {
            try {
                httpClient.updateServerCurrentPlayers(state.serverId, Bukkit.getOnlinePlayers().size)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}

fun ComachineBlock<McdState, McdEvent>.onReconnect() = whenIn<Reconnect> {
    onEnter {
        if (state.attempt <= 0) transitionTo { Disconnect(null) }

        launchInState {
            state.serverId?.let {
                try {
                    httpClient.deleteServer(it)
                } catch (_: Exception) {
                }
            }

            try {
                val id = httpClient.createServer("localhost", 25566, "test")
                transitionTo { Active(id) }
            } catch (ex: Exception) {
                delay(500)
                transitionTo { Reconnect(null, state.attempt - 1) }
            }
        }
    }

    on<Shutdown> {
        transitionTo { Disconnect() }
    }
}

fun ComachineBlock<McdState, McdEvent>.onDisconnect() = whenIn<Disconnect> {
    onEnter {
        state.exception?.printStackTrace()

        state.serverId?.let {
            launchInState {
                httpClient.deleteServer(it)
            }
        }

        if (state.shutdown) {
            println("shutdown")
            JavaPlugin.getPlugin(McdServicePlugin::class.java).server.shutdown()
        }
    }
}