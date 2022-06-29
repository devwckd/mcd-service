package me.devwckd.mcd_service.bungee.state_machine

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
import me.devwckd.mcd_service.*
import me.devwckd.mcd_service.bungee.McdServicePlugin
import me.devwckd.mcd_service.bungee.util.*
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
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

sealed class McdState
object Connect : McdState()
class Active(val proxyId: String) : McdState()
class Reconnect(val proxyId: String?, val attempt: Int) : McdState()
class Disconnect(val proxyId: String? = null, val exception: Throwable? = null, val shutdown: Boolean = true) :
    McdState()

fun ComachineBlock<McdState, McdEvent>.onConnect() = whenIn<Connect> {
    launchOnEnter {
        try {
            val id = httpClient.createProxy("localhost", 25565)
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
            val proxyServer = ProxyServer.getInstance()
            proxyServer.clearServers()
            httpClient.listAllServers().items.forEach(proxyServer::registerServer)
        } catch (ex: Exception) {
            transitionTo { Disconnect(state.proxyId, ex) }
        }
    }

    launchOnEnter {
        try {
            ProxyServer.getInstance().players.forEach {
                httpClient.createPlayer(state.proxyId, it)
            }
            httpClient.updateProxyCurrentPlayers(state.proxyId, ProxyServer.getInstance().onlineCount)

            repeatWithDelay(1000) {
                httpClient.sendProxyHeartbeat(state.proxyId)
            }
        } catch (ex: Exception) {
            transitionTo { Reconnect(state.proxyId, 5) }
        }
    }

    launchOnEnter {
        try {
            httpClient.subscribe(state.proxyId, "Server(.*)Event|TransportAuthorizedEvent").collect {
                when (it) {
                    is ServerCreatedEvent -> ProxyServer.getInstance().registerServer(it.serverInfo)
                    is ServerDeletedEvent -> ProxyServer.getInstance().unregisterServer(it.serverInfo)
                    is TransportAuthorizedEvent -> {
                        val transportInfo = it.transportInfo
                        McdServicePlugin.LOGGER.info("received transport authorization for ${transportInfo.id}")

                        val player = ProxyServer.getInstance().getPlayer(transportInfo.playerId)
                        val serverInfo = ProxyServer.getInstance().getServerInfo(transportInfo.serverId)
                        if(player == null || serverInfo == null) {
                            McdServicePlugin.LOGGER.warning("player ${transportInfo.id} not found on this proxy")
                            httpClient.deleteTransport(transportInfo.id)
                            return@collect
                        }

                        try {
                            player.connectAsync(serverInfo)
                        } catch (_: Exception) {
                            McdServicePlugin.LOGGER.warning("could not teleport ${transportInfo.id} to the server ${serverInfo.name}")
                        }

                        httpClient.deleteTransport(transportInfo.id)
                    }
                    else -> {}
                }
            }
        } catch (connectException: ConnectException) {
            transitionTo { Reconnect(state.proxyId, 5) }
        } catch (exception: Throwable) {
            McdServicePlugin.LOGGER.warning("error on subscriber thread: ${exception.message}")
        }
    }

    on<Shutdown> {
        transitionTo { Disconnect(state.proxyId) }
    }

    on<PlayerConnect> {
        launchInState {
            try {
                httpClient.createPlayer(state.proxyId, it.proxiedPlayer)
                httpClient.updateProxyCurrentPlayers(state.proxyId, ProxyServer.getInstance().onlineCount)
                } catch (ex: Exception) {
                ex.printStackTrace()
                it.proxiedPlayer.disconnect(TextComponent("error"))
            }
        }
    }

    on<PlayerDisconnect> {
        launchInState {
            try {
                httpClient.deletePlayer(it.proxiedPlayer)
                httpClient.updateProxyCurrentPlayers(state.proxyId, ProxyServer.getInstance().onlineCount)
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
            state.proxyId?.let {
                try {
                    httpClient.deleteProxy(it)
                } catch (_: Exception) {}
            }

            try {
                val id = httpClient.createProxy("localhost", 25565)
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

        state.proxyId?.let {
            launchInState {
                httpClient.deleteProxy(it)
            }
        }

        if (state.shutdown) {
            ProxyServer.getInstance().stop()
        }
    }
}