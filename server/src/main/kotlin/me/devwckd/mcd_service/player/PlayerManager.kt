package me.devwckd.mcd_service.player

import java.util.UUID

class PlayerManager {
    private val playersById = mutableMapOf<UUID, Player>()
    private val playersByLowercaseNickname = mutableMapOf<String, Player>()

    fun getAll(): Collection<Player> {
        return playersById.values
    }

    fun search(term: String): Player? {
        return if (term.length > 16)
            playersById[UUID.fromString(term)]
        else
            playersByLowercaseNickname[term.lowercase()]
    }

    fun getById(id: UUID): Player? {
        return playersById[id]
    }

    fun getByNickname(nickname: String): Player? {
        return playersByLowercaseNickname[nickname.lowercase()]
    }

    fun put(player: Player) {
        playersById[player.id] = player
        playersByLowercaseNickname[player.nickname.lowercase()] = player
    }

    fun remove(id: UUID) {
        playersById.remove(id)?.let {
            playersByLowercaseNickname.remove(it.nickname.lowercase())
        }
    }
}