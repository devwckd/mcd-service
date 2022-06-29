package me.devwckd.mcd_service.transport

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun StatusPagesConfig.teleportStatusPages() {
    exception<TeleportNotFoundException> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.orEmpty())
    }
}

class TeleportNotFoundException(_message: String) : Throwable(_message)