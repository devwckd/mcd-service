package me.devwckd.mcd_service.spigot.listener

import kotlinx.coroutines.runBlocking
import me.devwckd.mcd_service.spigot.McdServicePlugin
import me.devwckd.mcd_service.spigot.state_machine.Active
import me.devwckd.mcd_service.spigot.state_machine.PlayerConnect
import me.devwckd.mcd_service.spigot.state_machine.PlayerDisconnect
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class GeneralListener(
    private val plugin: McdServicePlugin
) : Listener {

    @EventHandler
    fun join(event: PlayerLoginEvent) = runBlocking {
        if (plugin.currentState !is Active) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "could not join server")
            return@runBlocking
        }

        plugin.stateMachine.send(PlayerConnect(event.player))
    }

    @EventHandler
    fun quit(event: PlayerQuitEvent) = runBlocking {
        plugin.stateMachine.send(PlayerDisconnect(event.player))
    }

}