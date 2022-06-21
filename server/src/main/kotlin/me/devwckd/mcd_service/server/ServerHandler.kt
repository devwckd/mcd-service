package me.devwckd.mcd_service.server

import kotlinx.datetime.Clock
import me.devwckd.mcd_service.*
import me.devwckd.mcd_service.proxy.ProxyManager
import me.devwckd.mcd_service.room.RoomManager
import me.devwckd.mcd_service.subscriber.SubscriberManager
import me.devwckd.mcd_service.util.PaginationInfo
import me.devwckd.mcd_service.util.generateServerId
import kotlin.math.ceil

class ServerHandler(
    private val serverManager: ServerManager,
    private val subscriberManager: SubscriberManager,
) {

    fun listPaginated(
        paginationInfo: PaginationInfo,
    ): ListServersResponse {
        val (page, itemsPerPage) = paginationInfo
        val servers = serverManager.getAll()

        return servers
            .drop(page * itemsPerPage)
            .take(itemsPerPage)
            .map { server -> ServerInfo(server.id, server.ip, server.port, server.type) }
            .let {
                Paginated(page, ceil(servers.size.toDouble() / itemsPerPage).toInt(), itemsPerPage, it)
            }
    }

    suspend fun create(
        createServerRequest: CreateServerRequest
    ): CreateServerResponse {
        val server = Server(
            newServerId(),
            createServerRequest.ip,
            createServerRequest.port,
            createServerRequest.type.lowercase()
        )
        serverManager.put(server)

        val serverInfo = ServerInfo(server.id, server.ip, server.port, server.type)
        subscriberManager.broadcast(ServerCreatedEvent(serverInfo))

        return serverInfo
    }

    fun read(
        id: String
    ): ReadServerResponse {
        val server = serverManager.getById(id) ?: throw ServerNotFoundException("server with id $id not found.")
        return ServerInfo(server.id, server.ip, server.port, server.type)
    }

    fun heartbeat(
        id: String,
        serverHeartbeatRequest: ServerHeartbeatRequest
    ) {
        val server = serverManager.getById(id) ?: throw ServerNotFoundException("server with id $id not found.")

        server.serverHealth.pushTps(serverHeartbeatRequest.tps)
        server.serverHealth.pushHeartbeatPing(System.currentTimeMillis() - serverHeartbeatRequest.sentAt)
        server.serverHealth.lastHeartbeat = Clock.System.now()
    }

    suspend fun delete(
        id: String
    ) {
        val server = serverManager.getById(id) ?: throw ServerNotFoundException("server with id $id not found.")
        subscriberManager.broadcast(ServerDeletedEvent(ServerInfo(server.id, server.ip, server.port, server.type)))
        serverManager.remove(id)
    }

    private fun newServerId(): String {
        var id: String
        do {
            id = generateServerId()
        } while (serverManager.getById(id) != null)

        return id
    }

}