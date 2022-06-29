package me.devwckd.mcd_service.bungee.util

import me.devwckd.mcd_service.ServerInfo
import net.md_5.bungee.api.ProxyServer
import java.net.InetSocketAddress

fun ProxyServer.clearServers() {
    servers.clear()
    config.listeners.forEach {
        it.serverPriority.clear()
        it.forcedHosts.clear()
    }
}

fun ProxyServer.registerServer(serverInfo: ServerInfo) {
    servers[serverInfo.id] =
        constructServerInfo(serverInfo.id, InetSocketAddress(serverInfo.ip, serverInfo.port), "", false)
    config.listeners.forEach {
        it.serverPriority.add(serverInfo.id)
    }
}

fun ProxyServer.unregisterServer(serverInfo: ServerInfo) {
    servers.remove(serverInfo.id)
    config.listeners.forEach {
        it.serverPriority.remove(serverInfo.id)
    }
}