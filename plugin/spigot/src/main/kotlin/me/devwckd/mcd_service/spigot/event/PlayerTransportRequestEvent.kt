package me.devwckd.mcd_service.spigot.event

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList


class PlayerTransportRequestEvent(
    val player: Player
) : Event(true), Cancellable {

    private var isCancelled = false

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList

    override fun isCancelled() = isCancelled

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }

}