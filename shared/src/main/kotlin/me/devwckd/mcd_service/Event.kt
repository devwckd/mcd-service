package me.devwckd.mcd_service

import kotlinx.serialization.Serializable

@Serializable
sealed class Event

@Serializable
sealed class ServerEvent: Event() {
    abstract val serverInfo: ServerInfo
}

@Serializable
class ServerCreatedEvent(
    override val serverInfo: ServerInfo
) : ServerEvent()

@Serializable
class ServerDeletedEvent(
    override val serverInfo: ServerInfo
) : ServerEvent()

@Serializable
sealed class ProxyEvent : Event() {
    abstract val proxyInfo: ProxyInfo
}

@Serializable
class ProxyCreatedEvent(
    override val proxyInfo: ProxyInfo
) : ProxyEvent()

@Serializable
class ProxyDeletedEvent(
    override val proxyInfo: ProxyInfo
) : ProxyEvent()

@Serializable
sealed class PlayerEvent: Event() {
    abstract val playerInfo: PlayerInfo
}

@Serializable
class PlayerCreatedEvent(
    override val playerInfo: PlayerInfo
) : PlayerEvent()

@Serializable
class PlayerEditedEvent(
    override val playerInfo: PlayerInfo
) : PlayerEvent()

@Serializable
class PlayerDeletedEvent(
    override val playerInfo: PlayerInfo
) : PlayerEvent()