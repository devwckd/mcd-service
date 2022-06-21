package me.devwckd.mcd_service.room

class RoomManager {
    private val rooms = hashMapOf<String, Room>()

    fun getAll(): Collection<Room> {
        return rooms.values
    }

    fun getById(id: String) = rooms[id]

    fun getByServerId(serverId: String): Collection<Room> {
        return getAll().filter { it.serverId == serverId }
    }

    fun put(room: Room) {
        rooms[room.id] = room
    }
}