package me.devwckd.mcd_service

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

typealias ListPlayersResponse = Paginated<PlayerInfo>

@Serializable
data class CreatePlayerRequest(
    val id: @Contextual UUID,
    val nickname: String,
    val proxyId: String
)

typealias CreatePlayerResponse = PlayerInfo

typealias ReadPlayerResponse = PlayerInfo

@Serializable
data class UpdatePlayerRequest(
    val serverId: String?,
    val roomId: String?
)

typealias UpdatePlayerResponse = PlayerInfo

@Serializable
data class PlayerInfo(
    val id: @Contextual UUID,
    val nickname: String,
    val proxyId: String,
    val serverId: String?,
    val roomId: String?
)