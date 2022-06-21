package me.devwckd.mcd_service

import kotlinx.serialization.Serializable

typealias ListRoomsResponse = Paginated<RoomInfo>

@Serializable
data class CreateRoomRequest(
    val maxPlayers: Int
)

typealias CreateRoomResponse = RoomInfo

@Serializable
data class RoomInfo(
    val id: String,
    val serverId: String,
)