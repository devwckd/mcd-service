package me.devwckd.mcd_service.proxy

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun StatusPagesConfig.proxyStatusPages() {
    exception<ProxyNotFoundException> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.orEmpty())
    }
}

class ProxyNotFoundException(_message: String) : Throwable(_message)