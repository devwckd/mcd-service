package me.devwckd.mcd_service.player

import me.devwckd.mcd_service.*
import me.devwckd.mcd_service.proxy.ProxyNotFoundException
import me.devwckd.mcd_service.subscriber.SubscriberManager
import me.devwckd.mcd_service.util.PaginationInfo
import kotlin.math.ceil

class PlayerHandler(
    private val playerManager: PlayerManager,
    private val subscriberManager: SubscriberManager
) {

    fun listPaginated(
        paginationInfo: PaginationInfo,
    ): ListPlayersResponse {
        val (page, itemsPerPage) = paginationInfo
        val servers = playerManager.getAll()

        return servers
            .drop(page * itemsPerPage)
            .take(itemsPerPage)
            .map { player -> PlayerInfo(player.id, player.nickname, player.proxyId, player.serverId) }
            .let {
                Paginated(page, ceil(servers.size.toDouble() / itemsPerPage).toInt(), itemsPerPage, it)
            }
    }

    suspend fun create(
        createPlayerRequest: CreatePlayerRequest
    ): CreatePlayerResponse {
        val player =
            Player(createPlayerRequest.id, createPlayerRequest.nickname, createPlayerRequest.proxyId, null)
        playerManager.put(player)

        val playerInfo = PlayerInfo(player.id, player.nickname, player.proxyId, null)
        subscriberManager.broadcast(PlayerCreatedEvent(playerInfo))

        return playerInfo
    }

    fun read(
        term: String
    ): ReadPlayerResponse {
        val player = playerManager.search(term) ?: throw PlayerNotFoundException("player with term $term not found.")
        return PlayerInfo(player.id, player.nickname, player.proxyId, player.serverId)
    }

    suspend fun update(
        term: String,
        updatePlayerRequest: UpdatePlayerRequest
    ): UpdatePlayerResponse {
        val player = playerManager.search(term) ?: throw PlayerNotFoundException("player with term $term not found.")
        val (serverId) = updatePlayerRequest

        if (serverId != null) player.serverId = serverId

        val playerInfo = PlayerInfo(player.id, player.nickname, player.proxyId, player.serverId)
        subscriberManager.broadcast(PlayerEditedEvent(playerInfo))

        return playerInfo
    }

    suspend fun delete(
        term: String
    ) {
        val player = playerManager.search(term) ?: throw PlayerNotFoundException("player with term $term not found.")
        val playerInfo = PlayerInfo(player.id, player.nickname, player.proxyId, player.serverId)
        subscriberManager.broadcast(PlayerDeletedEvent(playerInfo))
    }

}