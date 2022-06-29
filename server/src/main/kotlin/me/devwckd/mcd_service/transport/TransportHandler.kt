package me.devwckd.mcd_service.transport

import me.devwckd.mcd_service.*
import me.devwckd.mcd_service.player.PlayerManager
import me.devwckd.mcd_service.player.PlayerNotFoundException
import me.devwckd.mcd_service.proxy.ProxyManager
import me.devwckd.mcd_service.proxy.ProxyNotFoundException
import me.devwckd.mcd_service.server.ServerManager
import me.devwckd.mcd_service.server.ServerNotFoundException
import me.devwckd.mcd_service.subscriber.SubscriberManager
import java.util.UUID

class TransportHandler(
    private val transportManager: TransportManager,
    private val playerManager: PlayerManager,
    private val proxyManager: ProxyManager,
    private val serverManager: ServerManager,
    private val subscriberManager: SubscriberManager,
) {
    suspend fun create(
        createTransportRequest: CreateTransportRequest
    ): CreateTransportResponse {
        val (playerTerm, serverId) = createTransportRequest
        val player =
            playerManager.search(playerTerm) ?: throw PlayerNotFoundException("player with term $playerTerm not found.")
        val server =
            serverManager.getById(serverId) ?: throw ServerNotFoundException("server with id $serverId not found.")

        val transport = Transport(
            UUID.randomUUID().toString(),
            player.id,
            server.id
        )
        transportManager.put(transport)

        val transportInfo = TransportInfo(
            transport.id,
            player.id,
            server.id,
            transport.createdAt
        )
        subscriberManager.sendToId(server.id, TransportCreatedEvent(transportInfo))

        return transportInfo
    }

    suspend fun authorize(
        id: String
    ) {
        val teleport = transportManager.getById(id) ?: throw TeleportNotFoundException("teleport with id $id not found.")

        val playerId = teleport.player
        val player =
            playerManager.getById(playerId) ?: throw PlayerNotFoundException("player with id $playerId not found.")

        val proxyId = player.proxyId
        val proxy =
            proxyManager.getById(player.proxyId) ?: throw ProxyNotFoundException("proxy with id $proxyId not found")

        val transportInfo = TransportInfo(
            teleport.id,
            player.id,
            teleport.server,
            teleport.createdAt
        )

        subscriberManager.sendToId(proxy.id, TransportAuthorizedEvent(transportInfo))
    }

    fun delete(
        id: String
    ) {
        val teleport = transportManager.getById(id) ?: throw TeleportNotFoundException("teleport with id $id not found.")
        transportManager.remove(teleport)
    }
}