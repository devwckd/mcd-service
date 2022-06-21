package me.devwckd.mcd_service.room

import me.devwckd.mcd_service.*
import me.devwckd.mcd_service.server.ServerManager
import me.devwckd.mcd_service.server.ServerNotFoundException
import me.devwckd.mcd_service.util.PaginationInfo
import me.devwckd.mcd_service.util.generateRoomId
import me.devwckd.mcd_service.util.generateServerId
import kotlin.math.ceil

class  RoomHandler(
    private val roomManager: RoomManager,
    private val serverManager: ServerManager
) {

    fun listPaginated(serverId: String, paginationInfo: PaginationInfo): ListRoomsResponse {
        val (page, itemsPerPage) = paginationInfo
        val rooms = roomManager.getAll()

        return rooms
            .filter { it.serverId == serverId }
            .drop(page * itemsPerPage)
            .take(itemsPerPage)
            .map { room ->
                RoomInfo(room.id, serverId)
            }.let {
                Paginated(page, ceil(rooms.size.toDouble() / itemsPerPage).toInt(), itemsPerPage, it)
            }
    }

    fun create(serverId: String, createRoomRequest: CreateRoomRequest): CreateRoomResponse {
        serverManager.getById(serverId) ?: throw ServerNotFoundException("could not find server with id $serverId")
        val room = Room(newRoomId(), serverId)
        roomManager.put(room)
        return RoomInfo(room.id, room.serverId)
    }

    private fun newRoomId(): String {
        var id: String
        do {
            id = generateRoomId()
        } while (roomManager.getById(id) != null)

        return id
    }

}