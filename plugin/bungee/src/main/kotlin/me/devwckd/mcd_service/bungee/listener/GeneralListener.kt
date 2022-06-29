package me.devwckd.mcd_service.bungee.listener

import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import me.devwckd.mcd_service.bungee.McdServicePlugin
import me.devwckd.mcd_service.bungee.state_machine.Active
import me.devwckd.mcd_service.bungee.state_machine.PlayerConnect
import me.devwckd.mcd_service.bungee.state_machine.PlayerDisconnect
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class GeneralListener(private val plugin: McdServicePlugin) : Listener {

    @EventHandler
    fun onJoin(event: ServerConnectEvent) {
        event.target = plugin.proxy.servers.values.firstOrNull()
            ?: return event.player.disconnect(TextComponent("no server found"))
    }

    @EventHandler
    fun preLogin(event: PreLoginEvent) {
        if (plugin.proxy.servers.isEmpty() || plugin.currentState !is Active) {
            event.setCancelReason(TextComponent("no server found!"))
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPostLogin(event: PostLoginEvent) {
        runBlocking {
            plugin.stateMachine.send(PlayerConnect(event.player))
        }
    }

    @EventHandler
    fun onQuit(event: PlayerDisconnectEvent) {
        runBlocking {
            plugin.stateMachine.send(PlayerDisconnect(event.player))
        }
    }

}