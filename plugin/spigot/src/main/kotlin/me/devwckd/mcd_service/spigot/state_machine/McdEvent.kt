package me.devwckd.mcd_service.spigot.state_machine

import org.bukkit.entity.Player

sealed class McdEvent
class Shutdown(val fromDisconnect: Boolean = false) : McdEvent()
class PlayerConnect(val player: Player) : McdEvent()
class PlayerDisconnect(val player: Player) : McdEvent()