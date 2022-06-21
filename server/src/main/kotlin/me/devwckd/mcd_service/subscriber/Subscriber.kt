package me.devwckd.mcd_service.subscriber

import io.ktor.server.websocket.*
import java.util.concurrent.atomic.AtomicInteger

data class Subscriber(
    val regex: Regex,
    val session: DefaultWebSocketServerSession
) {
    companion object {
        var lastId = AtomicInteger(0)
    }
    val id = lastId.getAndIncrement()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Subscriber

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}