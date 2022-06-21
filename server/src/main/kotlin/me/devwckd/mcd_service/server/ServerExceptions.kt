package me.devwckd.mcd_service.server

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun StatusPagesConfig.serverStatusPages() {
    exception<ServerNotFoundException> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.orEmpty())
    }
}

class ServerNotFoundException(_message: String) : Throwable(_message)