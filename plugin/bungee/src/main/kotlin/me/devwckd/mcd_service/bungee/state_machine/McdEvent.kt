package me.devwckd.mcd_service.bungee.state_machine

import net.md_5.bungee.api.connection.ProxiedPlayer

sealed class McdEvent
class Shutdown(val fromDisconnect: Boolean = false) : McdEvent()
class PlayerConnect(val proxiedPlayer: ProxiedPlayer) : McdEvent()
class PlayerDisconnect(val proxiedPlayer: ProxiedPlayer) : McdEvent()