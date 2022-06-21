package me.devwckd.mcd_service.subscriber

import io.ktor.server.websocket.*
import me.devwckd.mcd_service.Event

class SubscriberManager {
    private val subscribers = mutableListOf<Subscriber>()

    fun put(subscriber: Subscriber) {
        subscribers.add(subscriber)
    }

    fun remove(subscriber: Subscriber) {
        subscribers.remove(subscriber)
    }

    suspend fun broadcast(event: Event) {
        subscribers.filter {
            it.regex.matches(event::class.simpleName!!.also(::println))
        }.forEach {
            it.session.sendSerialized(event)
        }
    }
}