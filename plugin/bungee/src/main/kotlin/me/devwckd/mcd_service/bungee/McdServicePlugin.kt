package me.devwckd.mcd_service.bungee

import com.github.shynixn.mccoroutine.bungeecord.SuspendingPlugin
import com.github.shynixn.mccoroutine.bungeecord.launch
import com.tinder.StateMachine
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import me.devwckd.mcd_service.*
import me.devwckd.mcd_service.bungee.listener.GeneralListener
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ClientConnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.lang.management.ManagementFactory
import java.lang.management.MemoryUsage
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import kotlin.math.log

class McdServicePlugin : SuspendingPlugin(), Listener {

    val mcdClient = McdProxyClient(this)

    override suspend fun onEnableAsync() {
        cleanup()

        if(!mcdClient.create()) return
        mcdClient.startHeartbeatThread()

        mcdClient.fetchServers()
        mcdClient.startSubscriberThread()

        registerListeners()
    }

    override suspend fun onDisableAsync() {
        mcdClient.delete()
    }

    private fun cleanup() {
        proxy.servers.clear()
        proxy.config.listeners.forEach { it.serverPriority.clear() }
    }

    private fun registerListeners() {
        proxy.pluginManager.registerListener(this, GeneralListener(this))
    }

}