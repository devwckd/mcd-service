package me.devwckd.mcd_service.subscriber

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.ktor.ext.inject

fun Route.subscriberRoutes() {
    val subscriberManager: SubscriberManager by inject()

    route("subscribers") {
        webSocket {
            val regex = call.request.queryParameters["filter"]?.toRegex() ?: ".*".toRegex()
            val subscriber = Subscriber(regex, this)
            subscriberManager.put(subscriber)

            try {
                for(message in incoming) {
                    application.log.info(message.toString())
                }
            } finally {
                subscriberManager.remove(subscriber)
            }
        }
    }
}