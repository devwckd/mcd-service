package me.devwckd.mcd_service.player

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun StatusPagesConfig.playerStatusPages() {
    exception<PlayerNotFoundException> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.orEmpty())
    }
}

class PlayerNotFoundException(_message: String) : Throwable(_message)