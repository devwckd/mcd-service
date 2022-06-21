package me.devwckd.mcd_service.bungee.listener

import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler

class GeneralListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onJoin(event: ServerConnectEvent) {
        event.target = plugin.proxy.servers.values.firstOrNull()
            ?: return event.player.disconnect(TextComponent("no server found"))
    }

    @EventHandler
    fun preLogin(event: PreLoginEvent) {
        if (plugin.proxy.servers.isEmpty()) {
            event.setCancelReason(TextComponent("no server found!"))
            event.isCancelled = true
        }
    }

}