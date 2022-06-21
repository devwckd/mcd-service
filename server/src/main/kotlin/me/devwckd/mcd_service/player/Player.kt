package me.devwckd.mcd_service.player

import java.util.UUID

data class Player(
    val id: UUID,
    val nickname: String,
    val proxyId: String,
    var serverId: String?,
    var roomId: String?
)